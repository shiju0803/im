/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.service.friendship.entry.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.web.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.web.req.FriendDto;
import com.sj.im.service.friendship.web.req.ReadFriendShipRequestReq;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友申请业务接口
 */
public interface ImFriendShipRequestService {

    /**
     * 添加好友申请
     *
     * @param fromId 申请人ID
     * @param dto    好友信息
     * @param appId  应用ID
     */
    void addFriendshipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 审批好友申请
     *
     * @param req 审批请求
     */
    void approveFriendRequest(ApproveFriendRequestReq req);

    /**
     * 标记好友申请为已读
     *
     * @param req 标记请求
     */
    void readFriendShipRequestReq(ReadFriendShipRequestReq req);

    /**
     * 获取好友申请列表
     *
     * @param fromId 申请人ID
     * @param appId  应用ID
     * @return 好友申请列表
     */
    List<ImFriendShipRequestEntity> getFriendRequest(String fromId, Integer appId);
}