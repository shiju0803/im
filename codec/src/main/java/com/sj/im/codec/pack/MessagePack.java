/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息服务发送给tcp的包体,tcp再根据改包体解析成Message发给客户端
 */
@Data
public class MessagePack<T> implements Serializable {

    private String userId;

    private Integer appId;

    /**
     * 接收方
     */
    private String toId;

    /**
     * 客户端标识
     */
    private int clientType;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 客户端设备唯一标识
     */
    private String imei;

    /**
     * 指令
     */
    private Integer command;

    /**
     * 业务数据对象，如果是聊天消息则不需要解析直接透传
     */
    private T data;

    /**
     * 用户签名
     */
    private String userSign;
}