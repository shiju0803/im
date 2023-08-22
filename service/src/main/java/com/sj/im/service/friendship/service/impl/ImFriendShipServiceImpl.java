/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sj.im.common.enums.CheckFriendShipTypeEnum;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.FriendShipStatusEnum;
import com.sj.im.common.enums.ResponseVO;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.model.RequestBase;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
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
import org.springframework.util.CollectionUtils;

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
    public ResponseVO<String> addFriendShip(FriendShipReq req) {
        // 判断好友双方的合法性
        String checkResult = checkUserData(req.getFromId(), req.getToItem().getToId(), req.getAppId());
        if (CharSequenceUtil.isBlank(checkResult)) {
            return ResponseVO.errorResponse(checkResult);
        }

        return doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
    }

    /**
     * 修改好友
     *
     * @param req 更新的好友数据
     */
    @Override
    public ResponseVO<String> updateFriend(FriendShipReq req) {
        // 判断好友双方的合法性
        String checkResult = checkUserData(req.getFromId(), req.getToItem().getToId(), req.getAppId());
        if (CharSequenceUtil.isBlank(checkResult)) {
            return ResponseVO.errorResponse(checkResult);
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
    public ResponseVO<ImFriendShipEntity> getRelation(RelationReq req) {
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
    public ResponseVO<List<ImFriendShipEntity>> getAllFriend(RelationReq req) {
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
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            String toId = entry.getKey();
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

    /**
     * 加入黑名单
     *
     * @param req
     */
    @Override
    public ResponseVO<String> addFriendSipBlack(RelationReq req) {
        // 判断好友双方的合法性
        String checkResult = checkUserData(req.getFromId(), req.getToId(), req.getAppId());
        if (CharSequenceUtil.isBlank(checkResult)) {
            return ResponseVO.errorResponse(checkResult);
        }

        return doAddFriendSipBlack(req.getFromId(), req.getToId(), req.getAppId(), req);
    }

    /**
     * 拉出黑名单
     *
     * @param req
     */
    @Override
    public ResponseVO<String> deleteFriendSipBlack(RelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(lqw);

        if (ObjectUtil.equal(entity.getBlack(), FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode())) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setBlack(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        imFriendShipMapper.update(update, lqw);
        return ResponseVO.errorResponse();
    }

    /**
     * 校验黑名单
     *
     * @param req
     */
    @Override
    public ResponseVO<List<CheckFriendShipResp>> checkFriendBlack(CheckFriendShipReq req) {
        Map<String, Integer> toIdMap = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;

        if (ObjectUtil.equal(req.getCheckType(), CheckFriendShipTypeEnum.SINGLE.getType())) {
            resp = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }

        Map<String, Integer> collect = resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));

        for (Map.Entry<String, Integer> entry : toIdMap.entrySet()) {
            String toId = entry.getKey();
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setStatus(toIdMap.get(toId));
                resp.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 同步好友列表信息
     *
     * @param req
     */
    @Override
    public ResponseVO<SyncResp<ImFriendShipEntity>> syncFriendShipList(SyncReq req) {
        // 单次最大拉取数量
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }

        // 返回体
        SyncResp<ImFriendShipEntity> resp = new SyncResp<>();
        // seq > req.getseq limit maxlimit
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getFromId, req.getOperator());
        lqw.gt(ImFriendShipEntity::getFriendSequence, req.getLastSequence());
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.last("limit " + req.getMaxLimit());
        lqw.orderByAsc(ImFriendShipEntity::getFriendSequence);
        List<ImFriendShipEntity> dataList = imFriendShipMapper.selectList(lqw);

        if (!CollectionUtils.isEmpty(dataList)) {
            ImFriendShipEntity maxSeqEntity = dataList.get(dataList.size() - 1);
            resp.setDataList(dataList);
            // 设置最大seq
            Long friendShipMaxSeq = imFriendShipMapper.getFriendShipMaxSeq(req.getAppId(), req.getOperator());
            resp.setMaxSequence(friendShipMaxSeq);
            // 设置是否拉取完毕
            resp.setCompleted(maxSeqEntity.getFriendSequence() >= friendShipMaxSeq);
            return ResponseVO.successResponse(resp);
        }

        resp.setCompleted(true);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取指定用户的所有的好友id
     *
     * @param userId 用户id
     * @param appId  appid
     */
    @Override
    public List<String> getAllFriendId(String userId, Integer appId) {
        return imFriendShipMapper.getAllFriendId(userId, appId);
    }

    /**
     * 判断好友双方的合法性
     */
    private String checkUserData(String fromId, String toId, Integer appId) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(fromId, appId);
        if (!fromInfo.isOk()) {
            return "非法用户";
        }
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(toId, appId);
        if (!toInfo.isOk()) {
            return "好友账号不存在";
        }
        return CharSequenceUtil.EMPTY;
    }

    private ResponseVO doUpateFriend(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource()).set(ImFriendShipEntity::getExtra, dto.getExtra()).set(ImFriendShipEntity::getRemark, dto.getRemark()).eq(ImFriendShipEntity::getAppId, appId).eq(ImFriendShipEntity::getToId, dto.getToId()).eq(ImFriendShipEntity::getFromId, fromId);

        imFriendShipMapper.update(null, updateWrapper);
        return ResponseVO.errorResponse();
    }

    // 添加好友的逻辑
    @Transactional
    public ResponseVO<String> doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
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

    /**
     * 添加黑名单逻辑
     */
    @Transactional
    public ResponseVO doAddFriendSipBlack(String fromId, String toId, Integer appId, RelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, appId);
        lqw.eq(ImFriendShipEntity::getFromId, fromId);
        lqw.eq(ImFriendShipEntity::getToId, toId);

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lqw);

        // 没有添加好友呢，就直接拉黑名单了
        if (ObjectUtil.isNull(fromItem)) {
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            fromItem.setToId(toId);
            fromItem.setAppId(appId);
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 如果存在那么就判断，是否已经被拉入黑名单了
            if (ObjectUtil.equal(fromItem.getBlack(), FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, lqw);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
            }
        }
        //TODO TCP通知

        //TODO 回调

        return ResponseVO.successResponse();
    }
}
