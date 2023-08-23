/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.model.resp.AddMemberResp;
import com.sj.im.service.group.model.resp.GetRoleInGroupResp;

import java.util.Collection;
import java.util.List;

public interface ImGroupMemberService {
    /**
     * 导入群组成员
     */
    ResponseVO<List<AddMemberResp>> importGroupMember(ImportGroupMemberReq req);

    /**
     * 导入群组成员具体逻辑
     */
    ResponseVO<String> addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    /**
     * 获得群组中某成员的成员类型
     */
    ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId);

    /**
     * 获取群组中的所有成员信息
     */
    ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId);

    /**
     * 获取到指定用户加入的所有群的id
     */
    ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req);

    /**
     * 转让群主
     */
    ResponseVO<String> transferGroupMember(String owner, String groupId, Integer appId);

    /**
     * 拉人入群
     */
    ResponseVO<Object> addMember(AddGroupMemberReq req);

    /**
     * 踢人出群
     */
    ResponseVO<String> removeMember(RemoveGroupMemberReq req);

    /**
     * 踢人出群具体逻辑
     */
    ResponseVO<String> removeGroupMember(String groupId, Integer appId, String memberId);

    /**
     * 退出群聊
     */
    ResponseVO<String> exitGroup(ExitGroupReq req);

    /**
     * 修改群成员信息
     */
    ResponseVO<String> updateGroupMember(UpdateGroupMemberReq req);

    /**
     * 禁言群成员
     */
    ResponseVO<String> speak(SpeakMemberReq req);

    /**
     * 获取指定群组的所有的成员Id
     */
    List<String> getGroupMemberId(String groupId, Integer appId);

    /**
     * 获取群组中的管理员具体信息
     */
    List<GroupMemberDto> getGroupManager(String groupId, Integer appId);

    /**
     * 增量同步用户加入的群组
     */
    ResponseVO<Collection<String>> syncMemberJoinedGroup(String operator, Integer appId);
}