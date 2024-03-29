/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.sj.im.service.friendship.entry.ImFriendShipGroupMemberEntity;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupMemberReq;

import java.util.List;

/**
 * 好友分组成员业务接口
 *
 * @author ShiJu
 * @version 1.0
 */
public interface ImFriendShipGroupMemberService extends IMppService<ImFriendShipGroupMemberEntity> {

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