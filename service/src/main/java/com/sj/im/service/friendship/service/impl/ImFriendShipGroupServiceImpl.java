/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务类
 */
@Service
@Slf4j
public class ImFriendShipGroupServiceImpl implements ImFriendShipGroupService {
    @Resource
    private ImFriendShipGroupMapper imFriendShipGroupMapper;
    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    /**
     * 添加分组
     */
    @Override
    public ResponseVO<String> addGroup(AddFriendShipGroupReq req) {
        LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupEntity::getGroupName, req.getGroupName());
        lqw.eq(ImFriendShipGroupEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipGroupEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);

        if (ObjectUtil.isNotNull(entity)) {
            // 好友分组已经存在
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        // 写入db
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setFromId(req.getFromId());
        try {
            int result = imFriendShipGroupMapper.insert(insert);
            if (result != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
        } catch (DuplicateKeyException e) {
            log.error("好友分组创建失败：分组已存在");
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        //TODO TCP通知

        return ResponseVO.successResponse();
    }

    /**
     * 删除分组
     */
    @Override
    public ResponseVO<String> deleteGroup(DeleteFriendShipGroupReq req) {
        for (String groupName : req.getGroupName()) {
            LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImFriendShipGroupEntity::getGroupName, groupName);
            lqw.eq(ImFriendShipGroupEntity::getAppId, req.getAppId());
            lqw.eq(ImFriendShipGroupEntity::getFromId, req.getFromId());
            lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);

            if (ObjectUtil.isNotNull(entity)) {
                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setGroupId(entity.getGroupId());

                // 逻辑删除
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendShipGroupMapper.updateById(update);
                // 清空组成员
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());

                //TODO TCP通知
            }
        }
        return ResponseVO.successResponse();
    }

    /**
     * 获取分组
     *
     * @param fromId    用户id
     * @param groupName 分组名称
     * @param appId     appId
     */
    @Override
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId) {
        LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupEntity::getGroupName, groupName);
        lqw.eq(ImFriendShipGroupEntity::getFromId, fromId);
        lqw.eq(ImFriendShipGroupEntity::getAppId, appId);
        lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    /**
     * 更新序列
     *
     * @param fromId
     * @param groupName
     * @param appId
     */
    @Override
    public Long updateSeq(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

//        long seq = redisSeq.doGetSeq(appId + ":" + Constants.SeqConstants.FriendshipGroup);

        ImFriendShipGroupEntity group = new ImFriendShipGroupEntity();
        group.setGroupId(entity.getGroupId());
//        group.setSequence(seq);
        imFriendShipGroupMapper.updateById(group);
        return 1L;
    }
}
