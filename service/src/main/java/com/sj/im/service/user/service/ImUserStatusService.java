/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service;

import com.sj.im.service.user.web.resp.UserStatusChangeNotifyContent;
import com.sj.im.service.user.web.req.PullFriendOnlineStatusReq;
import com.sj.im.service.user.web.req.PullUserOnlineStatusReq;
import com.sj.im.service.user.web.req.SetUserCustomerStatusReq;
import com.sj.im.service.user.web.req.SubscribeUserOnlineStatusReq;
import com.sj.im.service.user.web.resp.UserOnlineStatusResp;

import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户在线状态相关接口
 */
public interface ImUserStatusService {
    /**
     * 处理用户在线状态通知
     *
     * @param content 用户在线状态变更通知内容
     */
    void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

    /**
     * 订阅用户在线状态
     *
     * @param req 订阅用户在线状态请求
     */
    void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    /**
     * 设置用户客服状态
     *
     * @param req 设置用户客服状态请求
     */
    void setUserCustomerStatus(SetUserCustomerStatusReq req);

    /**
     * 查询好友在线状态
     *
     * @param req 查询好友在线状态请求
     * @return 好友在线状态映射
     */
    Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req);

    /**
     * 查询用户在线状态
     *
     * @param req 查询用户在线状态请求
     * @return 用户在线状态映射
     */
    Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req);
}