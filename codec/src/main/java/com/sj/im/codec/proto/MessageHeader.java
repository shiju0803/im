/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.proto;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息头
 */
@Data
public class MessageHeader {
    /**
     * 消息操作指令，十六进制，一个消息的开始通常以0x开头
     * 占用4字节
     */
    private Integer command;

    /**
     * 协议版本号，占用4字节
     */
    private Integer version;

    /**
     * 客户端类型，占用4字节
     */
    private Integer clientType;
    /**
     * 应用ID，占用4字节
     */
    private Integer appId;

    /**
     * 数据解析类型，和具体业务无关，后续根据解析类型解析data数据
     * 0x0表示Json,0x1表示ProtoBuf,0x2表示Xml,默认为0x0
     * 占用4字节
     */
    private Integer messageType = 0x0;

    /**
     * imei号的长度，占用4字节
     */
    private Integer imeiLength;

    /**
     * 包体长度，占用4字节
     */
    private Integer bodyLen;

    /**
     * imei号，对应占用的字节数由imeiLength确定
     */
    private String imei;
}
