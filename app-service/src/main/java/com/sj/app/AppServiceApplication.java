/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.sj.app", "com.sj.im.common"})
public class AppServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppServiceApplication.class, args);
    }
}