/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.util;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 生产seq工具类
 */
@Service
public class RedisSeq {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public long doGetSeq(String key) {
        return ObjectUtil.defaultIfNull(stringRedisTemplate.opsForValue().increment(key), 0L);
    }
}
