/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.util;

import com.sj.im.common.constant.RedisConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 写入用户序列号工具类
 */
@Service
public class WriteUserSeq {

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    /**
     * 将用户序列号写入 Redis 中
     *
     * @param appId  应用 ID
     * @param userId 用户 ID
     * @param type   序列号类型
     * @param seq    序列号值
     */
    public void writeUserSeq(Integer appId, String userId, String type, Long seq) {
        // 拼接 Redis key，格式为 appId:SeqPrefix:userId
        String key = appId + ":" + RedisConstants.SEQ_PREFIX + ":" + userId;
        // 将序列号值写入 Redis hash 中
        redisTemplate.opsForHash().put(key, type, seq);
    }
}