/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.LoginPack;
import com.sj.im.codec.pack.message.ChatMessageAck;
import com.sj.im.codec.pack.user.LoginAckPack;
import com.sj.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.sj.im.codec.proto.Message;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.enums.command.SystemCommand;
import com.sj.im.common.enums.command.UserEventCommand;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.common.model.UserSession;
import com.sj.im.common.model.message.CheckSendMessageReq;
import com.sj.im.tcp.config.TcpConfig;
import com.sj.im.tcp.feign.FeignMessageService;
import com.sj.im.tcp.publish.MqMessageProducer;
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
    private final TcpConfig nettyConfig;
    private final RedissonClient redissonClient;
    private final MqMessageProducer mqMessageProducer;
    private final FeignMessageService feignMessageService;

    public NettyServerHandler(RedissonClient redissonClient, FeignMessageService feignMessageService, TcpConfig nettyConfig, MqMessageProducer mqMessageProducer) {
        this.redissonClient = redissonClient;
        this.nettyConfig = nettyConfig;
        this.feignMessageService = feignMessageService;
        this.mqMessageProducer = mqMessageProducer;
    }

    /**
     * 处理接收到的消息
     *
     * @param ctx ChannelHandlerContext对象，表示通道处理的上下文环境
     * @param msg 表示接收到的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        // 获取消息指令
        Integer command = msg.getMessageHeader().getCommand();

        // 登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGIN.getCommand())) {
            processLoginEvent(ctx, msg);
            return;
        }

        // 退出登录command
        if (ObjectUtil.equal(command, SystemCommand.LOGOUT.getCommand())) {
            // 从系统中移除用户会话，包括从SessionSocketHolder、Redis中移除会话并发送用户状态更改通知
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
            return;
        }

        // 心跳command
        if (ObjectUtil.equal(command, SystemCommand.PING.getCommand())) {
            ctx.channel().attr(AttributeKey.valueOf(TcpConstants.READTIME)).set(System.currentTimeMillis());
            return;
        }

        // 聊天command
        if (ObjectUtil.equal(command, MessageCommand.MSG_P2P.getCommand()) || ObjectUtil.equal(command, GroupEventCommand.MSG_GROUP.getCommand())) {
            processMsgEvent(ctx, msg, command);
            return;
        }

        // 默认将消息发送到消息队列
        mqMessageProducer.sendMessage(msg, command);
    }

    /**
     * 处理登录事件
     *
     * @param ctx ChannelHandlerContext对象，表示通道处理的上下文环境
     * @param msg 表示接收到的消息
     */
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
        userSession.setBrokerId(nettyConfig.getBrokerId());
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            userSession.setBrokerHost(hostAddress);
        } catch (Exception e) {
            log.error("获取主机地址失败: {}", e.getMessage());
        }

        // 存到redis
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        map.put(clientType + ":" + imei, JSONUtil.toJsonStr(userSession));

        // 将用户ID和通道对象绑定到自定义的SessionSocketHolder中
        SessionSocketHolder.put(appId, userId, clientType, imei, (NioSocketChannel) ctx.channel());

        // 用户上线的相关信息，发送给其他服务端
        UserClientDto dto = new UserClientDto();
        dto.setUserId(userId);
        dto.setAppId(appId);
        dto.setImei(imei);
        dto.setClientType(clientType);
        // 通过redissonClient获取一个RTopic对象，并指定频道为UserLoginChannel
        RTopic topic = redissonClient.getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        // 记录日志
        log.info("发送用户上线通知：{}", JSONUtil.toJsonStr(dto));
        // 将dto对象序列化为JSON格式，并发送到UserLoginChannel频道中
        topic.publish(JSONUtil.toJsonStr(dto));

        // 用户状态变更的相关信息，发送给消息队列
        UserStatusChangeNotifyPack pack = new UserStatusChangeNotifyPack();
        pack.setAppId(appId);
        pack.setUserId(userId);
        pack.setStatus(ImConnectStatusEnum.ONLINE_STATUS.getCode());
        // 发送用户状态变更消息
        JSONObject json = mqMessageProducer.sendMessage(pack, msg.getMessageHeader(), UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand());
        log.info("发送用户状态变更通知：{}", json.toString());

        // 给客户端回复登录ack
        MessagePack<LoginAckPack> loginSuccess = new MessagePack<>();
        // 设置 userId 属性为登录请求包中的 userId
        LoginAckPack loginAckPack = new LoginAckPack();
        loginAckPack.setUserId(userId);
        // 设置 MessagePack 对象的命令为登录成功的命令，并将 LoginAckPack 对象设置为数据
        loginSuccess.setCommand(SystemCommand.LOGIN_ACK.getCommand());
        // 设置命令描述信息
        loginSuccess.setDesc(SystemCommand.LOGIN_ACK.getDesc());
        loginSuccess.setData(loginAckPack);
        // 设置 MessagePack 对象的 IMEI 和 AppId 属性为收到的消息的对应属性
        loginSuccess.setImei(imei);
        loginSuccess.setAppId(appId);
        // 将 MessagePack 对象写入并刷新到通道中
        ctx.channel().writeAndFlush(loginSuccess);
    }

    /**
     * 处理消息发送事件
     *
     * @param ctx ChannelHandlerContext对象，表示通道处理的上下文环境
     * @param msg 表示接收到的消息
     */
    private void processMsgEvent(ChannelHandlerContext ctx, Message msg, Integer command) {
        try {
            // 获取消息的发送者和接收者 ID，并创建 CheckSendMessageReq 对象
            CheckSendMessageReq req = new CheckSendMessageReq();
            req.setAppId(msg.getMessageHeader().getAppId());
            req.setCommand(msg.getMessageHeader().getCommand());

            JSONObject jsonObject = JSONUtil.parseObj(msg.getMessagePack());
            req.setFromId(jsonObject.getStr("fromId"));
            if (ObjectUtil.equal(command, MessageCommand.MSG_P2P.getCommand())) {
                req.setToId(jsonObject.getStr("toId"));
            } else {
                req.setToId(jsonObject.getStr("groupId"));
            }

            // 调用 Feign 客户端接口，检查是否可以发送消息
            ResponseVO<Object> response = feignMessageService.checkSendMessage(req);
            // 如果检查通过，则将消息发送到消息队列
            if (response.isOk()) {
                mqMessageProducer.sendMessage(msg, command);
            } else { // 否则，将检查结果返回给客户端
                MessagePack<ResponseVO<Object>> ack = new MessagePack<>();
                if (ObjectUtil.equal(command, MessageCommand.MSG_P2P.getCommand())) {
                    ack.setCommand(MessageCommand.MSG_ACK.getCommand());
                } else {
                    ack.setCommand(GroupEventCommand.GROUP_MSG_ACK.getCommand());
                }

                // 创建 ChatMessageAck 对象，并将检查结果封装为 RestResponse 对象
                ChatMessageAck chatMessageAck = new ChatMessageAck(jsonObject.getStr("messageId"));
                response.setData(chatMessageAck);
                ack.setData(response);
                // 将检查结果发送给客户端
                ctx.channel().writeAndFlush(ack);
            }
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
        }
    }
}
