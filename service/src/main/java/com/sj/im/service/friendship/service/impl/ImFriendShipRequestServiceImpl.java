/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sj.im.codec.pack.friendship.ApproverFriendRequestPack;
import com.sj.im.codec.pack.friendship.ReadAllFriendRequestPack;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.ApproveFriendRequestStatusEnum;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.enums.exception.FriendShipErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.service.friendship.entry.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.mapper.ImFriendShipRequestMapper;
import com.sj.im.service.friendship.service.ImFriendShipRequestService;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.friendship.web.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.web.req.FriendDto;
import com.sj.im.service.friendship.web.req.ReadFriendShipRequestReq;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.util.RedisSeq;
import com.sj.im.service.util.WriteUserSeq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友申请业务类
 */
@Service
@Slf4j
public class ImFriendShipRequestServiceImpl extends ServiceImpl<ImFriendShipRequestMapper, ImFriendShipRequestEntity> implements ImFriendShipRequestService {
    @Resource
    private ImFriendShipRequestMapper imFriendShipRequestMapper;
    @Resource
    private ImFriendShipService imFriendShipService;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private WriteUserSeq writeUserSeq;
    @Resource
    private RedisSeq redisSeq;

    @Override
    public void addFriendshipRequest(String fromId, FriendDto dto, Integer appId) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipRequestEntity::getAppId, appId);
        lqw.eq(ImFriendShipRequestEntity::getFromId, fromId);
        lqw.eq(ImFriendShipRequestEntity::getToId, dto.getToId());
        ImFriendShipRequestEntity entity = imFriendShipRequestMapper.selectOne(lqw);

        long seq = redisSeq.doGetSeq(appId + ":" + SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        // 没有这条好友请求的记录就直接新建
        if (ObjectUtil.isNull(entity)) {
            entity = new ImFriendShipRequestEntity();
            entity.setAddSource(dto.getAddSource());
            entity.setAddWording(dto.getAddWorking());
            entity.setSequence(seq);
            entity.setAppId(appId);
            entity.setFromId(fromId);
            entity.setToId(dto.getToId());
            entity.setReadStatus(0);
            entity.setApproveStatus(0);
            entity.setRemark(dto.getRemark());
            entity.setCreateTime(new Date());
            int insert = imFriendShipRequestMapper.insert(entity);
            if (insert != 1) {
                throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_REQUEST_ERROR);
            }
        }

        // 有这条好友请求的记录，就更新记录内容和时间
        if (CharSequenceUtil.isNotBlank(dto.getAddSource())) {
            entity.setAddSource(dto.getAddSource());
        }
        if (CharSequenceUtil.isNotBlank(dto.getRemark())) {
            entity.setRemark(dto.getRemark());
        }
        if (CharSequenceUtil.isNotBlank(dto.getAddWorking())) {
            entity.setAddWording(dto.getAddWorking());
        }

        entity.setSequence(seq);
        entity.setApproveStatus(0);
        entity.setReadStatus(0);
        imFriendShipRequestMapper.updateById(entity);
        writeUserSeq.writeUserSeq(appId, dto.getToId(), SeqConstants.FRIENDSHIP_REQUEST_SEQ, seq);
        // 发送好友申请的tcp给接收方
        messageHelper.sendToUser(dto.getToId(), null, "", FriendshipEventCommand.FRIEND_REQUEST, entity, appId);
    }

    /**
     * 审批好友请求
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public void approveFriendRequest(ApproveFriendRequestReq req) {
        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (ObjectUtil.isNull(imFriendShipRequestEntity)) {
            throw new BusinessException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (ObjectUtil.notEqual(req.getOperator(), imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new BusinessException(FriendShipErrorCode.NOT_APPROVAL_OTHER_MAN_REQUEST);
        }

        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        // 这里审批是指同意或者拒绝，所以要写活
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(new Date());
        update.setSequence(seq);
        update.setId(req.getId());
        imFriendShipRequestMapper.updateById(update);
        writeUserSeq.writeUserSeq(req.getAppId(), req.getOperator(), SeqConstants.FRIENDSHIP_REQUEST_SEQ, seq);

        // 如果是统一的话，就可以直接调用添加好友的逻辑了
        if (ObjectUtil.equal(ApproveFriendRequestStatusEnum.AGREE.getCode(), req.getStatus())) {
            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWorking(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            imFriendShipService.doAddFriend(req, imFriendShipRequestEntity.getFromId(), dto, req.getAppId());
        }

        // 通知审批人的其他端
        ApproverFriendRequestPack approverFriendRequestPack = new ApproverFriendRequestPack();
        approverFriendRequestPack.setStatus(req.getStatus());
        approverFriendRequestPack.setSequence(seq);
        approverFriendRequestPack.setId(req.getId());
        messageHelper.sendToUser(imFriendShipRequestEntity.getToId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_REQUEST_APPROVE, approverFriendRequestPack, req.getAppId());
    }

    /**
     * 已读好友请求
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public void readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImFriendShipRequestEntity::getAppId, req.getAppId());
        query.eq(ImFriendShipRequestEntity::getToId, req.getFromId());

        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setReadStatus(1);
        update.setSequence(seq);
        imFriendShipRequestMapper.update(update, query);
        writeUserSeq.writeUserSeq(req.getAppId(), req.getOperator(), SeqConstants.FRIENDSHIP_REQUEST_SEQ, seq);
        // TCP通知
        ReadAllFriendRequestPack readAllFriendRequestPack = new ReadAllFriendRequestPack();
        readAllFriendRequestPack.setFromId(req.getFromId());
        readAllFriendRequestPack.setSequence(seq);
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_REQUEST_READ, readAllFriendRequestPack, req.getAppId());
    }

    @Override
    public List<ImFriendShipRequestEntity> getFriendRequest(String fromId, Integer appId) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipRequestEntity::getAppId, appId);
        lqw.eq(ImFriendShipRequestEntity::getToId, fromId);
        return imFriendShipRequestMapper.selectList(lqw);
    }
}
