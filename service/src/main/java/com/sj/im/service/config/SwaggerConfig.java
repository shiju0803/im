/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger配置类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Configuration
public class SwaggerConfig {
    /**
     * Swagger配置
     */
    @Bean
    public Docket createRestApi() {
        log.info("---------------------------开启Swagger---------------------------");
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("IM即时通讯")
                        .description("IM即时通讯服务接口文档")
                        .license("ShiJu 2023/08/21")
                        .version("v1.0")
                        .contact(new Contact("ShiJu", "https://github.com/shiju0803/im.git", "shiju1283@163.com"))
                        .build()) // 用来展示该 API 的基本信息
                .groupName("用户数据")
                .select() // 返回一个 ApiSelectorBuilder 实例，用来控制哪些接口暴露给 Swagger 来展现
                .apis(RequestHandlerSelectors.basePackage("com.sj.im.service.user.controller")) // 配置包扫描路径（根据自己项目调整，通常配置为控制器路径）
                .paths(PathSelectors.any())
                .build();
    }
}
