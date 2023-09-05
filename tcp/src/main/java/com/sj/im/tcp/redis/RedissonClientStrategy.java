/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.redis;

import com.sj.im.common.config.BootstrapConfig;
import com.sj.im.common.constant.RedisConstants;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Redisson连接配置类
 */
public class RedissonClientStrategy {

    /**
     * 根据连接类型，自动返回对应的连接配置类
     */
    public RedissonClient getRedissonClient(BootstrapConfig.RedisConfig redisConfig) {
        RedissonClient redissonClient = null;
        switch (redisConfig.getModel()) {
            case "single":
                redissonClient = getSingleRedissonClient(redisConfig);
                break;
            case "sentinel":
                redissonClient = getSentinelRedissonClient(redisConfig);
                break;
            case "cluster":
                redissonClient = getClusterRedissonClient(redisConfig);
                break;
        }
        return redissonClient;
    }

    /**
     * 单机连接
     */
    public RedissonClient getSingleRedissonClient(BootstrapConfig.RedisConfig redisConfig) {
        Config config = new Config();
        String node = redisConfig.getSingle().getAddress();
        node = node.startsWith(RedisConstants.REDIS_PROTO) ? node : RedisConstants.REDIS_PROTO + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setPassword(redisConfig.getPassword())
                .setDatabase(redisConfig.getDatabase())
                .setTimeout(redisConfig.getTimeout())
                .setConnectionMinimumIdleSize(redisConfig.getPoolMinIdle())
                .setConnectTimeout(redisConfig.getPoolConnTimeout())
                .setConnectionPoolSize(redisConfig.getPoolSize());
        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            serverConfig.setPassword(redisConfig.getPassword());
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    /**
     * 哨兵连接
     */
    public RedissonClient getSentinelRedissonClient(BootstrapConfig.RedisConfig redisConfig) {
        Config config = new Config();
        String address = redisConfig.getSentinel().getNodes();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .addSentinelAddress(getNodes(address))
                .setPassword(redisConfig.getPassword())
                .setDatabase(redisConfig.getDatabase())
                .setMasterName(redisConfig.getSentinel().getMaster())
                .setTimeout(redisConfig.getTimeout())
                .setConnectTimeout(redisConfig.getPoolConnTimeout());
        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            sentinelServersConfig.setPassword(redisConfig.getPassword());
        }
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    /**
     * 集群连接
     */
    public RedissonClient getClusterRedissonClient(BootstrapConfig.RedisConfig redisConfig) {
        Config config = new Config();
        String address = redisConfig.getCluster().getNodes();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .setKeepAlive(true)
                .setPingConnectionInterval(redisConfig.getPingConnectionInterval())
                .addNodeAddress(getNodes(address))
                .setPassword(redisConfig.getPassword())
                .setTimeout(redisConfig.getTimeout())
                .setConnectTimeout(redisConfig.getPoolConnTimeout());
        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            clusterServersConfig.setPassword(redisConfig.getPassword());
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
