/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.model.req.FriendShipReq;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友申请业务接口
 */
public interface ImFriendShipRequestService {
    /**
     * 添加好友请求
     */
    ResponseVO<String> addFriendshipRequest(FriendShipReq req);

    /**
     * 审批好友请求
     */
    ResponseVO<String> approveFriendRequest(ApproveFriendRequestReq req);

    /**
     * 已读好友请求
     */
    ResponseVO<String> readFriendShipRequestReq(FriendShipReq req);

    /**
     * 获得好友请求
     */
    ResponseVO<List<ImFriendShipRequestEntity>> getFriendRequest(FriendShipReq req);
}