/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.Message;
import com.sj.im.codec.pack.LoginPack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.enums.command.SystemCommand;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.common.model.UserSession;
import com.sj.im.tcp.publish.MqMessageProducer;
import com.sj.im.tcp.redis.RedisManager;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.net.InetAddress;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 事件处理器
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    private final Integer brokerId;

    private String logicUrl;

    public NettyServerHandler(Integer brokerId){
        this.brokerId = brokerId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Integer command = msg.getMessageHeader().getCommand();

        // 登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGIN.getCommand())) {
            processLoginEvent(ctx, msg);
            return;
        }

        // 退出登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGOUT.getCommand())) {
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
            return;
        }

        // 心跳command
        if (ObjectUtil.equal(command, SystemCommand.PING.getCommand())) {
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.READTIME)).set(System.currentTimeMillis());
            return;
        }

        MqMessageProducer.sendMessage(msg, command);
    }

    private void processLoginEvent(ChannelHandlerContext ctx, Message msg) {
        // 从messagePack中解析出来
        LoginPack loginPack = JSONUtil.toBean(JSONUtil.toJsonStr(msg.getMessagePack()), LoginPack.class, true);
        String userId = loginPack.getUserId();
        Integer appId = msg.getMessageHeader().getAppId();
        Integer clientType = msg.getMessageHeader().getClientType();
        String imei = msg.getMessageHeader().getImei();

        // 给channel设置一下属性，方便于后面退出登录
        ctx.channel().attr(AttributeKey.valueOf(TcpConstants.USERID)).set(userId);
        ctx.channel().attr(AttributeKey.valueOf(TcpConstants.APPID)).set(appId);
        ctx.channel().attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).set(clientType);
        ctx.channel().attr(AttributeKey.valueOf(TcpConstants.IMEI)).set(imei);

        // redis map
        UserSession userSession = new UserSession();
        userSession.setAppId(appId);
        userSession.setClientType(clientType);
        userSession.setUserId(userId);
        userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
        userSession.setImei(imei);

        // 这里的brokerId 和 brokerHost是来区分 分布式的server
        userSession.setBrokerId(brokerId);
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            userSession.setBrokerHost(hostAddress);
        }catch (Exception e){
            log.error("获取主机地址失败: {}", e.getMessage());
        }

        // 存到redis
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        map.put(clientType + ":" + imei, JSONUtil.toJsonStr(userSession));
        SessionSocketHolder.put(appId, userId, clientType, imei, (NioSocketChannel) ctx.channel());

        UserClientDto dto = new UserClientDto();
        dto.setUserId(userId);
        dto.setAppId(appId);
        dto.setImei(imei);
        dto.setClientType(clientType);
        // 通过redis将用户信息广播给其他服务，通知其他服务该用户已登录
        RTopic topic = redissonClient.getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        topic.publish(JSONUtil.toJsonStr(dto));
    }
}
