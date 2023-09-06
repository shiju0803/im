/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

public interface CheckSendMessageService {
    /**
     * 检查发送者是否被禁言或屏蔽
     *
     * @param fromId 发送者ID
     * @param appId  应用ID
     */
    void checkSenderForbiddenAndMute(String fromId, Integer appId);


    /**
     * 检查好友关系
     *
     * @param fromId 发送者ID
     * @param toId   接收者ID
     * @param appId  应用ID
     */
    void checkFriendShip(String fromId, String toId, Integer appId);


    /**
     * 检查群组消息
     *
     * @param fromId  发送者ID
     * @param groupId 群组ID
     * @param appId   应用ID
     */
    void checkGroupMessage(String fromId, String groupId, Integer appId);
}