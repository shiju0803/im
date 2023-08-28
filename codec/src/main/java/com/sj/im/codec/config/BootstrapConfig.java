/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 启动配置类
 */
@Data
@Component
public class BootstrapConfig {

    private TcpConfig lim;

    @Data
    public static class TcpConfig{
        /**
         * tcp 绑定的端口号
         */
        private Integer tcpPort;

        /**
         * webSocket 绑定的端口号
         */
        private Integer webSocketPort;

        /**
         * boss线程 默认=1
         */
        private Integer bossThreadSize;

        /**
         * work线程
         */
        private Integer workThreadSize;

        /**
         * 心跳超时时间  单位ms
         */
        private Long heartBeatTime;

        /**
         * 登录模式
         */
        private Integer loginModel;

        /**
         * redis配置
         */
        private RedisConfig redis;

        /**
         * Rabbitmq配置
         */
        private Rabbitmq rabbitmq;

        /**
         * zookeeper配置
         */
        private ZkConfig zkConfig;

        /**
         * brokerId
         */
        private Integer brokerId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {
        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String model;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;

        /**
         * redis哨兵配置
         */
        private RedisSentinel sentinel;

        /**
         * redis集群配置
         */
        private RedisCluster cluster;
    }

    /**
     * rabbitmq哨兵模式配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rabbitmq {
        private String host;

        private Integer port;

        private String virtualHost;

        private String userName;

        private String password;
    }

    /**
     * redis单机配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }

    /**
     * redis哨兵配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSentinel {
        /**
         * 节点地址
         */
        private String nodes;

        /**
         * 主节点名称
         */
        private String master;
    }

    /**
     * redis单机配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisCluster {
        /**
         * 节点地址
         */
        private String nodes;
    }

    /**
     * redis单机配置
     */
    @Data
    public static class ZkConfig {
        /**
         * zk连接地址
         */
        private String zkAddr;

        /**
         * zk连接超时时间
         */
        private Integer zkConnectTimeOut;
    }
}
