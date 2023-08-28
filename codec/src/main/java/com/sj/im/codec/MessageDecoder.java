/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.message.Message;
import com.sj.im.codec.message.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 私有协议解码器
 */
public class MessageDecoder extends ByteToMessageDecoder {
    /**
     * 请求方式 HTTP GET POST PUT DELETE 版本 1.0 1.1 2.0
     * client IOS 安卓 pc(windows mac) web // 支持json 也支持 protobuf
     * appId (自定义参数)
     * 请求数据长度 28 + imei + body
     * 请求头（指令 版本 clientType 消息解析类型 appId imei长度 bodyLen） + imei号 + 请求体
     */

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 请求头（指令 版本 clientType 消息解析类型 appId imei长度 bodyLen） + imei号 + 请求体

        // 请求数据长度 28 + imei + body
        if (in.readableBytes() < 28) {
            return;
        }

        // 获取command
        int command = in.readInt();
        // 获取version
        int version = in.readInt();
        // 获取clientType
        int clientType = in.readInt();
        // 获取messageType
        int messageType = in.readInt();
        // 获取appId
        int appId = in.readInt();
        // 获取imeiLen
        int imeiLen = in.readInt();
        // 获取bodyLen
        int bodyLen = in.readInt();

        // 判断数据长度是否正确
        if (in.readableBytes() < imeiLen + bodyLen) {
            in.resetReaderIndex();
            return;
        }

        // 获取imei
        byte[] imeiData = new byte[imeiLen];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        // 获取请求体
        byte[] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);

        MessageHeader header = new MessageHeader();
        header.setCommand(command);
        header.setVersion(version);
        header.setClientType(clientType);
        header.setMessageType(messageType);
        header.setAppId(appId);
        header.setImeiLength(imeiLen);
        header.setBodyLen(bodyLen);
        header.setImei(imei);

        Message message = new Message();
        message.setMessageHeader(header);

        // 解析data数据 0x0:Json,0x1:ProtoBuf,0x2:Xml,默认:0x0
        if (ObjectUtil.equal(messageType, 0x0)) {
            String body = new String(bodyData);
            JSONObject parseObj = JSONUtil.parseObj(body);
            message.setMessagePack(parseObj);
        }

        in.markReaderIndex();
        out.add(message);
    }
}
