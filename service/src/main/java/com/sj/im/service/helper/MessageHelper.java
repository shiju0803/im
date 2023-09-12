/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.common.constant.RabbitConstants;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户行为消息发送
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class MessageHelper {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserSessionHelper userSessionHelper;

    /**
     * 发送消息到 RabbitMQ
     *
     * @param session 用户会话
     * @param msg     消息内容
     * @return 发送成功返回 true，发送失败返回 false
     */
    private boolean sendMessage(UserSession session, Object msg) {
        try {
            log.info("向MQ发送消息: {}", msg);
            rabbitTemplate.convertAndSend(RabbitConstants.IM_EXCHANGE, String.valueOf(session.getBrokerId()), msg);
            return true;
        } catch (Exception e) {
            log.error("向MQ发送消息失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 包装数据并调用 sendMessage 方法发送消息到 RabbitMQ
     *
     * @param toId    接收消息的用户 ID
     * @param command 消息命令
     * @param msg     消息内容
     * @param session 用户会话
     * @return 发送成功返回 true，发送失败返回 false
     */
    private boolean sendPack(String toId, Command command, Object msg, UserSession session) {
        // 创建消息包对象
        MessagePack<Object> messagePack = new MessagePack<>();
        // 设置消息命令
        messagePack.setCommand(command.getCommand());
        // 设置命令名称
        messagePack.setDesc(command.getDesc());
        // 设置接收消息的用户 ID
        messagePack.setToId(toId);
        // 设置客户端类型
        messagePack.setClientType(session.getClientType());
        // 设置应用 ID
        messagePack.setAppId(session.getAppId());
        // 设置设备 IMEI
        messagePack.setImei(session.getImei());
        // 将消息内容转换为 JSON 对象
        JSONObject data = JSONUtil.parseObj(msg);
        messagePack.setData(data);
        // 将消息包转换为 JSON 字符串
        String msgBody = JSONUtil.toJsonStr(messagePack);
        // 调用 sendMessage 方法发送消息到 RabbitMQ
        return sendMessage(session, msgBody);
    }

    /**
     * 发送消息给指定用户的所有客户端
     *
     * @param toId    接收消息的用户 ID
     * @param command 消息命令
     * @param data    消息内容
     * @param appId   应用 ID
     * @return 发送成功的客户端信息列表
     */
    public List<ClientInfo> sendToUser(String toId, Command command, Object data, Integer appId) {
        // 获取指定用户的所有会话信息
        List<UserSession> userSessionList = userSessionHelper.getUserSessionList(appId, toId);
        // 创建发送成功的客户端信息列表
        List<ClientInfo> list = new ArrayList<>();
        for (UserSession session : userSessionList) {
            // 调用 sendPack 方法发送消息
            boolean b = sendPack(toId, command, data, session);
            if (b) {
                // 如果发送成功 将客户端信息添加到发送成功的客户端信息列表中
                list.add(new ClientInfo(session.getAppId(), session.getClientType(), session.getImei()));
            }
        }
        return list;
    }

    /**
     * 发送消息给指定用户的指定客户端
     *
     * @param toId       接收消息的用户 ID
     * @param command    消息命令
     * @param data       消息内容
     * @param clientInfo 客户端信息
     */
    public void sendToUser(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession session = userSessionHelper.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(),
                                                               clientInfo.getImei());
        sendPack(toId, command, data, session);
    }

    /**
     * 发送给除了某一端的其他端（这个相当于是对下面那个方法做了一个再封装）
     *
     * @param toId       接收消息的用户 ID
     * @param clientType 客户端类型
     * @param imei       设备 IMEI
     * @param command    消息命令
     * @param msg        消息内容
     * @param appId      应用 ID
     */
    public void sendToUser(String toId, Integer clientType, String imei, Command command, Object msg, Integer appId) {
        // 如果imei好和clientType不为空的话，说明就是正常的用户，那就把这个信息发送给除了这个端的其他用户
        if (clientType != null && StringUtils.isNotBlank(imei)) {
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId, command, msg, clientInfo);
        } else {
            sendToUser(toId, command, msg, appId);
        }
    }

    /**
     * 发送消息给指定用户的除某个客户端外的其他客户端
     *
     * @param toId       接收消息的用户 ID
     * @param command    消息命令
     * @param data       消息内容
     * @param clientInfo 客户端信息
     */
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        // 获取指定用户的所有会话信息
        List<UserSession> userSessionList = userSessionHelper.getUserSessionList(clientInfo.getAppId(), toId);
        for (UserSession session : userSessionList) {
            // 如果会话信息和客户端信息不匹配
            if (!isMatch(session, clientInfo)) {
                // 调用 sendPack 方法发送消息
                sendPack(toId, command, data, session);
            }
        }
    }

    /**
     * 判断会话信息和客户端信息是否匹配
     *
     * @param sessionDto 会话信息
     * @param clientInfo 客户端信息
     * @return 匹配返回 true，不匹配返回 false
     */
    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return ObjectUtil.equal(sessionDto.getAppId(), clientInfo.getAppId()) && ObjectUtil.equal(sessionDto.getImei(),
                                                                                                  clientInfo.getImei())
                && ObjectUtil.equal(sessionDto.getClientType(), clientInfo.getClientType());
    }
}
