/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.demo.netty.base;

import com.sj.im.tcp.demo.netty.base.server.DiscardServer;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Netty服务启动类
 */
public class Starter {
    public static void main(String[] args) throws Exception {
        new DiscardServer(9001).run();
    }
}
