/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.message.Message;
import com.sj.im.codec.pack.LoginPack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.enums.command.SystemCommand;
import com.sj.im.common.model.UserSession;
import com.sj.im.tcp.redis.RedisManager;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 事件处理器
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Integer command = msg.getMessageHeader().getCommand();
        // 登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGIN.getCommand())) {
            LoginPack loginPack = JSONUtil.toBean(JSONUtil.toJsonStr(msg.getMessagePack()), LoginPack.class, true);
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.USERID)).set(loginPack.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.APPID)).set(msg.getMessageHeader().getAppId());
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).set(msg.getMessageHeader().getClientType());

            // 将channel存起来
            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            // 存到redis
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<Integer, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + RedisConstants.USER_SESSION + loginPack.getUserId());
            map.put(msg.getMessageHeader().getClientType(), JSONUtil.toJsonStr(userSession));

            SessionSocketHolder.put(msg.getMessageHeader().getAppId(), loginPack.getUserId(), msg.getMessageHeader().getClientType(), (NioSocketChannel) ctx.channel());
        }

        // 退出登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGOUT.getCommand())) {
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        }

        // 心跳command
        if (ObjectUtil.equal(command, SystemCommand.PING.getCommand())) {
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.READTIME)).set(System.currentTimeMillis());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

    }
}
