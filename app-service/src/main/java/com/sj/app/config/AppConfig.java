/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 应用配置类
 *
 * @author ShiJu
 * @version 1.0
 */
@Component
@Getter
public class AppConfig {
    /**
     * im服务器地址
     */
    @Value("${appConfig.imUrl}")
    private String imUrl;
    /**
     * im接口版本
     */
    @Value("${appConfig.imVersion}")
    private String imVersion;

    /**
     * 应用id
     */
    @Value("${appConfig.appId}")
    private Integer appId;

    /**
     * 管理员账户id
     */
    @Value("${appConfig.adminId}")
    private String adminId;

    /**
     * 密钥
     */
    @Value("${appConfig.privateKey}")
    private String privateKey;
}
