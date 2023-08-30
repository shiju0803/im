/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.ResponseVO;
import com.sj.im.common.model.RequestBase;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.model.req.*;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;

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
    ResponseVO<String> addFriend(FriendShipReq req);

    /**
     * 添加好友具体逻辑
     */
    ResponseVO<String> doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    /**
     * 修改好友
     */
    ResponseVO<String> updateFriend(FriendShipReq req);

    /**
     * 删除好友
     */
    ResponseVO<String> deleteFriend(RelationReq req);

    /**
     * 删除所有好友
     */
    ResponseVO<String> deleteAllFriend(FriendShipReq req);

    /**
     * 拉取指定好友信息
     */
    ResponseVO<ImFriendShipEntity> getRelation(RelationReq req);

    /**
     * 拉取所有好友信息
     */
    ResponseVO<List<ImFriendShipEntity>> getAllFriend(RelationReq req);

    /**
     * 校验好友关系
     */
    ResponseVO<List<CheckFriendShipResp>> checkFriendShip(CheckFriendShipReq req);

    /**
     * 加入黑名单
     */
    ResponseVO<String> addBlack(RelationReq req);

    /**
     * 拉出黑名单
     */
    ResponseVO<String> deleteBlack(RelationReq req);

    /**
     * 校验黑名单
     */
    ResponseVO<List<CheckFriendShipResp>> checkFriendBlack(CheckFriendShipReq req);

    /**
     * 同步好友列表信息
     */
    ResponseVO<SyncResp<ImFriendShipEntity>> syncFriendShipList(SyncReq req);

    /**
     * 获取指定用户的所有的好友id
     */
    List<String> getAllFriendId(String userId, Integer appId);
}
