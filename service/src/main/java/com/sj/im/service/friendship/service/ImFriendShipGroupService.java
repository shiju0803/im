/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupReq;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务接口
 */
public interface ImFriendShipGroupService {

    /**
     * 添加分组
     */
    ResponseVO<String> addGroup(AddFriendShipGroupReq req);

    /**
     * 删除分组
     */
    ResponseVO<String> deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取分组
     */
    ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

    /**
     * 更新序列
     */
    Long updateSeq(String fromId, String groupName, Integer appId);
}