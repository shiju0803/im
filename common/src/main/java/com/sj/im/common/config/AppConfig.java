/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 应用配置类
 */
@Component
@Getter
public class AppConfig {
    /**
     * zk连接地址
     */
    @Value("${appConfig.zkAddr}")
    private String zkAddr;

    /**
     * zk连接超时时间
     */
    @Value("${appConfig.zkConnectTimeOut}")
    private Integer zkConnectTimeOut;

    /**
     * im管道地址路由轮循策略 1随机 2轮循 3一致性hash
     */
    @Value("${appConfig.imRouteWay}")
    private Integer imRouteWay;

    /**
     * 如果选用一致性hash的话具体hash算法
     */
    @Value("${appConfig.consistentHashWay}")
    private Integer consistentHashWay;

    /**
     * 回调地址
     */
    @Value("${appConfig.callbackUrl}")
    private String callbackUrl;

    /**
     * 发送消息是否校验关系链
     */
    @Value("${appConfig.sendMessageCheckFriend}")
    private boolean sendMessageCheckFriend;

    /**
     * 发送消息是否校验黑名单
     */
    @Value("${appConfig.sendMessageCheckBlack}")
    private boolean sendMessageCheckBlack;

    /**
     * 用户资料变更之后回调开关
     */
    @Value("${appConfig.modifyUserAfterCallback}")
    private boolean modifyUserAfterCallback;

    /**
     * 添加好友之后回调开关
     */
    @Value("${appConfig.addFriendAfterCallback}")
    private boolean addFriendAfterCallback;

    /**
     * 添加好友之前回调开关
     */
    @Value("${appConfig.addFriendBeforeCallback}")
    private boolean addFriendBeforeCallback;

    /**
     * 修改好友之后回调开关
     */
    @Value("${appConfig.modifyFriendAfterCallback}")
    private boolean modifyFriendAfterCallback;

    /**
     * 删除好友之后回调开关
     */
    @Value("${appConfig.deleteFriendAfterCallback}")
    private boolean deleteFriendAfterCallback;

    /**
     * 添加黑名单之后回调开关
     */
    @Value("${appConfig.addFriendShipBlackAfterCallback}")
    private boolean addFriendShipBlackAfterCallback;

    /**
     * 删除黑名单之后回调开关
     */
    @Value("${appConfig.deleteFriendShipBlackAfterCallback}")
    private boolean deleteFriendShipBlackAfterCallback;

    /**
     * 创建群聊之后回调开关
     */
    @Value("${appConfig.createGroupAfterCallback}")
    private boolean createGroupAfterCallback;

    /**
     * 修改群聊之后回调开关
     */
    @Value("${appConfig.modifyGroupAfterCallback}")
    private boolean modifyGroupAfterCallback;

    /**
     * 解散群聊之后回调开关
     */
    @Value("${appConfig.destroyGroupAfterCallback}")
    private boolean destroyGroupAfterCallback;

    /**
     * 删除群成员之后回调
     */
    @Value("${appConfig.deleteGroupMemberAfterCallback}")
    private boolean deleteGroupMemberAfterCallback;

    /**
     * 拉人入群之前回调
     */
    @Value("${appConfig.addGroupMemberBeforeCallback}")
    private boolean addGroupMemberBeforeCallback;

    /**
     * 拉人入群之后回调
     */
    @Value("${appConfig.addGroupMemberAfterCallback}")
    private boolean addGroupMemberAfterCallback;

    /**
     * 发送单聊消息之后
     */
    @Value("${appConfig.sendMessageAfterCallback}")
    private boolean sendMessageAfterCallback;

    /**
     * 发送单聊消息之前
     */
    @Value("${appConfig.sendMessageBeforeCallback}")
    private boolean sendMessageBeforeCallback;

    /**
     * 发送群聊消息之后
     */
    @Value("${appConfig.sendGroupMessageAfterCallback}")
    private boolean sendGroupMessageAfterCallback;

    /**
     * 发送群聊消息之前
     */
    @Value("${appConfig.sendGroupMessageBeforeCallback}")
    private boolean sendGroupMessageBeforeCallback;
}
