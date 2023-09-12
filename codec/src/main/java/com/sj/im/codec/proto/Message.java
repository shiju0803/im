/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.proto;

import lombok.Data;

/**
 * 自定义的消息类，包含消息头和消息体
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class Message {

    /**
     * 消息头
     */
    private MessageHeader messageHeader;

    /**
     * 消息体
     */
    private Object messagePack;

    /**
     * 重写 toString 方法，返回消息的字符串表示形式
     *
     * @return String 表示消息的字符串
     */
    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
