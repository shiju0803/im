/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Bean 配置类，用于定义应用程序中的 Bean
 *
 * @author ShiJu
 * @version 1.0
 */
@Configuration
public class BeanConfig {

    /**
     * 定义 EasySqlInjector Bean。
     *
     * @return EasySqlInjector 实例
     */
    @Bean
    @Primary
    public EasySqlInjector easySqlInjector() {
        return new EasySqlInjector();
    }
}
