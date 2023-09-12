/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.group.AddGroupMemberPack;
import com.sj.im.codec.pack.group.RemoveGroupMemberPack;
import com.sj.im.codec.pack.group.UpdateGroupMemberPack;
import com.sj.im.common.ClientType;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.service.group.web.req.GroupMemberDto;
import com.sj.im.service.group.service.ImGroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 群组行为消息发送
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
public class GroupMessageHelper {
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ImGroupMemberService imGroupMemberService;

    public void producer(String userId, Command command, Object data, ClientInfo clientInfo) {
        JSONObject object = JSONUtil.parseObj(data);
        String groupId = object.getStr("groupId");
        List<String> groupMemberId = imGroupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());

        if (ObjectUtil.equal(command, GroupEventCommand.ADDED_MEMBER)) {
            // 加入群聊需要发送给管理员和加入者本身
            doAddMember(userId, command, data, clientInfo, groupId, object);
        } else {
            if (ObjectUtil.equal(command, GroupEventCommand.DELETED_MEMBER)) {
                // 踢人出群需要发送给群内所有人和踢出者本身
                doDeleteMember(userId, command, data, clientInfo, object, groupId);
            } else {
                if (ObjectUtil.equal(command, GroupEventCommand.UPDATED_MEMBER)) {
                    // 修改群成员只需要发送给给管理员和修改者本身
                    doUpdateMember(userId, command, data, clientInfo, object, groupId);
                } else {
                    // 其他操作默认发给所有人
                    doOther(userId, command, data, clientInfo, groupMemberId);
                }
            }
        }
    }

    private void doOther(String userId, Command command, Object data, ClientInfo clientInfo,
                         List<String> groupMemberId) {
        for (String memberId : groupMemberId) {
            if (ObjectUtil.notEqual(clientInfo.getClientType(), ClientType.WEBAPI.getCode()) && ObjectUtil.equal(
                    memberId, userId)) {
                messageHelper.sendToUserExceptClient(memberId, command, data, clientInfo);
            } else {
                messageHelper.sendToUser(memberId, command, data, clientInfo.getAppId());
            }
        }
    }

    private void doUpdateMember(String userId, Command command, Object data, ClientInfo clientInfo, JSONObject object,
                                String groupId) {
        UpdateGroupMemberPack updateMember = BeanUtil.toBean(object, UpdateGroupMemberPack.class);
        String memberId = updateMember.getMemberId();
        List<GroupMemberDto> groupManager = imGroupMemberService.getGroupManager(groupId, clientInfo.getAppId());
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(memberId);
        groupManager.add(groupMemberDto);
        for (GroupMemberDto memberDto : groupManager) {
            if (ObjectUtil.notEqual(clientInfo.getClientType(), ClientType.WEBAPI.getCode()) && ObjectUtil.equal(
                    memberDto.getMemberId(), userId)) {
                messageHelper.sendToUserExceptClient(memberDto.getMemberId(), command, data, clientInfo);
            } else {
                messageHelper.sendToUser(memberDto.getMemberId(), command, data, clientInfo.getAppId());
            }
        }
    }

    private void doDeleteMember(String userId, Command command, Object data, ClientInfo clientInfo, JSONObject object,
                                String groupId) {
        RemoveGroupMemberPack removeMember = BeanUtil.toBean(object, RemoveGroupMemberPack.class);
        String member = removeMember.getMember();
        List<String> members = imGroupMemberService.getGroupMemberId(groupId, clientInfo.getAppId());
        members.add(member);
        for (String memberId : members) {
            if (ObjectUtil.notEqual(clientInfo.getClientType(), ClientType.WEBAPI.getCode()) && ObjectUtil.equal(
                    memberId, userId)) {
                messageHelper.sendToUserExceptClient(memberId, command, data, clientInfo);
            } else {
                messageHelper.sendToUser(memberId, command, data, clientInfo.getAppId());
            }
        }
    }

    private void doAddMember(String userId, Command command, Object data, ClientInfo clientInfo, String groupId,
                             JSONObject object) {
        List<GroupMemberDto> groupManager = imGroupMemberService.getGroupManager(groupId, clientInfo.getAppId());
        AddGroupMemberPack addGroupMemberPack = BeanUtil.toBean(object, AddGroupMemberPack.class);
        List<String> members = addGroupMemberPack.getMembers();
        for (GroupMemberDto manager : groupManager) {
            if (ObjectUtil.notEqual(clientInfo.getClientType(), ClientType.WEBAPI.getCode()) && ObjectUtil.equal(
                    manager.getMemberId(), userId)) {
                messageHelper.sendToUserExceptClient(manager.getMemberId(), command, data, clientInfo);
            } else {
                messageHelper.sendToUser(manager.getMemberId(), command, data, clientInfo.getAppId());
            }
        }

        for (String member : members) {
            if (ObjectUtil.notEqual(clientInfo.getClientType(), ClientType.WEBAPI.getCode()) && ObjectUtil.equal(member,
                                                                                                                 userId)) {
                messageHelper.sendToUserExceptClient(member, command, data, clientInfo);
            } else {
                messageHelper.sendToUser(member, command, data, clientInfo.getAppId());
            }
        }
    }
}
