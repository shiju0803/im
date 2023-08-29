/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 应用配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {
    /**
     * zk连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时时间
     */
    private Integer zkConnectTimeOut;

    /**
     * im管道地址路由轮循策略 1随机 2轮循 3一致性hash
     */
    private Integer imRouteWay;

    /**
     * 如果选用一致性hash的话具体hash算法
     */
    private Integer consistentHashWay;
}
