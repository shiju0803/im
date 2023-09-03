/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import com.alibaba.fastjson.JSON;
import com.sj.im.codec.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息编码类，私有协议规则，前4位表示长度，接着command4位，后面是数据。
 * MessageEncoder 类继承自 Netty 的 MessageToByteEncoder 类，用于将消息编码成字节流。
 */
public class MessageEncoder extends MessageToByteEncoder<MessagePack<Object>> {

    /**
     * 重写 encode 方法，将 MessagePack 对象编码成字节流
     *
     * @param ctx ChannelHandlerContext 对象，提供 Channel 相关的操作
     * @param msg 待编码的消息对象
     * @param out 编码后的字节流
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack<Object> msg, ByteBuf out) {
        String data = JSON.toJSONString(msg.getData());
        byte[] bytes = data.getBytes();
        out.writeInt(msg.getCommand());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
