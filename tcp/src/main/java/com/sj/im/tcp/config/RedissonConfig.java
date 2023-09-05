/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.config;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.tcp.listener.RedisUserLoginMessageListener;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RedissonConfig {
    @Resource
    private TcpConfig tcpConfig;

    @Value("${redisson.model}")
    private String model;

    @Value("${redisson.database}")
    private Integer database;

    @Value("${redisson.password}")
    private String password;

    @Value("${redisson.timeout}")
    private Integer timeout;

    @Value("${redisson.poolMinIdle}")
    private Integer poolMinIdle;

    @Value("${redisson.poolConnTimeout}")
    private Integer poolConnTimeout;

    @Value("${redisson.poolSize}")
    private Integer poolSize;

    @Value("${redisson.pingConnectionInterval}")
    private Integer pingConnectionInterval;

    @Value("${redisson.address}")
    private String address;

    @Value("${redisson.nodes}")
    private String nodes;

    @Value("${redisson.masterName}")
    private String masterName;

    /**
     * 根据连接类型，自动返回对应的连接配置类
     */
    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redissonClient = null;
        if (ObjectUtil.equal(model, "single")) {
            redissonClient = getSingleRedissonClient();
        }
        if (ObjectUtil.equal(model, "sentinel")) {
            redissonClient = getSentinelRedissonClient();
        }
        if (ObjectUtil.equal(model, "cluster")) {
            redissonClient = getClusterRedissonClient();
        }
        return redissonClient;
    }

    @Bean
    public RedisUserLoginMessageListener userLoginMessageListener() {
        RedisUserLoginMessageListener userLoginMessageListener = new RedisUserLoginMessageListener(redissonClient(), tcpConfig);
        userLoginMessageListener.listenerUserLogin();
        return userLoginMessageListener;
    }

    /**
     * 单机连接
     */
    public RedissonClient getSingleRedissonClient() {
        Config config = new Config();
        String host = address.startsWith(RedisConstants.REDIS_PROTO) ? address : RedisConstants.REDIS_PROTO + address;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(host)
                .setDatabase(database)
                .setTimeout(timeout)
                .setConnectionMinimumIdleSize(poolMinIdle)
                .setConnectTimeout(poolConnTimeout)
                .setConnectionPoolSize(poolSize);
        if (StringUtils.isNotBlank(password)) {
            serverConfig.setPassword(password);
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    /**
     * 哨兵连接
     */
    public RedissonClient getSentinelRedissonClient() {
        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .addSentinelAddress(getNodes(nodes))
                .setDatabase(database)
                .setMasterName(masterName)
                .setTimeout(timeout)
                .setConnectTimeout(poolConnTimeout);
        if (StringUtils.isNotBlank(password)) {
            sentinelServersConfig.setPassword(password);
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    /**
     * 集群连接
     */
    public RedissonClient getClusterRedissonClient() {
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .setKeepAlive(true)
                .setPingConnectionInterval(pingConnectionInterval)
                .addNodeAddress(getNodes(nodes))
                .setTimeout(timeout)
                .setConnectTimeout(poolConnTimeout);
        if (StringUtils.isNotBlank(password)) {
            clusterServersConfig.setPassword(password);
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    private static String[] getNodes(String address) {
        String[] nodes = address.split(RedisConstants.SPLIT_DOT);

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = RedisConstants.REDIS_PROTO + nodes[i];
        }
        return nodes;
    }
}