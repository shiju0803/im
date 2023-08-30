/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.model.UserSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取UserSession工具类
 */
@Component
public class UserSessionHelper {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户所有的session
     */
    public List<UserSession> getUserSessionList(Integer appId, String userId) {
        String userSessionKey = appId + RedisConstants.USER_SESSION + userId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(userSessionKey);

        List<UserSession> userSessions = new ArrayList<>();
        entries.values().forEach(entry -> {
            String str = (String) entry;
            UserSession userSession = JSONUtil.toBean(str, UserSession.class);

            if (ObjectUtil.equal(userSession.getConnectState(), ImConnectStatusEnum.ONLINE_STATUS)) {
                userSessions.add(userSession);
            }
        });
        return userSessions;
    }

    /**
     * 获取指定端的session
     */
    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String userSessionKey = appId + RedisConstants.USER_SESSION + userId;
        String hashKey = clientType + ":" + imei;
        String entry = (String) stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
        return JSONUtil.toBean(entry, UserSession.class);
    }
}
