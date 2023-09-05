/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty")
public class TcpConfig {

    private Integer tcpPort; // tcp绑定的端口号
    private Integer webSocketPort; // webSocket，绑定的端口号
    private boolean enableWebSocket; // 是否启用webSocket
    private Integer bossThreadSize; // boss线程大小，默认=1
    private Integer workThreadSize; // work线程大小
    private Long heartBeatTime; // 心跳超时时间，单位毫秒
    private Integer loginModel; // 登陆模式
    private Integer brokerId; // 应用实例的唯一ID，用于区分不同的应用实例或者集群
    private String logicUrl; // 逻辑地址
}