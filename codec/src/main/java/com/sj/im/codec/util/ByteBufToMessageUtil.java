/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.Message;
import com.sj.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 自定义私有协议转换器
 */
public class ByteBufToMessageUtil {
    /**
     * 请求方式 HTTP GET POST PUT DELETE 版本 1.0 1.1 2.0
     * client IOS 安卓 pc(windows mac) web // 支持json 也支持 protobuf
     * appId (自定义参数)
     * 请求数据长度 28 + imei + body
     * 请求头（指令 版本 clientType 消息解析类型 appId imei长度 bodyLen） + imei号 + 请求体
     */
    public static Message transition(ByteBuf in) {
        // 获取指令
        int command = in.readInt();
        // 获取版本
        int version = in.readInt();
        // 获取客户端好类型
        int clientType = in.readInt();
        // 获取消息类型
        int messageType = in.readInt();
        // 获取appId
        int appId = in.readInt();
        // 获取imei长度
        int imeiLen = in.readInt();
        // 获取请求体长度
        int bodyLen = in.readInt();

        // 检查可读取的字节数是否足够解析整个消息，如果不够则返回 null
        if (in.readableBytes() < imeiLen + bodyLen) {
            in.resetReaderIndex();
            return null;
        }

        // 读取imei号数据并转换成字符串类型
        byte[] imeiData = new byte[imeiLen];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        // 读取消息体数据
        byte[] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);

        // 构造消息头对象
        MessageHeader header = new MessageHeader();
        header.setCommand(command);
        header.setVersion(version);
        header.setClientType(clientType);
        header.setMessageType(messageType);
        header.setAppId(appId);
        header.setImeiLength(imeiLen);
        header.setBodyLen(bodyLen);
        header.setImei(imei);

        // 构造消息对象
        Message message = new Message();
        message.setMessageHeader(header);

        // 如果消息类型为 0x0，则解析消息体为 JSON 格式
        if (ObjectUtil.equal(messageType, 0x0)) {
            String body = new String(bodyData);
            JSONObject parseObj = JSONUtil.parseObj(body);
            message.setMessagePack(parseObj);
        }

        // 标记读指针位置
        in.markReaderIndex();
        return message;
    }

    private ByteBufToMessageUtil() {
    }
}
