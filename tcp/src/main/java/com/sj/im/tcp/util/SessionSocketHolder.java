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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ShiJu
 * @version 1.0
 * @description: NioSocketChannel存取工具类
 */
public class SessionSocketHolder {
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Integer appId, String userId, Integer clientType, String imei, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        CHANNELS.put(dto, channel);
    }

    public static NioSocketChannel get(Integer appId, String userId, Integer clientType, String imei){
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setAppId(appId);
        userClientDto.setUserId(userId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);
        return CHANNELS.get(userClientDto);
    }

    public static List<NioSocketChannel> getAll(Integer appId, String userId) {
        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();

        channelInfos.forEach(channel -> {
            if (ObjectUtil.equal(channel.getAppId(), appId) && ObjectUtil.equal(channel.getUserId(), userId)) {
                channels.add(CHANNELS.get(channel));
            }
        });
        return channels;
    }

    public static NioSocketChannel remove(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        dto.setImei(imei);
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
        String imei = (String) channel.attr(AttributeKey.valueOf(TcpConstants.IMEI)).get();
        // 删除session
        SessionSocketHolder.remove(appId, userId, clientType, imei);
        // redis删除
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        map.remove(clientType + ":" + imei);
        channel.close();
    }

    /**
     * 离线
     */
    public static void offlineUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.APPID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(TcpConstants.IMEI)).get();
        // 删除session
        SessionSocketHolder.remove(appId, userId, clientType, imei);
        // redis删除
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        String sessionStr = map.get(clientType + ":" + imei);

        if (CharSequenceUtil.isNotBlank(sessionStr)) {
            UserSession userSession = JSONUtil.toBean(sessionStr, UserSession.class, true);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType + ":" + imei, JSONUtil.toJsonStr(userSession));
        }
        channel.close();
    }
}
