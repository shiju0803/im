/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.common.model.UserSession;
import com.sj.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ShiJu
 * @version 1.0
 * @description: NioSocketChannel存取工具类
 */
public class SessionSocketHolder {
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Integer appId, String userId, Integer clientType, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        CHANNELS.put(dto, channel);
    }

    public static NioSocketChannel get(Integer appId, String userId, Integer clientType) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        return CHANNELS.get(dto);
    }

    public static NioSocketChannel remove(Integer appId, String userId, Integer clientType) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        return CHANNELS.remove(dto);
    }

    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet().stream()
                .filter(entry -> ObjectUtil.equal(entry.getValue(), channel))
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    /**
     * 退出
     */
    public static void removeUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.APPID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
        // 删除session
        SessionSocketHolder.remove(appId, userId, clientType);
        // redis删除
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Integer, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        map.remove(clientType);
        channel.close();
    }

    /**
     * 离线
     */
    public static void offlineUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.APPID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
        // 删除session
        SessionSocketHolder.remove(appId, userId, clientType);
        // redis删除
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Integer, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        String sessionStr = map.get(clientType);

        if (CharSequenceUtil.isNotBlank(sessionStr)) {
            UserSession userSession = JSONUtil.toBean(sessionStr, UserSession.class, true);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType, JSONUtil.toJsonStr(userSession));
        }
        channel.close();
    }
}
