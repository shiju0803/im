/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.sj.im.codec.pack.friendship.AddFriendGroupMemberPack;
import com.sj.im.codec.pack.friendship.DeleteFriendGroupMemberPack;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.enums.exception.FriendShipErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.service.friendship.entry.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.entry.ImFriendShipGroupMemberEntity;
import com.sj.im.service.friendship.mapper.ImFriendShipGroupMemberMapper;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupMemberReq;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组成员业务类
 */
@Service
@Slf4j
public class ImFriendShipGroupMemberServiceImpl extends MppServiceImpl<ImFriendShipGroupMemberMapper, ImFriendShipGroupMemberEntity> implements ImFriendShipGroupMemberService {
    @Resource
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;
    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private ImFriendShipGroupMemberService thisService;
    @Resource
    private MessageHelper messageHelper;

    /**
     * 添加组内成员
     */
    @Override
    public List<String> addGroupMember(AddFriendShipGroupMemberReq req) {
        // 判断该组是否合法
        ImFriendShipGroupEntity group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (ObjectUtil.isNull(group)) {
            throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ImUserDataEntity singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            // 新增组的成员的话，就要判断一下每个成员是否都合法
            if (ObjectUtil.isNull(singleUserInfo)) {
                int i = thisService.doAddGroupMember(group.getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }

        Long seq = imFriendShipGroupService.updateSeq(req.getFromId(), req.getGroupName(), req.getAppId());
        // TCP通知
        AddFriendGroupMemberPack pack = new AddFriendGroupMemberPack();
        pack.setFromId(req.getFromId());
        pack.setGroupName(req.getGroupName());
        pack.setToIds(successId);
        pack.setSequence(seq);
        messageHelper.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_ADD,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return successId;
    }

    /**
     * 删除组内成员
     */
    @Override
    public List<String> delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ImFriendShipGroupEntity group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (ObjectUtil.isNull(group)) {
            throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            // 删除组成员的时候，也要判断一下组成员的合法性
            ImUserDataEntity singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (ObjectUtil.isNull(singleUserInfo)) {
                int i = deleteGroupMember(group.getGroupId(), req.getToIds());
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }
        // TCP通知
        DeleteFriendGroupMemberPack pack = new DeleteFriendGroupMemberPack();
        pack.setFromId(req.getFromId());
        pack.setGroupName(req.getGroupName());
        pack.setToIds(successId);
        messageHelper.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_DELETE,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        return successId;
    }

    /**
     * 添加组内成员具体逻辑
     *
     * @param groupId 分组id
     * @param toId    好友id
     */
    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity entity = new ImFriendShipGroupMemberEntity();
        entity.setGroupId(groupId);
        entity.setToId(toId);
        try {
            return imFriendShipGroupMemberMapper.insert(entity);
        } catch (Exception e) {
            log.error("添加分组成员失败：", e);
            return 0;
        }
    }

    /**
     * 清空组内所有成员
     *
     * @param groupId 分组id
     */
    @Override
    public int clearGroupMember(Long groupId) {
        LambdaQueryWrapper<ImFriendShipGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupMemberEntity::getGroupId, groupId);
        return imFriendShipGroupMemberMapper.delete(lqw);
    }

    /**
     * 删除组成员具体逻辑
     */
    private int deleteGroupMember(Long groupId, List<String> toIds) {
        LambdaQueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImFriendShipGroupMemberEntity::getGroupId, groupId);
        queryWrapper.in(ImFriendShipGroupMemberEntity::getToId, toIds);
        try {
            return imFriendShipGroupMemberMapper.delete(queryWrapper);
        } catch (Exception e) {
            log.error("删除分组成员失败", e);
            return 0;
        }
    }
}
