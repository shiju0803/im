/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.util;

import com.sj.im.common.exception.BusinessException;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 生成点对点会话 ID 的工具类
 */
public class ConversationIdGenerate {

    /**
     * 生成点对点会话 ID
     *
     * @param fromId 发送方 ID
     * @param toId   接收方 ID
     * @return 点对点会话 ID
     * @throws BusinessException 当发送方 ID 和接收方 ID 相同时，抛出异常
     */
    public static String generateP2PId(String fromId, String toId) {
        // 根据发送方 ID 和接收方 ID 的大小关系，确定点对点会话 ID 的格式
        int i = fromId.compareTo(toId);
        if (i < 0) {
            return toId + "|" + fromId;
        } else if (i > 0) {
            return fromId + "|" + toId;
        }

        // 如果发送方 ID 和接收方 ID 相同，抛出异常
        throw new BusinessException("发送方 ID 和接收方 ID 不能相同");
    }

    private ConversationIdGenerate() {}
}