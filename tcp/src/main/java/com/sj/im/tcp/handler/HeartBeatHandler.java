/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.handler;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 心跳检测处理类
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private Long heartBeatTime;

    public HeartBeatHandler(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    // 心跳检测触发
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断evt是否是IdleStateEvent (用于触发用户事件，包含 读空闲/写空闲/读写空闲)
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (ObjectUtil.equal(event.state(), IdleState.READER_IDLE)) {
                log.info("进入读空闲");
            }
            if (ObjectUtil.equal(event.state(), IdleState.WRITER_IDLE)) {
                log.info("进入写空闲");
            }
            if (ObjectUtil.equal(event.state(), IdleState.ALL_IDLE)) {
                // 最后一次写入
                Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(TcpConstants.READTIME)).get();
                // 现在的时间
                long now = System.currentTimeMillis();
                // 如果最后一次写入的时间不为空，且现在的时间
                if (ObjectUtil.isNotNull(lastReadTime) && now - lastReadTime > heartBeatTime) {
                    // 退后台离线
                    SessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
                }
            }
        }
    }
}
