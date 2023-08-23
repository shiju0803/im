/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.demo.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author ShiJu
 * @version 1.0
 * @description: bio演示代码
 */
public class SocketServer {

    // 1.0 服务端在处理完第一个客户端的所有事件之前，无法为其他客户端提供服务
    // 2.0 会产生大量空闲线程，浪费服务器资源。
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);
        while (true) {
            System.out.println("等待连接...");
            // 阻塞方法
            Socket clientSocket = serverSocket.accept();
            System.out.println("有客户端连接了...");

            // 阻塞
//            handler(clientSocket);

            // 多线程解决阻塞
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler(clientSocket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备Read...");
        // 接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = clientSocket.getInputStream().read(bytes);
        System.out.println("read完毕...");
        if (read != -1) {
            System.out.println("接收到客户端的数据: " + new String(bytes, 0, read));
        }
    }
}
