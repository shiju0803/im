/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.model.req.CheckFriendShipReq;
import com.sj.im.service.friendship.model.req.FriendShipReq;
import com.sj.im.service.friendship.model.req.GetRelationReq;
import com.sj.im.service.friendship.model.req.ImportFriendShipReq;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;
import com.sj.im.service.user.dao.ImUserDataEntity;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友业务接口
 */
public interface ImFriendShipService {
    /**
     * 导入关系链
     */
    ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);

    /**
     * 添加好友
     */
    ResponseVO<ImUserDataEntity> addFriendShip(FriendShipReq req);

    /**
     * 修改好友
     */
    ResponseVO<ImUserDataEntity> updateFriend(FriendShipReq req);

    /**
     * 删除好友
     */
    ResponseVO<String> deleteFriend(FriendShipReq req);

    /**
     * 删除所有好友
     */
    ResponseVO<String> deleteAllFriend(FriendShipReq req);

    /**
     * 拉取指定好友信息
     */
    ResponseVO<ImFriendShipEntity> getRelation(GetRelationReq req);

    /**
     * 拉取所有好友信息
     */
    ResponseVO<List<ImFriendShipEntity>> getAllFriend(GetRelationReq req);

    /**
     * 校验好友关系
     */
    ResponseVO<List<CheckFriendShipResp>> checkFriendShip(CheckFriendShipReq req);
}
