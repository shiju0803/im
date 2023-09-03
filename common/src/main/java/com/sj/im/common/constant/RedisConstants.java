/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.constant;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Redisson公共常量
 */
public class RedisConstants {

    /**
     * 用户签名，格式：appId:userSign:
     */
    public static final String USER_SIGN = "userSign";

    /**
     * 用户上线通知channel
     */
    public static final String USER_LOGIN_CHANNEL = "signal/channel/LOGIN_USER_INNER_QUEUE";

    /**
     * 用户会话，格式：appId + UserSessionConstants + 用户id，例如10000：userSession：lld
     */
    public static final String USER_SESSION = ":userSession:";

    /**
     * 缓存客户端消息防重，格式： appId + :cacheMessage: + messageId
     */
    public static final String CACHE_MESSAGE = "cacheMessage";

    /**
     * 离线消息
     */
    public static final String OFFLINE_MESSAGE = "offlineMessage";

    /**
     * seq 前缀
     */
    public static final String SEQ_PREFIX = "seq";

    /**
     * 地址前缀
     */
    public static final String REDIS_PROTO = "redis://";

    /**
     * 分隔符
     */
    public static final String SPLIT_DOT = ",";

    /**
     * 用户订阅列表，格式 ：appId + :subscribe: + userId。Hash结构，filed为订阅自己的人
     */
    public static final String SUBSCRIBE = "subscribe";

    /**
     * 用户自定义在线状态，格式 ：appId + :userCustomerStatus: + userId。set，value为用户id
     */
    public static final String USER_CUSTOMER_STATUS = "userCustomerStatus";

    /**
     * 隐藏无参构造
     */
    private RedisConstants() {}
}