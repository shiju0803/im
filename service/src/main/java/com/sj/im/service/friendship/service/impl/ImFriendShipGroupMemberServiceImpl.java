/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import com.sj.im.service.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import com.sj.im.service.user.dao.ImUserDataEntity;
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
public class ImFriendShipGroupMemberServiceImpl implements ImFriendShipGroupMemberService {
    @Resource
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;
    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private ImFriendShipGroupMemberService thisService;

    /**
     * 添加组内成员
     */
    @Override
    public ResponseVO<Object> addGroupMember(AddFriendShipGroupMemberReq req) {
        // 判断该组是否合法
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            // 新增组的成员的话，就要判断一下每个成员是否都合法
            if (singleUserInfo.isOk()) {
                int i = thisService.doAddGroupMember(group.getData().getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }

        Long seq = imFriendShipGroupService.updateSeq(req.getFromId(), req.getGroupName(), req.getAppId());

        //TODO TCP通知

        return ResponseVO.successResponse(successId);
    }

    /**
     * 删除组内成员
     */
    @Override
    public ResponseVO<Object> delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            // 删除组成员的时候，也要判断一下组成员的合法性
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (singleUserInfo.isOk()) {
                int i = deleteGroupMember(group.getData().getGroupId(), req.getToIds());
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }
        return ResponseVO.successResponse(successId);
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
    public ResponseVO<Integer> clearGroupMember(Long groupId) {
        LambdaQueryWrapper<ImFriendShipGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupMemberEntity::getGroupId, groupId);
        int delete = imFriendShipGroupMemberMapper.delete(lqw);
        return ResponseVO.successResponse(delete);
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
