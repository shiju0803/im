/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.im.codec.pack.friendship.ApproverFriendRequestPack;
import com.sj.im.codec.pack.friendship.ReadAllFriendRequestPack;
import com.sj.im.common.ResponseVO;
import com.sj.im.common.enums.ApproveFriendRequestStatusEnum;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.sj.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.model.req.FriendDto;
import com.sj.im.service.friendship.model.req.FriendShipReq;
import com.sj.im.service.friendship.service.ImFriendShipRequestService;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.helper.MessageHelper;
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
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {
    @Resource
    private ImFriendShipRequestMapper imFriendShipRequestMapper;
    @Resource
    private ImFriendShipService imFriendShipService;
    @Resource
    private MessageHelper messageHelper;

    /**
     * 添加好友请求
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public ResponseVO<String> addFriendshipRequest(FriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipRequestEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipRequestEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipRequestEntity::getToId, req.getToItem().getToId());
        ImFriendShipRequestEntity entity = imFriendShipRequestMapper.selectOne(lqw);

        FriendDto dto = req.getToItem();

        // 没有这条好友请求的记录就直接新建
        if (ObjectUtil.isNull(entity)) {
            entity = new ImFriendShipRequestEntity();
            entity.setAddSource(dto.getAddSource());
            entity.setAddWording(dto.getAddWorking());
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            entity.setToId(dto.getToId());
            entity.setReadStatus(0);
            entity.setApproveStatus(0);
            entity.setRemark(dto.getRemark());
            entity.setCreateTime(new Date());
            int insert = imFriendShipRequestMapper.insert(entity);
            if (insert != 1) {
                return ResponseVO.errorResponse();
            }
        } else {
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

            entity.setApproveStatus(0);
            entity.setReadStatus(0);
            imFriendShipRequestMapper.updateById(entity);
        }

        // 发送好友申请的tcp给接收方
        messageHelper.sendToUser(dto.getToId(), null, "",
                FriendshipEventCommand.FRIEND_REQUEST, entity, req.getAppId());

        return ResponseVO.successResponse();
    }

    /**
     * 审批好友请求
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public ResponseVO<String> approveFriendRequest(ApproveFriendRequestReq req) {
        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (ObjectUtil.isNull(imFriendShipRequestEntity)) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (ObjectUtil.notEqual(req.getOperator(), imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCode.NOT_APPROVAL_OTHER_MAN_REQUEST);
        }

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        // 这里审批是指同意或者拒绝，所以要写活
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(new Date());
        update.setId(req.getId());
        imFriendShipRequestMapper.updateById(update);


        // 如果是统一的话，就可以直接调用添加好友的逻辑了
        if (ObjectUtil.equal(ApproveFriendRequestStatusEnum.AGREE.getCode(), req.getStatus())) {
            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWorking(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            ResponseVO<String> responseVO = imFriendShipService.doAddFriend(req
                    , imFriendShipRequestEntity.getFromId(), dto, req.getAppId());
            if(!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()){
                return responseVO;
            }
        }

        // 通知审批人的其他端
        ApproverFriendRequestPack approverFriendRequestPack = new ApproverFriendRequestPack();
        approverFriendRequestPack.setStatus(req.getStatus());
        approverFriendRequestPack.setId(req.getId());
        messageHelper.sendToUser(imFriendShipRequestEntity.getToId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_REQUEST_APPROVER, approverFriendRequestPack, req.getAppId());

        return ResponseVO.successResponse();
    }

    /**
     * 已读好友请求
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public ResponseVO<String> readFriendShipRequestReq(FriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImFriendShipRequestEntity::getAppId, req.getAppId());
        query.eq(ImFriendShipRequestEntity::getToId, req.getFromId());

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setReadStatus(1);

        imFriendShipRequestMapper.update(update, query);
        // TCP通知
        ReadAllFriendRequestPack readAllFriendRequestPack = new ReadAllFriendRequestPack();
        readAllFriendRequestPack.setFromId(req.getFromId());
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_REQUEST_READ, readAllFriendRequestPack, req.getAppId());

        return ResponseVO.successResponse();
    }

    /**
     * 获得好友请求
     *
     * @param req 请求参数
     */
    @Override
    public ResponseVO<List<ImFriendShipRequestEntity>> getFriendRequest(FriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipRequestEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipRequestEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipRequestEntity::getToId, req.getFromId());
        List<ImFriendShipRequestEntity> requestEntities = imFriendShipRequestMapper.selectList(lqw);
        return ResponseVO.successResponse(requestEntities);
    }
}
