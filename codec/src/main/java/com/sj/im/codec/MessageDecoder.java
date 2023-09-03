/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec;

import com.sj.im.codec.proto.Message;
import com.sj.im.codec.util.ByteBufToMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息解码器
 * ByteToMessageDecoder是Netty中的一个抽象类，它提供了一个可重用的ByteBuf解码器，
 * 可以将ByteBuf解码为消息对象。它的作用是将二进制数据按照特定的规则进行解码，转换成Java对象，方便后续的业务处理。
 */
public class MessageDecoder extends ByteToMessageDecoder {

    /**
     * 将ByteBuf解码为Message对象。ByteBuf必须包含一个28字节的标头，该标头指定
     * 消息命令、版本、客户端类型、消息类型、应用程序ID、imei长度和正文长度。
     * 每个字段占用4个字节。如果ByteBuf不包含至少28个字节，则此方法返回而不产生任何输出。
     *
     * @param ctx 用于传递事件给ChannelPipeline中的下一个ChannelHandler。
     * @param in  Netty 提供的可读写字节容器，包含要解码的字节数据。
     * @param out 解码后的消息对象将被添加到此列表中，以便传递给下一个ChannelHandler进行处理。
     * @throws Exception 如果在解码过程中发生错误，则异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        /**
         * 请求头：
         * 指令（4字节）
         * 版本（4字节）
         * clientType（4字节）
         * 消息解析类型（4字节）
         * appId（4字节）
         * imei长度（4字节）
         * bodyLen（4字节）：imei号 + 请求体的长度
         *
         * 上述所有字段每个占用4个字节，总共占用28个字节
         */
        // 检查可读字节数是否小于请求头的28个字节，如果小于则不够解析一个完整的请求头，直接返回
        if (in.readableBytes() < 28) {
            return;
        }

        // 将ByteBuf对象转换为Message对象
        Message message = ByteBufToMessageUtil.transition(in);
        // 如果转换结果为null，则表示解析失败，直接返回
        if (message == null) {
            return;
        }
        // 将解析结果添加到输出列表中，交给后续处理器处理
        out.add(message);
    }
}
