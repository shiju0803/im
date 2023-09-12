/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.sj.im.service.group.entry.ImGroupMemberEntity;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.AddMemberResp;
import com.sj.im.service.group.web.resp.GetRoleInGroupResp;

import java.util.List;

/**
 * 群成员服务类
 *
 * @author ShiJu
 * @version 1.0
 */
public interface ImGroupMemberService extends IMppService<ImGroupMemberEntity> {
    /**
     * 导入群组成员
     */
    List<AddMemberResp> importGroupMember(ImportGroupMemberReq req);

    /**
     * 导入群组成员具体逻辑
     */
    void addGroupMember(String groupId, Integer appId, GroupMemberDto dto);

    /**
     * 获得群组中某成员的成员类型
     */
    GetRoleInGroupResp getRoleInGroupOne(String groupId, String memberId, Integer appId);

    /**
     * 获取群组中的所有成员信息
     */
    List<GroupMemberDto> getGroupMember(String groupId, Integer appId);

    /**
     * 获取到指定用户加入的所有群的id
     */
    List<String> getMemberJoinedGroup(GetJoinedGroupReq req);

    /**
     * 转让群主
     */
    void transferGroupMember(String owner, String groupId, Integer appId);

    /**
     * 拉人入群
     */
    List<AddMemberResp> addMember(AddGroupMemberReq req);

    /**
     * 踢人出群
     */
    void removeMember(RemoveGroupMemberReq req);

    /**
     * 踢人出群具体逻辑
     */
    void removeGroupMember(String groupId, Integer appId, String memberId);

    /**
     * 退出群聊
     */
    void exitGroup(ExitGroupReq req);

    /**
     * 修改群成员信息
     */
    void updateGroupMember(UpdateGroupMemberReq req);

    /**
     * 禁言群成员
     */
    void speak(SpeakMemberReq req);

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
    List<String> syncMemberJoinedGroup(String operator, Integer appId);
}