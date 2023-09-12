/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TCP网关启动类
 *
 * @author ShiJu
 * @version 1.0
 */
@EnableFeignClients(basePackages = {"com.sj.im.tcp.feign"})
@SpringBootApplication(scanBasePackages = {"com.sj.im.tcp", "com.sj.im.common"})
public class ImTcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImTcpApplication.class, args);
    }
}