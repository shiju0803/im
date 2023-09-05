/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.service.friendship.entry.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupReq;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务接口
 */
public interface ImFriendShipGroupService {

    /**
     * 添加好友分组
     *
     * @param req 添加好友分组的请求对象
     */
    void addGroup(AddFriendShipGroupReq req);

    /**
     * 删除好友分组
     *
     * @param req 删除好友分组的请求对象
     */
    void deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取好友分组
     *
     * @param fromId    用户ID
     * @param groupName 分组名称
     * @param appId     应用ID
     * @return 返回好友分组实体对象
     */
    ImFriendShipGroupEntity getGroup(String fromId, String groupName, Integer appId);

    /**
     * 更新好友分组的排序
     *
     * @param fromId    用户ID
     * @param groupName 分组名称
     * @param appId     应用ID
     * @return 返回更新后的排序值
     */
    Long updateSeq(String fromId, String groupName, Integer appId);
}