/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.demo.chat;

import com.sj.im.tcp.demo.chat.server.ChatDiscardServer;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Netty服务启动类
 */
public class ChatStarter {
    public static void main(String[] args) throws Exception {
        new ChatDiscardServer(9001).run();
    }
}
