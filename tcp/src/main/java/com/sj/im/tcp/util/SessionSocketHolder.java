/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.sj.im.codec.proto.MessageHeader;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ImConnectStatusEnum;
import com.sj.im.common.enums.command.UserEventCommand;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.common.model.UserSession;
import com.sj.im.tcp.publish.MqMessageProducer;
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
 * 用于管理用户连接的工具类
 *
 * @author ShiJu
 * @version 1.0
 */
public class SessionSocketHolder {

    /**
     * 存储用户连接信息的 ConcurrentHashMap
     * key：用户连接信息的 Dto
     * value：连接的 NioSocketChannel 对象
     */
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    /**
     * 将用户连接信息与对应的 NioSocketChannel 对象放入 CHANNELS 中
     *
     * @param appId      应用ID
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @param imei       设备imei号
     * @param channel    NioSocketChannel对象
     */
    public static void put(Integer appId, String userId, Integer clientType, String imei, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        CHANNELS.put(dto, channel);
    }

    /**
     * 根据应用ID、用户ID、客户端类型、设备imei号获取对应的 NioSocketChannel 对象
     *
     * @param appId      应用 ID
     * @param userId     用户 ID
     * @param clientType 客户端类型
     * @return 对应的 NioSocketChannel 对象，如果不存在则返回 null
     */
    public static NioSocketChannel get(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setAppId(appId);
        userClientDto.setUserId(userId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);
        return CHANNELS.get(userClientDto);
    }

    /**
     * 根据应用ID、用户ID获取对应的NioSocketChannel 对象
     *
     * @param appId  应用ID
     * @param userId 用户ID
     */
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

    /**
     * 根据应用 ID、用户 ID、客户端类型移除对应的 NioSocketChannel 对象
     *
     * @param appId      应用 ID
     * @param userId     用户 ID
     * @param clientType 客户端类型
     */
    public static void remove(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setUserId(userId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        CHANNELS.remove(dto);
    }

    /**
     * 根据 NioSocketChannel 对象移除对应的用户连接信息
     *
     * @param channel NioSocketChannel 对象
     */
    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet().stream().filter(entry -> ObjectUtil.equal(entry.getValue(), channel))
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    /**
     * 从系统中移除用户会话，包括从SessionSocketHolder、Redis中移除会话并发送用户状态更改通知。
     *
     * @param channel 与用户会话关联的 NioSocketChannel。
     */
    public static void removeUserSession(NioSocketChannel channel) {
        // 从通道的属性中获取用户信息
        String userId = (String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.APPID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(TcpConstants.IMEI)).get();

        // 从SessionSocketHolder中移除会话
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        // 从Redis中移除会话
        RedissonClient redissonClient = (RedissonClient) ApplicationContextHelper.getBean(RedissonClient.class);
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        map.remove(clientType + ":" + imei);

        // 准备用户状态更改通知的消息头
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setImei(imei);
        messageHeader.setClientType(clientType);

        // 准备用户状态更改通知
        UserStatusChangeNotifyPack pack = new UserStatusChangeNotifyPack();
        pack.setAppId(appId);
        pack.setUserId(userId);
        pack.setStatus(ImConnectStatusEnum.OFFLINE_STATUS.getCode());

        // 发送用户状态更改通知
        MqMessageProducer mqMessageProducer =
                (MqMessageProducer) ApplicationContextHelper.getBean(MqMessageProducer.class);
        mqMessageProducer.sendMessage(pack, messageHeader, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand());

        // 关闭通道
        channel.close();
    }

    /**
     * 离线处理用户会话，包括从 SessionSocketHolder 中移除会话、更新用户状态并发送消息通知等。
     *
     * @param channel 通道对象
     */
    public static void offlineUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.APPID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(TcpConstants.IMEI)).get();

        // 从SessionSocketHolder中移除用户会话
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        RedissonClient redissonClient = (RedissonClient) ApplicationContextHelper.getBean(RedissonClient.class);
        // 从Redis中获取用户会话信息
        RMap<String, String> map = redissonClient.getMap(appId + RedisConstants.USER_SESSION + userId);
        String sessionStr = map.get(clientType + ":" + imei);

        // 更新用户状态为离线
        if (CharSequenceUtil.isNotBlank(sessionStr)) {
            UserSession userSession = JSONUtil.toBean(sessionStr, UserSession.class, true);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType + ":" + imei, JSONUtil.toJsonStr(userSession));
        }

        // 构建消息头和用户状态变更消息体
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setImei(imei);
        messageHeader.setClientType(clientType);

        UserStatusChangeNotifyPack pack = new UserStatusChangeNotifyPack();
        pack.setAppId(appId);
        pack.setUserId(userId);
        pack.setStatus(ImConnectStatusEnum.OFFLINE_STATUS.getCode());

        // 发送用户状态变更消息
        MqMessageProducer mqMessageProducer =
                (MqMessageProducer) ApplicationContextHelper.getBean(MqMessageProducer.class);
        mqMessageProducer.sendMessage(pack, messageHeader, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand());

        // 关闭通道
        channel.close();
    }

    private SessionSocketHolder() {}
}
