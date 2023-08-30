/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.MessagePack;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: TODO
 */
@Slf4j
@Component
public class MessageHelper {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserSessionHelper userSessionHelper;

    private void sendMessage(UserSession session, Object msg) {
        try {
            log.info("send message == " + msg);
            rabbitTemplate.convertAndSend("", String.valueOf(session.getBrokerId()), msg);
        } catch (Exception e) {
            log.error("send error : {}", e.getMessage());
        }
    }

    /**
     * 包装数据，调用sendMessage
     */
    private void sendPack(String toId, Command command, Object msg, UserSession session) {
        MessagePack<Object> messagePack = new MessagePack<>();
        messagePack.setCommand(command.getCommand());
        messagePack.setToId(toId);
        messagePack.setClientType(session.getClientType());
        messagePack.setAppId(session.getAppId());
        messagePack.setImei(session.getImei());
        JSONObject data = JSONUtil.parseObj(msg);
        messagePack.setData(data);

        String msgBody = JSONUtil.toJsonStr(messagePack);
        sendMessage(session, msgBody);
    }

    /**
     * 发送给所有端
     */
    public void sendToUser(String toId, Command command, Object data, Integer appId) {
        List<UserSession> userSessionList = userSessionHelper.getUserSessionList(appId, toId);
        for (UserSession session : userSessionList) {
            sendPack(toId, command, data, session);
        }
    }

    /**
     * 发送给某个用户的指定客户端
     */
    public void sendToUser(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession session = userSessionHelper.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(), clientInfo.getImei());
        sendPack(toId, command, data, session);
    }

    /**
     * 发送给除了某一端的其他端（这个相当于是对下面那个方法做了一个再封装）
     */
    public void sendToUser(String toId, Integer clientType, String imei, Command command, Object msg, Integer appId){
        // 如果imei好和clientType不为空的话，说明就是正常的用户，那就把这个信息发送给除了这个端的其他用户
        if(clientType != null && StringUtils.isNotBlank(imei)){
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId, command, msg, clientInfo);
        }else{
            sendToUser(toId, command, msg, appId);
        }
    }

    /**
     * 发送给除了某个用户的指定客户端
     */
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        List<UserSession> userSessionList = userSessionHelper.getUserSessionList(clientInfo.getAppId(), toId);
        for (UserSession session : userSessionList) {
            if (!isMatch(session, clientInfo)) {
                sendPack(toId, command, data, session);
            }
        }
    }

    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return ObjectUtil.equal(sessionDto.getAppId(), clientInfo.getAppId())
                && ObjectUtil.equal(sessionDto.getImei(), clientInfo.getImei())
                && ObjectUtil.equal(sessionDto.getClientType(), clientInfo.getClientType());
    }
}
