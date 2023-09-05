/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.sj.im.service.friendship.web.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupMemberReq;

import java.util.List;

public interface ImFriendShipGroupMemberService {

    /**
     * 添加组内成员
     */
    List<String> addGroupMember(AddFriendShipGroupMemberReq req);

    /**
     * 删除组内成员
     */
    List<String> delGroupMember(DeleteFriendShipGroupMemberReq req);

    /**
     * 添加组内成员具体逻辑
     */
    int doAddGroupMember(Long groupId, String toId);

    /**
     * 清空组内所有成员
     */
    int clearGroupMember(Long groupId);
}