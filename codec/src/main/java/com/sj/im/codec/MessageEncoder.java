/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import com.alibaba.fastjson.JSON;
import com.sj.im.codec.pack.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息编码类，私有协议规则，前4位表示长度，接着command 4位，后面是数据
 */
public class MessageEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        if (msg instanceof MessagePack) {
            MessagePack msgBody = (MessagePack) msg;
            String data = JSON.toJSONString(msgBody.getData());
            byte[] bytes = data.getBytes();
            out.writeInt(msgBody.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
