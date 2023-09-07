/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.sj.im.codec.pack.friendship.AddFriendGroupPack;
import com.sj.im.codec.pack.friendship.DeleteFriendGroupPack;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.enums.exception.FriendShipErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.service.friendship.entry.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.mapper.ImFriendShipGroupMapper;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupReq;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.util.RedisSeq;
import com.sj.im.service.util.WriteUserSeq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务类
 */
@Service
@Slf4j
public class ImFriendShipGroupServiceImpl extends MppServiceImpl<ImFriendShipGroupMapper, ImFriendShipGroupEntity> implements ImFriendShipGroupService {
    @Resource
    private ImFriendShipGroupMapper imFriendShipGroupMapper;
    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private WriteUserSeq writeUserSeq;
    @Resource
    private RedisSeq redisSeq;

    /**
     * 添加分组
     */
    @Override
    public void addGroup(AddFriendShipGroupReq req) {
        LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupEntity::getGroupName, req.getGroupName());
        lqw.eq(ImFriendShipGroupEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipGroupEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);

        if (ObjectUtil.isNotNull(entity)) {
            // 好友分组已经存在
            throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        // 写入db
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_GROUP_SEQ);
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(new Date());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setFromId(req.getFromId());
        insert.setSequence(seq);
        try {
            int result = imFriendShipGroupMapper.insert(insert);
            if (result != 1) {
                throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
            if (CollUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setToIds(req.getToIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
            }
        } catch (DuplicateKeyException e) {
            log.error("好友分组创建失败：分组已存在");
            throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        // TCP通知
        AddFriendGroupPack addFriendGropPack = new AddFriendGroupPack();
        addFriendGropPack.setFromId(req.getFromId());
        addFriendGropPack.setGroupName(req.getGroupName());
        addFriendGropPack.setSequence(seq);
        messageHelper.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_ADD,
                addFriendGropPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        //写入seq
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_GROUP_SEQ, seq);
    }

    /**
     * 删除分组
     */
    @Override
    @Transactional
    public void deleteGroup(DeleteFriendShipGroupReq req) {
        for (String groupName : req.getGroupName()) {
            LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImFriendShipGroupEntity::getGroupName, groupName);
            lqw.eq(ImFriendShipGroupEntity::getAppId, req.getAppId());
            lqw.eq(ImFriendShipGroupEntity::getFromId, req.getFromId());
            lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);

            if (ObjectUtil.isNotNull(entity)) {
                long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_GROUP_SEQ);
                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setSequence(seq);
                update.setGroupId(entity.getGroupId());
                // 逻辑删除
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendShipGroupMapper.updateById(update);
                // 清空组成员
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());

                //TCP通知
                DeleteFriendGroupPack deleteFriendGroupPack = new DeleteFriendGroupPack();
                deleteFriendGroupPack.setFromId(req.getFromId());
                deleteFriendGroupPack.setGroupName(groupName);
                deleteFriendGroupPack.setSequence(seq);
                messageHelper.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_GROUP_DELETE,
                        deleteFriendGroupPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

                //写入seq
                writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_GROUP_SEQ, seq);
            }
        }
    }

    /**
     * 获取分组
     *
     * @param fromId    用户id
     * @param groupName 分组名称
     * @param appId     appId
     */
    @Override
    public ImFriendShipGroupEntity getGroup(String fromId, String groupName, Integer appId) {
        LambdaQueryWrapper<ImFriendShipGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipGroupEntity::getGroupName, groupName);
        lqw.eq(ImFriendShipGroupEntity::getFromId, fromId);
        lqw.eq(ImFriendShipGroupEntity::getAppId, appId);
        lqw.eq(ImFriendShipGroupEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return entity;
    }

    @Override
    public Long updateSeq(String fromId, String groupName, Integer appId) {
        LambdaQueryWrapper<ImFriendShipGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImFriendShipGroupEntity::getGroupName, groupName);
        query.eq(ImFriendShipGroupEntity::getAppId, appId);
        query.eq(ImFriendShipGroupEntity::getFromId, fromId);

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        long seq = redisSeq.doGetSeq(appId + ":" + SeqConstants.FRIENDSHIP_GROUP_SEQ);
        ImFriendShipGroupEntity group = new ImFriendShipGroupEntity();
        group.setGroupId(entity.getGroupId());
        group.setSequence(seq);
        imFriendShipGroupMapper.updateById(group);
        return seq;
    }
}
