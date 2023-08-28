/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.redis;

import com.sj.im.codec.config.BootstrapConfig;
import lombok.Getter;
import org.redisson.api.RedissonClient;

/**
 * @author ShiJu
 * @version 1.0
 * @description: redis管理类
 */
public class RedisManager {

    @Getter
    private static RedissonClient redissonClient;

    public static void init(BootstrapConfig config) {
        RedissonClientStrategy redissonClientStrategy = new RedissonClientStrategy();
        redissonClient = redissonClientStrategy.getRedissonClient(config.getLim().getRedis());
    }
}
