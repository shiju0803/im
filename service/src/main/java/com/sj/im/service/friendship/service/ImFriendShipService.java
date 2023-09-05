/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.model.RequestBase;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.entry.ImFriendShipEntity;
import com.sj.im.service.friendship.web.req.*;
import com.sj.im.service.friendship.web.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.web.resp.ImportFriendShipResp;

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
    ImportFriendShipResp importFriendShip(ImportFriendShipReq req);

    /**
     * 添加好友
     */
    void addFriend(AddFriendReq req);

    /**
     * 修改好友
     */
    void updateFriend(UpdateFriendReq req);

    /**
     * 修改好友具体逻辑
     */
    void doUpdateFriend(String fromId, FriendDto dto, Integer appId);

    /**
     * 删除好友
     */
    void deleteFriend(DeleteFriendReq req);

    /**
     * 删除所有好友
     */
    void deleteAllFriend(DeleteFriendReq req);

    /**
     * 拉取所有好友信息
     */
    List<ImFriendShipEntity> getAllFriend(GetAllFriendShipReq req);

    /**
     * 拉取指定好友信息
     */
    ImFriendShipEntity getRelation(GetRelationReq req);

    /**
     * 添加好友具体逻辑
     */
    void doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    /**
     * 校验好友关系
     */
    List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req);

    /**
     * 加入黑名单
     */
    void addBlack(AddFriendShipBlackReq req);

    /**
     * 拉出黑名单
     */
    void deleteBlack(DeleteBlackReq req);

    /**
     * 校验黑名单
     */
    List<CheckFriendShipResp> checkFriendBlack(CheckFriendShipReq req);

    /**
     * 同步好友列表信息
     */
    SyncResp<ImFriendShipEntity> syncFriendShipList(SyncReq req);

    /**
     * 获取指定用户的所有的好友id
     */
    List<String> getAllFriendId(String userId, Integer appId);
}
