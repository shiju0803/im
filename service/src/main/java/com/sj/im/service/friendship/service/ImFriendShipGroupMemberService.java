/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.common.ResponseVO;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;

public interface ImFriendShipGroupMemberService {

    /**
     * 添加组内成员
     */
    ResponseVO<Object> addGroupMember(AddFriendShipGroupMemberReq req);

    /**
     * 删除组内成员
     */
    ResponseVO<Object> delGroupMember(DeleteFriendShipGroupMemberReq req);

    /**
     * 添加组内成员具体逻辑
     */
    int doAddGroupMember(Long groupId, String toId);

    /**
     * 清空组内所有成员
     */
    ResponseVO<Integer> clearGroupMember(Long groupId);
}