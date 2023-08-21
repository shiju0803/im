/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sj.im.common.enums.CheckFriendShipTypeEnum;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.FriendShipStatusEnum;
import com.sj.im.common.enums.ResponseVO;
import com.sj.im.common.model.RequestBase;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.sj.im.service.friendship.model.req.*;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友关系业务类
 */
@Service
@Slf4j
public class ImFriendShipServiceImpl implements ImFriendShipService {
    @Resource
    private ImFriendShipMapper imFriendShipMapper;

    @Resource
    private ImUserService imUserService;

    /**
     * 导入关系链
     *
     * @param req 添加的好友数据
     */
    @Override
    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req) {
        // 导入数据数量的限制
        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (ImportFriendShipReq.ImportFriendDto dto : req.getFriendItem()) {
            // 数据填充
            ImFriendShipEntity en = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, en);
            en.setAppId(req.getAppId());
            en.setFromId(req.getFromId());

            try {
                int insert = imFriendShipMapper.insert(en);
                if (insert == 1) {
                    successId.add(dto.getToId());
                } else {
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                log.error("导入失败: 好友id:{}", dto.getToId(), e);
                errorId.add(dto.getToId());
            }
        }

        ImportFriendShipResp resp = new ImportFriendShipResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return ResponseVO.successResponse(resp);
    }

    /**
     * 添加好友
     *
     * @param req 添加的好友数据
     */
    @Override
    public ResponseVO<ImUserDataEntity> addFriendShip(FriendShipReq req) {
        // 判断好友双方的合法性
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        return doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
    }

    /**
     * 修改好友
     *
     * @param req 更新的好友数据
     */
    @Override
    public ResponseVO<ImUserDataEntity> updateFriend(FriendShipReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }

        return doUpateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    /**
     * 删除好友
     *
     * @param req 删除的好友数据
     */
    @Override
    public ResponseVO<String> deleteFriend(FriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getStatus, FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, lqw);

        //TODO TCP通知

        return ResponseVO.successResponse();
    }

    /**
     * 删除所有好友
     *
     * @param req 请求参数
     */
    @Override
    public ResponseVO<String> deleteAllFriend(FriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getStatus, FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, lqw);

        //TODO TCP通知

        return ResponseVO.successResponse();
    }

    /**
     * 拉取指定好友信息
     *
     * @param req 请求参数
     */
    @Override
    public ResponseVO<ImFriendShipEntity> getRelation(GetRelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            return ResponseVO.errorResponse(FriendShipErrorCode.REPEAT_SHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    /**
     * 拉取所有好友信息
     *
     * @param req 请求参数
     */
    @Override
    public ResponseVO<List<ImFriendShipEntity>> getAllFriend(GetRelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        List<ImFriendShipEntity> lists = imFriendShipMapper.selectList(lqw);
        return ResponseVO.successResponse(lists);
    }

    /**
     * 校验好友关系
     *
     * @param req 请求参数
     */
    @Override
    public ResponseVO<List<CheckFriendShipResp>> checkFriendShip(CheckFriendShipReq req) {
        // 双向校验的修改
        // 1、先是把req中的所有的toIds都转化为key为属性，value为0的map
        Map<String, Integer> result = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;

        if (ObjectUtil.equal(req.getCheckType(), CheckFriendShipTypeEnum.SINGLE.getType())) {
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }

        // 2、将复杂sql查询出来的数据转换为map
        Map<String, Integer> collect = resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));

        // 3、最后比对之前result中和collect是否完全相同，collect中没有的话，就将这个数据封装起来放到resp中去
        for (String toId : result.keySet()) {
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setStatus(result.get(toId));
                resp.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }

    private ResponseVO doUpateFriend(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource()).set(ImFriendShipEntity::getExtra, dto.getExtra()).set(ImFriendShipEntity::getRemark, dto.getRemark()).eq(ImFriendShipEntity::getAppId, appId).eq(ImFriendShipEntity::getToId, dto.getToId()).eq(ImFriendShipEntity::getFromId, fromId);

        imFriendShipMapper.update(null, updateWrapper);
        return ResponseVO.errorResponse();
    }

    // 添加好友的逻辑
    @Transactional
    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        // A-B
        // Friend表插入 A 和 B 两条记录
        // 查询是否有记录存在，如果存在则判断状态，如果是已经添加，则提示已经添加了，如果是未添加，则修改状态

        // 第一条数据的插入
        LambdaQueryWrapper<ImFriendShipEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImFriendShipEntity::getAppId, appId);
        qw.eq(ImFriendShipEntity::getFromId, fromId);
        qw.eq(ImFriendShipEntity::getToId, dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(qw);

        // 不存在这条消息
        if (ObjectUtil.isNull(fromItem)) {
            // 直接添加
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 存在这条消息，去根据状态做判断
            // 如果已添加，则提示已添加
            if (ObjectUtil.equal(fromItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }

            //如果是未添加，则修改状态
            if (ObjectUtil.notEqual(fromItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode())) {
                ImFriendShipEntity update = new ImFriendShipEntity();
                if (StringUtils.isNotEmpty(dto.getAddSource())) {
                    update.setAddSource(dto.getAddSource());
                }
                if (StringUtils.isNotEmpty(dto.getRemark())) {
                    update.setRemark(dto.getRemark());
                }
                if (StringUtils.isNotEmpty(dto.getExtra())) {
                    update.setExtra(dto.getExtra());
                }
                update.setUpdateTime(new Date());
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int res = imFriendShipMapper.update(update, qw);
                if (res != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        // 第二条数据的插入
        LambdaQueryWrapper<ImFriendShipEntity> qw1 = new LambdaQueryWrapper<>();
        qw1.eq(ImFriendShipEntity::getAppId, appId);
        qw1.eq(ImFriendShipEntity::getFromId, dto.getToId());
        qw1.eq(ImFriendShipEntity::getToId, fromId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(qw1);

        // 不存在就直接添加
        if (ObjectUtil.isNull(toItem)) {
            toItem = new ImFriendShipEntity();
            toItem.setAppId(appId);
            toItem.setFromId(dto.getToId());
            toItem.setToId(fromId);
            BeanUtils.copyProperties(dto, toItem);
            toItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(toItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 存在就判断状态
            if (ObjectUtil.equal(toItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            } else {
                ImFriendShipEntity entity = new ImFriendShipEntity();
                entity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                imFriendShipMapper.update(entity, qw1);
            }
        }

        // TODO TCP通知
        return ResponseVO.successResponse();
    }
}
