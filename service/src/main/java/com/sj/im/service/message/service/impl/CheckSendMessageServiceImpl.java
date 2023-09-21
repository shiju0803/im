/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.enums.*;
import com.sj.im.common.enums.exception.FriendShipErrorCode;
import com.sj.im.common.enums.exception.GroupErrorCode;
import com.sj.im.common.enums.exception.MessageErrorCode;
import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.friendship.entry.ImFriendShipEntity;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.friendship.web.req.GetRelationReq;
import com.sj.im.service.group.entry.ImGroupEntity;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.group.web.resp.GetRoleInGroupResp;
import com.sj.im.service.message.service.CheckSendMessageService;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 发送消息校验逻辑
 *
 * @author ShiJu
 * @version 1.0
 */
@Service
public class CheckSendMessageServiceImpl implements CheckSendMessageService {
    @Resource
    private ImUserService imUserService;
    @Resource
    private ImFriendShipService imFriendShipService;
    @Resource
    private ImGroupService imGroupService;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private AppConfig appConfig;

    /**
     * 检查用户是否被禁用和被禁言
     *
     * @param fromId 发送方id
     * @param appId  接收方id
     */
    @Override
    public void checkSenderForbiddenAndMute(String fromId, Integer appId) {
        ImUserDataEntity user = imUserService.getSingleUserInfo(fromId, appId);
        if (ObjectUtil.isNull(user)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        if (ObjectUtil.equal(user.getForbiddenFlag(), UserForbiddenFlagEnum.FORBIDDEN)) {
            throw new BusinessException(MessageErrorCode.SENDER_IS_FORBIDDEN);
        }

        if (ObjectUtil.equal(user.getSilentFlag(), UserSilentFlagEnum.MUTE)) {
            throw new BusinessException(MessageErrorCode.SENDER_IS_MUTE);
        }
    }

    /**
     * 判断好友关系
     *
     * @param fromId 发送方id
     * @param toId   接收方id
     * @param appId  应用id
     */
    @Override
    public void checkFriendShip(String fromId, String toId, Integer appId) {
        // 判断双方好友记录是否存在
        if (appConfig.isSendMessageCheckFriend()) {
            GetRelationReq fromReq = new GetRelationReq();
            fromReq.setFromId(fromId);
            fromReq.setToId(toId);
            fromReq.setAppId(appId);
            ImFriendShipEntity fromRelation = imFriendShipService.getRelation(fromReq);

            GetRelationReq toReq = new GetRelationReq();
            toReq.setFromId(toId);
            toReq.setToId(fromId);
            toReq.setAppId(appId);
            ImFriendShipEntity toRelation = imFriendShipService.getRelation(toReq);

            // 判断好友关系记录是否正常
            if (ObjectUtil.notEqual(fromRelation.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                throw new BusinessException(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
            if (ObjectUtil.notEqual(toRelation.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                throw new BusinessException(FriendShipErrorCode.FRIEND_IS_DELETED);
            }

            // 判断是否在黑名单里面
            if (appConfig.isSendMessageCheckBlack()) {
                // 检查发送者是否在接收者的黑名单中
                if (ObjectUtil.notEqual(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode(), fromRelation.getBlack())) {
                    throw new BusinessException(FriendShipErrorCode.FRIEND_IS_BLACK);
                }
                // 检查接收者是否在发送者的黑名单中
                if (ObjectUtil.notEqual(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode(), toRelation.getBlack())) {
                    throw new BusinessException(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
                }
            }
        }
    }

    /**
     * 检查用户是否被禁用和被禁言
     *
     * @param fromId 发送方id
     * @param appId  接收方id
     */
    @Override
    public void checkGroupMessage(String fromId, String groupId, Integer appId) {
        checkSenderForbiddenAndMute(fromId, appId);
        // 判断群逻辑
        ImGroupEntity imGroup = imGroupService.getGroup(groupId, appId);
        // 判断群成员是否在群内
        GetRoleInGroupResp roleInGroup = imGroupMemberService.getRoleInGroupOne(groupId, fromId, appId);
        // 判断群是否被禁言
        // 如果禁言 只有群管理和群主可以发言
        if (ObjectUtil.equal(imGroup.getMute(), GroupMuteTypeEnum.MUTE.getCode()) && (
                ObjectUtil.notEqual(roleInGroup.getRole(), GroupMemberRoleEnum.MANAGER.getCode())
                        || ObjectUtil.notEqual(roleInGroup.getRole(), GroupMemberRoleEnum.OWNER.getCode()))) {
            throw new BusinessException(GroupErrorCode.THIS_GROUP_IS_MUTE);
        }
        if (ObjectUtil.isNotNull(roleInGroup.getSpeakDate())
                && roleInGroup.getSpeakDate() > System.currentTimeMillis()) {
            throw new BusinessException(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }
    }
}
