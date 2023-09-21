/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.sj.im.codec.pack.friendship.AddFriendBlackPack;
import com.sj.im.codec.pack.friendship.AddFriendPack;
import com.sj.im.codec.pack.friendship.DeleteFriendPack;
import com.sj.im.codec.pack.friendship.UpdateFriendPack;
import com.sj.im.common.constant.CallbackCommandConstants;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.AllowFriendTypeEnum;
import com.sj.im.common.enums.CheckFriendShipTypeEnum;
import com.sj.im.common.enums.FriendShipStatusEnum;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.enums.exception.FriendShipErrorCode;
import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.RequestBase;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.friendship.entry.ImFriendShipEntity;
import com.sj.im.service.friendship.mapper.ImFriendShipMapper;
import com.sj.im.service.friendship.service.ImFriendShipRequestService;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.friendship.web.callback.AddFriendAfterCallbackDto;
import com.sj.im.service.friendship.web.callback.AddFriendBlackAfterCallbackDto;
import com.sj.im.service.friendship.web.callback.DeleteFriendAfterCallbackDto;
import com.sj.im.service.friendship.web.req.*;
import com.sj.im.service.friendship.web.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.web.resp.ImportFriendShipResp;
import com.sj.im.service.helper.CallbackHelper;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.util.RedisSeq;
import com.sj.im.service.util.WriteUserSeq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * 好友关系业务类
 *
 * @author ShiJu
 * @version 1.0
 */
@Service
@Slf4j
public class ImFriendShipServiceImpl extends MppServiceImpl<ImFriendShipMapper, ImFriendShipEntity>
        implements ImFriendShipService {
    @Resource
    private ImFriendShipService thisService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private ImFriendShipRequestService imFriendShipRequestService;
    @Resource
    private ImFriendShipMapper imFriendShipMapper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackHelper callBackHelper;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private WriteUserSeq writeUserSeq;
    @Resource
    private RedisSeq redisSeq;

    /**
     * 导入关系链
     *
     * @param req 添加的好友数据
     */
    @Override
    public ImportFriendShipResp importFriendShip(ImportFriendShipReq req) {
        // 判断导入的好友信息数量是否超过 100，如果超过则抛出业务异常
        if (req.getFriendItem().size() > 100) {
            throw new BusinessException(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }

        // 创建 successId 和 errorId 集合，用于存储导入成功和失败的好友信息 ID
        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();
        // 遍历导入的好友信息列表
        for (ImportFriendShipReq.ImportFriendDto dto : req.getFriendItem()) {
            // 创建 FriendShip 对象，并将导入的好友信息复制到该对象中
            ImFriendShipEntity entity = BeanUtil.toBean(dto, ImFriendShipEntity.class);
            // 设置好友信息的 AppId 和 FromId
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                // 将好友信息插入数据库中
                boolean insert = save(entity);
                // 如果插入成功，则将好友信息 ID 存入 successId 集合中
                if (insert) {
                    successId.add(dto.getToId());
                } else {
                    // 如果插入失败，则将好友信息 ID 存入 errorId 集合中
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                log.error("导入失败: 好友id:{}", dto.getToId(), e);
                errorId.add(dto.getToId());
            }
        }

        // 将成功和失败的好友信息 ID 分别设置到 ImportFriendShipResp 对象中
        ImportFriendShipResp resp = new ImportFriendShipResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        // 返回 ImportFriendShipResp 对象
        return resp;
    }

    /**
     * 添加好友
     *
     * @param req 添加的好友数据
     */
    @Override
    public void addFriend(AddFriendReq req) {
        // 获取发起添加好友请求的用户信息
        ImUserDataEntity fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (ObjectUtil.isNull(fromInfo)) {
            // 如果发起请求的用户不存在，则抛出业务异常
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 获取被添加好友的用户信息
        ImUserDataEntity toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (ObjectUtil.isNull(toInfo)) {
            // 如果被添加的用户不存在，则抛出业务异常
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 如果配置了添加好友前回调，则调用回调服务
        if (appConfig.isAddFriendBeforeCallback()) {
            ResponseVO callbackResp =
                    callBackHelper.beforeCallback(req.getAppId(), CallbackCommandConstants.ADD_FRIEND_BEFORE,
                                                  JSONUtil.toJsonStr(req));
            if (!callbackResp.isOk()) {
                // 如果回调服务返回失败，则抛出业务异常
                throw new BusinessException(callbackResp.getMsg());
            }
        }

        // 判断被添加好友的用户是否允许被添加
        if (ObjectUtil.equal(toInfo.getFriendAllowType(), AllowFriendTypeEnum.NOT_NEED.getCode())) {
            // 如果允许被添加，则执行添加好友操作
            thisService.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {
            // 如果不允许被添加，则插入一条好友申请的数据
            LambdaQueryWrapper<ImFriendShipEntity> query = new LambdaQueryWrapper<>();
            query.eq(ImFriendShipEntity::getAppId, req.getAppId());
            query.eq(ImFriendShipEntity::getFromId, req.getFromId());
            query.eq(ImFriendShipEntity::getToId, req.getToItem().getToId());
            ImFriendShipEntity fromItem = getOne(query);
            if (ObjectUtil.notEqual(fromItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                // 如果之前没有好友关系，则插入一条好友申请的数据
                imFriendShipRequestService.addFriendshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
            } else {
                // 如果之前已经存在好友关系，则抛出业务异常
                throw new BusinessException(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
        }
    }

    /**
     * 修改好友
     *
     * @param req 更新的好友数据
     */
    @Override
    public void updateFriend(UpdateFriendReq req) {
        // 判断好友双方的合法性
        //获取发送方用户信息
        ImUserDataEntity fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (ObjectUtil.isNull(fromInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        //获取接收方用户信息
        ImUserDataEntity toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (ObjectUtil.isNull(toInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 执行好友信息的更新操作
        thisService.doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());

        // 构造好友信息更新的消息实体
        UpdateFriendPack pack = new UpdateFriendPack();
        pack.setRemark(req.getToItem().getRemark());
        pack.setToId(req.getToItem().getToId());
        // 将好友信息更新的消息实体发送给发送方用户
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                                 FriendshipEventCommand.FRIEND_UPDATE, pack, req.getAppId());

        // 如果配置了在回调之后再修改好友信息，构造回调相关的参数并执行回调操作
        if ((appConfig.isModifyFriendAfterCallback())) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToItem(req.getToItem());
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.UPDATE_FRIEND_AFTER,
                                    JSONUtil.toJsonStr(callbackDto));
        }
    }

    /**
     * 删除好友
     *
     * @param req 删除的好友数据
     */
    @Override
    @Transactional
    public void deleteFriend(DeleteFriendReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(ImFriendShipEntity::getAppId, req.getAppId());
        delWrapper.eq(ImFriendShipEntity::getFromId, req.getFromId());
        delWrapper.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(delWrapper);

        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }

        if (ObjectUtil.equal(entity.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
            ImFriendShipEntity update = new ImFriendShipEntity();
            long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_SEQ);
            update.setFriendSequence(seq);
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            // 逻辑删除
            imFriendShipMapper.update(update, delWrapper);
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_SEQ, seq);

            // TCP通知其他端
            DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
            deleteFriendPack.setSequence(seq);
            deleteFriendPack.setFromId(req.getFromId());
            deleteFriendPack.setToId(req.getToId());
            messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                                     FriendshipEventCommand.FRIEND_DELETE, deleteFriendPack, req.getAppId());

            // 之后回调
            if (appConfig.isDeleteFriendAfterCallback()) {
                DeleteFriendAfterCallbackDto deleteFriendAfterCallbackDto = new DeleteFriendAfterCallbackDto();
                deleteFriendAfterCallbackDto.setFromId(req.getFromId());
                deleteFriendAfterCallbackDto.setToId(req.getToId());
                callBackHelper.callback(req.getAppId(), CallbackCommandConstants.DELETE_FRIEND_AFTER,
                                        JSONUtil.toJsonStr(deleteFriendAfterCallbackDto));
            }
        } else {
            throw new BusinessException(FriendShipErrorCode.FRIEND_IS_DELETED);
        }
    }

    /**
     * 删除所有好友
     *
     * @param req 请求参数
     */
    @Override
    public void deleteAllFriend(DeleteFriendReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getStatus, FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, lqw);

        // TCP通知其他端
        DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
        deleteFriendPack.setFromId(req.getFromId());
        // 发送删除所有好友的通知消息
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                                 FriendshipEventCommand.FRIEND_ALL_DELETE, deleteFriendPack, req.getAppId());
    }

    /**
     * 拉取指定好友信息
     *
     * @param req 请求参数
     */
    @Override
    public ImFriendShipEntity getRelation(GetRelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = getOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException(FriendShipErrorCode.REPEAT_SHIP_IS_NOT_EXIST);
        }
        return entity;
    }

    /**
     * 拉取所有好友信息
     *
     * @param req 请求参数
     */
    @Override
    public List<ImFriendShipEntity> getAllFriend(GetAllFriendShipReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        return list(lqw);
    }

    /**
     * 校验好友关系
     *
     * @param req 请求参数
     */
    @Override
    public List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req) {
        // 双向校验的修改
        // 1、先是把req中的所有的toIds都转化为key为属性，value为0的map
        Map<String, Integer> result = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;
        // 根据检查类型分别查询好友关系
        if (ObjectUtil.equal(req.getCheckType(), CheckFriendShipTypeEnum.SINGLE.getType())) {
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }

        // 2、将复杂sql查询出来的数据转换为map
        Map<String, Integer> collect =
                resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
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
        return resp;
    }

    /**
     * 加入黑名单
     */
    @Override
    public void addBlack(AddFriendShipBlackReq req) {
        Integer appId = req.getAppId();
        String fromId = req.getFromId();
        String toId = req.getToId();
        //获取发送方用户信息
        ImUserDataEntity fromInfo = imUserService.getSingleUserInfo(fromId, appId);
        if (ObjectUtil.isNull(fromInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        //获取接收方用户信息
        ImUserDataEntity toInfo = imUserService.getSingleUserInfo(toId, appId);
        if (ObjectUtil.isNull(toInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getAppId, appId);
        lqw.eq(ImFriendShipEntity::getFromId, fromId);
        lqw.eq(ImFriendShipEntity::getToId, toId);
        //查询好友关系
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(lqw);

        long seq;
        //如果好友关系不存在，则添加好友关系
        if (ObjectUtil.isNull(fromItem)) {
            seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_SEQ);
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            fromItem.setToId(toId);
            fromItem.setFriendSequence(seq);
            fromItem.setAppId(appId);
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_SEQ, seq);
        } else {
            // 如果存在那么就判断，是否已经被拉入黑名单了
            if (ObjectUtil.equal(fromItem.getBlack(), FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode())) {
                throw new BusinessException(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_SEQ);
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setFriendSequence(seq);
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, lqw);
                if (result != 1) {
                    throw new BusinessException(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
                writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_SEQ, seq);
            }
        }
        // TCP通知其他端
        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setFromId(fromId);
        addFriendBlackPack.setSequence(seq);
        addFriendBlackPack.setToId(toId);
        //发送添加黑名单关系的通知消息
        messageHelper.sendToUser(fromId, req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_BLACK_ADD,
                                 addFriendBlackPack, appId);

        // 之后回调
        if (appConfig.isAddFriendShipBlackAfterCallback()) {
            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToId(toId);
            callBackHelper.callback(appId, CallbackCommandConstants.ADD_BLACK_AFTER, JSONUtil.toJsonStr(callbackDto));
        }
    }

    /**
     * 拉出黑名单
     */
    @Override
    public void deleteBlack(DeleteBlackReq req) {
        // 查询该好友是否在黑名单中
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getFromId, req.getFromId());
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(lqw);

        if (ObjectUtil.equal(entity.getBlack(), FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode())) {
            throw new BusinessException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        // 生成好友关系序列号
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIENDSHIP_SEQ);
        // 更新好友关系表
        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setFriendSequence(seq);
        update.setBlack(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        int delete = imFriendShipMapper.update(update, lqw);
        if (delete == 1) {
            // 写入用户序列号
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.FRIENDSHIP_SEQ, seq);

            // TCP通知其他端 发送删除好友关系的消息
            DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
            deleteFriendPack.setFromId(req.getFromId());
            deleteFriendPack.setSequence(seq);
            deleteFriendPack.setToId(req.getToId());
            messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                                     FriendshipEventCommand.FRIEND_BLACK_DELETE, deleteFriendPack, req.getAppId());

            // 如果设置了回调，执行回调操作
            if ((appConfig.isDeleteFriendShipBlackAfterCallback())) {
                AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToId(req.getToId());
                callBackHelper.callback(req.getAppId(), CallbackCommandConstants.DELETE_BLACK,
                                        JSONUtil.toJsonStr(callbackDto));

            }
        }
    }

    /**
     * 校验黑名单
     */
    @Override
    public List<CheckFriendShipResp> checkFriendBlack(CheckFriendShipReq req) {
        // 将被检查的好友 ID 列表转换为 Map，初始值为 0
        Map<String, Integer> toIdMap = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));
        // 定义结果列表
        List<CheckFriendShipResp> resp;
        // 根据检查类型调用不同的 Mapper 方法查询好友关系黑名单
        if (ObjectUtil.equal(req.getCheckType(), CheckFriendShipTypeEnum.SINGLE.getType())) {
            resp = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }

        // 将查询结果转换为 Map，以被检查好友 ID 为键，好友关系状态为值
        Map<String, Integer> collect =
                resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
        // 遍历被检查好友 ID Map，如果某个好友关系不存在则将其添加到结果列表中
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
        // 返回结果列表
        return resp;
    }

    /**
     * 同步好友列表信息
     */
    @Override
    public SyncResp<ImFriendShipEntity> syncFriendShipList(SyncReq req) {
        // 如果最大限制超过 100，则将最大限制设置为 100。
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }

        // 返回体
        SyncResp<ImFriendShipEntity> resp = new SyncResp<>();
        // seq > req.getSeq limit maxLimit
        LambdaQueryWrapper<ImFriendShipEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImFriendShipEntity::getFromId, req.getOperator());
        lqw.gt(ImFriendShipEntity::getFriendSequence, req.getLastSequence());
        lqw.eq(ImFriendShipEntity::getAppId, req.getAppId());
        lqw.last("limit " + req.getMaxLimit());
        lqw.orderByAsc(ImFriendShipEntity::getFriendSequence);
        List<ImFriendShipEntity> dataList = imFriendShipMapper.selectList(lqw);

        if (CollUtil.isNotEmpty(dataList)) {
            // 如果查询结果非空，则设置 SyncResp 对象的属性
            ImFriendShipEntity maxSeqEntity = dataList.get(dataList.size() - 1);
            resp.setDataList(dataList);
            // 设置最大序列号
            Long friendShipMaxSeq = imFriendShipMapper.getFriendShipMaxSeq(req.getAppId(), req.getOperator());
            resp.setMaxSequence(friendShipMaxSeq);
            // 设置是否拉取完毕
            resp.setCompleted(maxSeqEntity.getFriendSequence() >= friendShipMaxSeq);
            return resp;
        }

        // 如果查询结果为空，则设置 SyncResp 对象的 completed 属性为 true
        resp.setCompleted(true);
        return resp;
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

    @Transactional
    public void doUpdateFriend(String fromId, FriendDto dto, Integer appId) {
        // 获取一个序列号
        long seq = redisSeq.doGetSeq(appId + SeqConstants.FRIENDSHIP_SEQ);
        // 创建一个 UpdateWrapper 对象，用于更新 FriendShip 表中的记录
        LambdaUpdateWrapper<ImFriendShipEntity> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段和对应的值
        updateWrapper.set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                     .set(ImFriendShipEntity::getExtra, dto.getExtra()).set(ImFriendShipEntity::getFriendSequence, seq)
                     .set(ImFriendShipEntity::getRemark, dto.getRemark()).eq(ImFriendShipEntity::getAppId, appId)
                     .eq(ImFriendShipEntity::getToId, dto.getToId()).eq(ImFriendShipEntity::getFromId, fromId);

        int update = imFriendShipMapper.update(null, updateWrapper);
        if (update == 1) {
            // 如果更新成功，则写入用户序列号
            writeUserSeq.writeUserSeq(appId, fromId, SeqConstants.FRIENDSHIP_SEQ, seq);
        }
    }

    /**
     * 添加好友的逻辑
     */
    @Transactional
    public void doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        // A-B
        // Friend表插入 A 和 B 两条记录
        // 查询是否有记录存在，如果存在则判断状态，如果是已经添加，则提示已经添加了，如果是未添加，则修改状态
        LambdaQueryWrapper<ImFriendShipEntity> fromQuery = new LambdaQueryWrapper<>();
        fromQuery.eq(ImFriendShipEntity::getAppId, appId);
        fromQuery.eq(ImFriendShipEntity::getFromId, fromId);
        fromQuery.eq(ImFriendShipEntity::getToId, dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(fromQuery);

        long seq = 0L;
        // 不存在这条消息
        if (ObjectUtil.isNull(fromItem)) {
            seq = redisSeq.doGetSeq(appId + SeqConstants.FRIENDSHIP_SEQ);
            // 直接添加
            fromItem = BeanUtil.toBean(dto, ImFriendShipEntity.class);
            fromItem.setAppId(appId);
            fromItem.setFromId(fromId);
            fromItem.setFriendSequence(seq);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 存在这条消息，去根据状态做判断
            // 如果已添加，则提示已添加
            if (ObjectUtil.equal(fromItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                throw new BusinessException(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
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
                seq = redisSeq.doGetSeq(appId + SeqConstants.FRIENDSHIP_SEQ);
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int res = imFriendShipMapper.update(update, fromQuery);
                if (res != 1) {
                    throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        // 添加好友关系的另一方
        LambdaQueryWrapper<ImFriendShipEntity> toQuery = new LambdaQueryWrapper<>();
        toQuery.eq(ImFriendShipEntity::getAppId, appId);
        toQuery.eq(ImFriendShipEntity::getFromId, dto.getToId());
        toQuery.eq(ImFriendShipEntity::getToId, fromId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(toQuery);
        // 不存在就直接添加
        if (ObjectUtil.isNull(toItem)) {
            toItem = BeanUtil.toBean(dto, ImFriendShipEntity.class);
            toItem.setAppId(appId);
            toItem.setFromId(dto.getToId());
            toItem.setToId(fromId);
            toItem.setFriendSequence(seq);
            toItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem.setCreateTime(new Date());
            int insert = imFriendShipMapper.insert(toItem);
            if (insert != 1) {
                throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            // 存在就判断状态
            if (ObjectUtil.equal(toItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                throw new BusinessException(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
            ImFriendShipEntity entity = new ImFriendShipEntity();
            if (StringUtils.isNotEmpty(dto.getAddSource())) {
                entity.setAddSource(dto.getAddSource());
            }
            if (StringUtils.isNotEmpty(dto.getRemark())) {
                entity.setRemark(dto.getRemark());
            }
            if (StringUtils.isNotEmpty(dto.getExtra())) {
                entity.setExtra(dto.getExtra());
            }
            entity.setFriendSequence(seq);
            entity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            int update = imFriendShipMapper.update(entity, toQuery);
            if (update != 1) {
                throw new BusinessException(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }
        writeUserSeq.writeUserSeq(appId, fromId, SeqConstants.FRIENDSHIP_SEQ, seq);
        writeUserSeq.writeUserSeq(appId, dto.getToId(), SeqConstants.FRIENDSHIP_SEQ, seq);

        // TCP通知A的其他端 发送添加好友通知
        AddFriendPack addFriendPack = BeanUtil.toBean(fromItem, AddFriendPack.class);
        addFriendPack.setSequence(seq);
        if (ObjectUtil.isNotNull(requestBase)) {
            messageHelper.sendToUser(fromId, requestBase.getClientType(), requestBase.getImei(),
                                     FriendshipEventCommand.FRIEND_ADD, addFriendPack, requestBase.getAppId());
        } else {
            messageHelper.sendToUser(fromId, FriendshipEventCommand.FRIEND_ADD, addFriendPack, requestBase.getAppId());
        }
        // TCP通知B的所有端
        AddFriendPack toPack = BeanUtil.toBean(toItem, AddFriendPack.class);
        toPack.setSequence(seq);
        messageHelper.sendToUser(toItem.getFromId(), FriendshipEventCommand.FRIEND_ADD, toPack, requestBase.getAppId());

        // 之后回调
        if (appConfig.isAddFriendAfterCallback()) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToItem(dto);
            callBackHelper.callback(appId, CallbackCommandConstants.ADD_FRIEND_AFTER, JSONUtil.toJsonStr(callbackDto));
        }
    }
}
