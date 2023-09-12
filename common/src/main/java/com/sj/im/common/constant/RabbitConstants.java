/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.constant;

/**
 * RabbitMQ公共常量
 *
 * @author ShiJu
 * @version 1.0
 */
public class RabbitConstants {
    /**
     * IM系统公用交换机
     */
    public static final String IM_EXCHANGE = "IM_EXCHANGE";

    /**
     * 用于用户服务与消息服务之间的消息队列通信
     */
    public static final String IM_2_USER_SERVICE_QUEUE = "IM_2_USER_SERVICE_QUEUE";

    /**
     * 用于消息处理管道向消息服务发送消息
     */
    public static final String IM_2_MESSAGE_SERVICE_QUEUE = "IM_2_MESSAGE_SERVICE_QUEUE";

    /**
     * 用于消息服务与群聊服务之间的消息队列通信
     */
    public static final String IM_2_GROUP_SERVICE_QUEUE = "IM_2_GROUP_SERVICE_QUEUE";

    /**
     * 用于消息服务与好友关系服务之间的消息队列通信
     */
    public static final String IM_2_FRIENDSHIP_SERVICE_QUEUE = "IM_2_FRIENDSHIP_SERVICE_QUEUE";

    /**
     * 用于消息服务向消息处理管道发送消息
     */
    public static final String MESSAGE_SERVICE_2_IM_QUEUE = "MESSAGE_SERVICE_2_IM_QUEUE";

    /**
     * 用于群组服务向消息处理管道发送消息
     */
    public static final String GROUP_SERVICE_2_IM_QUEUE = "GROUP_SERVICE_2_IM_QUEUE";

    /**
     * 用于好友关系服务向消息处理管道发送消息
     */
    public static final String FRIENDSHIP_2_IM_QUEUE = "FRIENDSHIP_2_IM_QUEUE";

    /**
     * 用于存储点对点消息的消息队列
     */
    public static final String STORE_P2P_MESSAGE_QUEUE = "STORE_P2P_MESSAGE_QUEUE";

    /**
     * 用于存储群聊消息的消息队列
     */
    public static final String STORE_GROUP_MESSAGE_QUEUE = "STORE_GROUP_MESSAGE_QUEUE";

    /**
     * 隐藏无参构造
     */
    private RabbitConstants() {}
}