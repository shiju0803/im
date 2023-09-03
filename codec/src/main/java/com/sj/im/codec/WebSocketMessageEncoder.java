/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: WebSocket 消息编码器，将自定义的 MessagePack 对象转换为二进制 WebSocketFrame
 */
@Slf4j
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack<Object>> {

    /**
     * 编码方法，将 MessagePack 对象转换为二进制 WebSocketFrame
     *
     * @param ctx ChannelHandlerContext 对象
     * @param msg MessagePack 对象，表示要编码的消息
     * @param out List<Object> 对象，表示编码后输出的消息列表
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack<Object> msg, List<Object> out)  {
        try {
            // 将 MessagePack 对象转换为 JSON 字符串
            String s = JSONUtil.toJsonStr(msg);
            // 创建一个直接缓冲区，容量为 8 + s.length()，用于存储编码后的消息
            ByteBuf byteBuf = Unpooled.directBuffer(8+s.length());
            // 将 JSON 字符串转换为字节数组
            byte[] bytes = s.getBytes();
            // 将消息的命令编号写入缓冲区的前 4 个字节
            byteBuf.writeInt(msg.getCommand());
            // 将消息体的长度写入缓冲区的接下来 4 个字节
            byteBuf.writeInt(bytes.length);
            // 将消息体的内容写入缓冲区的接下来的字节中
            byteBuf.writeBytes(bytes);
            // 将编码后的消息添加到输出列表中
            out.add(new BinaryWebSocketFrame(byteBuf));
        }catch (Exception e){
            // 如果编码过程中发生异常，则打印异常日志
            log.error("webSocker data encode error : {}", e.getMessage());
        }
    }
}