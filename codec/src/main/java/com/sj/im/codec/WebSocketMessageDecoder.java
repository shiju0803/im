/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import com.sj.im.codec.proto.Message;
import com.sj.im.codec.util.ByteBufToMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * WebSocket 消息解码器，将二进制 WebSocketFrame 转换为自定义的 Message 对象
 *
 * @author ShiJu
 * @version 1.0
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    /**
     * 解码方法，将 BinaryWebSocketFrame 对象转换为自定义的 Message 对象
     *
     * @param ctx ChannelHandlerContext 对象
     * @param msg BinaryWebSocketFrame 对象，表示接收到的 WebSocket 消息
     * @param out List<Object> 对象，表示解码后输出的消息列表
     * @throws Exception 异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
        // 获取 BinaryWebSocketFrame 的 ByteBuf 对象
        ByteBuf content = msg.content();
        // 如果 ByteBuf 对象的可读字节数小于 28，则返回
        if (content.readableBytes() < 28) {
            return;
        }

        // 将 ByteBuf 对象转换为自定义的 Message 对象
        Message message = ByteBufToMessageUtil.transition(content);
        // 如果转换失败，则返回
        if (message == null) {
            return;
        }
        // 将解码后的 Message 对象添加到输出列表中
        out.add(message);
    }
}