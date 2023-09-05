package com.sj.im.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.sj.im.message", "com.sj.im.common"})
public class ImMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImMessageApplication.class, args);
    }
}