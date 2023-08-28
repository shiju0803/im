/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.message;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息头
 */
@Data
public class MessageHeader {
    /**
     * 4字节 消息操作指令 十六进制 一个消息的开始通常以0x开头
     */
    private Integer command;

    /**
     * 4字节 版本号
     */
    private Integer version;

    /**
     * 4字节 端类型
     */
    private Integer clientType;
    /**
     * 4字节 appId 应用ID
     */
    private Integer appId;

    /**
     * 4字节 数据解析类型 和具体业务无关，后续根据解析类型解析data数据 0x0:Json,0x1:ProtoBuf,0x2:Xml,默认:0x0
     */
    private Integer messageType = 0x0;

    /**
     * 4字节 imei长度
     */
    private Integer imeiLength;

    /**
     * 4字节 包体长度
     */
    private Integer bodyLen;

    /**
     * imei号
     */
    private String imei;
}
