/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sj.im.codec.pack.friendship.AddFriendBlackPack;
import com.sj.im.codec.pack.friendship.AddFriendPack;
import com.sj.im.codec.pack.friendship.DeleteFriendPack;
import com.sj.im.codec.pack.friendship.UpdateFriendPack;
import com.sj.im.common.ResponseVO;
import com.sj.im.common.config.AppConfig;
import com.sj.im.common.constant.CallbackCommandConstants;
import com.sj.im.common.enums.CheckFriendShipTypeEnum;
import com.sj.im.common.enums.FriendShipErrorCode;
import com.sj.im.common.enums.FriendShipStatusEnum;
import com.sj.im.common.enums.command.FriendshipEventCommand;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.model.RequestBase;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.sj.im.service.friendship.model.callback.AddFriendAfterCallbackDto;
import com.sj.im.service.friendship.model.callback.AddFriendBlackAfterCallbackDto;
import com.sj.im.service.friendship.model.callback.DeleteFriendAfterCallbackDto;
import com.sj.im.service.friendship.model.req.*;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.helper.CallbackHelper;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private ImFriendShipServiceImpl thisService;
    @Resource
    private ImFriendShipMapper imFriendShipMapper;
    @Resource
    private ImUserService imUserService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackHelper callBackHelper;
    @Resource
    private MessageHelper messageHelper;

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
            BeanUtil.copyProperties(dto, en);
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
    public ResponseVO<String> addFriend(FriendShipReq req) {
        // 判断好友双方的合法性
        String checkResult = checkUserData(req.getFromId(), req.getToItem().getToId(), req.getAppId());
        if (CharSequenceUtil.isBlank(checkResult)) {
            return ResponseVO.errorResponse(checkResult);
        }

        // 之前回调
        if (appConfig.isAddFriendBeforeCallback()) {
            ResponseVO responseVO = callBackHelper.beforeCallback(req.getAppId(),
                    CallbackCommandConstants.ADD_FRIEND_BEFORE, JSONUtil.toJsonStr(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }
        }

        return thisService.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
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
        ResponseVO<String> responseVO = doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
        if (responseVO.isOk()) {
            // TCP通知其他端
            UpdateFriendPack pack = new UpdateFriendPack();
            pack.setRemark(req.getToItem().getRemark());
            pack.setToId(req.getToItem().getToId());
            messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                    FriendshipEventCommand.FRIEND_UPDATE, pack, req.getAppId());

            // 之后回调
            if ((appConfig.isModifyFriendAfterCallback())) {
                AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToItem(req.getToItem());
                callBackHelper.callback(req.getAppId(), CallbackCommandConstants.UPDATE_FRIEND_AFTER, JSONUtil.toJsonStr(callbackDto));
            }
        }


        return responseVO;
    }

    /**
     * 删除好友
     *
     * @param req 删除的好友数据
     */
    @Override
    public ResponseVO<String> deleteFriend(RelationReq req) {
        LambdaQueryWrapper<ImFriendShipEntity> delwrapper = new LambdaQueryWrapper<>();
        delwrapper.eq(ImFriendShipEntity::getAppId, req.getAppId());
        delwrapper.eq(ImFriendShipEntity::getFromId, req.getFromId());
        delwrapper.eq(ImFriendShipEntity::getToId, req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(delwrapper);

        if(ObjectUtil.isNull(entity)){
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }else{
            if(ObjectUtil.isNotNull(entity.getStatus())
                    && ObjectUtil.equal(entity.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())){
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                // 逻辑删除还是
                imFriendShipMapper.update(update, delwrapper);

                // TCP通知其他端
                DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
                deleteFriendPack.setFromId(req.getFromId());
                deleteFriendPack.setToId(req.getToId());
                messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                        FriendshipEventCommand.FRIEND_DELETE, deleteFriendPack, req.getAppId());

                // 之后回调
                if(appConfig.isDeleteFriendAfterCallback()){
                    DeleteFriendAfterCallbackDto deleteFriendAfterCallbackDto = new DeleteFriendAfterCallbackDto();
                    deleteFriendAfterCallbackDto.setFromId(req.getFromId());
                    deleteFriendAfterCallbackDto.setToId(req.getToId());
                    callBackHelper.callback(req.getAppId(), CallbackCommandConstants.DELETE_FRIEND_AFTER,
                            JSONUtil.toJsonStr(deleteFriendAfterCallbackDto));
                }
            }else{
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
        }

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

        // TCP通知其他端
        DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
        deleteFriendPack.setFromId(req.getFromId());
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_ALL_DELETE, deleteFriendPack, req.getAppId());

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
     */
    @Override
    public ResponseVO<String> addBlack(RelationReq req) {
        // 判断好友双方的合法性
        String checkResult = checkUserData(req.getFromId(), req.getToId(), req.getAppId());
        if (CharSequenceUtil.isBlank(checkResult)) {
            return ResponseVO.errorResponse(checkResult);
        }

        return thisService.doAddBlack(req.getFromId(), req.getToId(), req.getAppId(), req);
    }

    /**
     * 拉出黑名单
     */
    @Override
    public ResponseVO<String> deleteBlack(RelationReq req) {
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

        int delete = imFriendShipMapper.update(update, lqw);
        if (delete == 1) {
            // TCP通知其他端
            DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
            deleteFriendPack.setFromId(req.getFromId());
            deleteFriendPack.setToId(req.getToId());
            messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                    FriendshipEventCommand.FRIEND_BLACK_DELETE, deleteFriendPack, req.getAppId());

            // 之后回调
            if ((appConfig.isDeleteFriendShipBlackAfterCallback())) {
                AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToId(req.getToId());
                callBackHelper.callback(req.getAppId(),
                        CallbackCommandConstants.DELETE_BLACK, JSONUtil.toJsonStr(callbackDto));

            }
        }
        return ResponseVO.errorResponse();
    }

    /**
     * 校验黑名单
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

    private ResponseVO<String> doUpdateFriend(String fromId, FriendDto dto, Integer appId) {
        LambdaUpdateWrapper<ImFriendShipEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendShipEntity::getExtra, dto.getExtra())
                .set(ImFriendShipEntity::getRemark, dto.getRemark())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getToId, dto.getToId())
                .eq(ImFriendShipEntity::getFromId, fromId);

        int update = imFriendShipMapper.update(null, updateWrapper);
        if (update == 1) {
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    /**
     * 添加好友的逻辑
     */
    @Transactional
    public ResponseVO<String> doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        // A-B
        // Friend表插入 A 和 B 两条记录
        // 查询是否有记录存在，如果存在则判断状态，如果是已经添加，则提示已经添加了，如果是未添加，则修改状态
        LambdaQueryWrapper<ImFriendShipEntity> fromQuery = new LambdaQueryWrapper<>();
        fromQuery.eq(ImFriendShipEntity::getAppId, appId);
        fromQuery.eq(ImFriendShipEntity::getFromId, fromId);
        fromQuery.eq(ImFriendShipEntity::getToId, dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(fromQuery);

        // 不存在这条消息
        if (ObjectUtil.isNull(fromItem)) {
            // 直接添加
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(fromId);
            BeanUtil.copyProperties(dto, fromItem);
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

                int res = imFriendShipMapper.update(update, fromQuery);
                if (res != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        // 第二条数据的插入
        LambdaQueryWrapper<ImFriendShipEntity> toQuery = new LambdaQueryWrapper<>();
        toQuery.eq(ImFriendShipEntity::getAppId, appId);
        toQuery.eq(ImFriendShipEntity::getFromId, dto.getToId());
        toQuery.eq(ImFriendShipEntity::getToId, fromId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(toQuery);

        // 不存在就直接添加
        if (ObjectUtil.isNull(toItem)) {
            toItem = new ImFriendShipEntity();
            toItem.setAppId(appId);
            toItem.setFromId(dto.getToId());
            toItem.setToId(fromId);
            BeanUtil.copyProperties(dto, toItem);
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
                imFriendShipMapper.update(entity, toQuery);
            }
        }

        // TCP通知A的其他端
        AddFriendPack fromPack = new AddFriendPack();
        BeanUtil.copyProperties(fromItem, fromPack);
        if (ObjectUtil.isNotNull(requestBase)) {
            messageHelper.sendToUser(fromId, requestBase.getClientType(), requestBase.getImei(), 
                    FriendshipEventCommand.FRIEND_ADD, fromPack, requestBase.getAppId());
        } else {
            messageHelper.sendToUser(fromId, FriendshipEventCommand.FRIEND_ADD, fromPack, requestBase.getAppId());
        }
        // TCP通知B的所有端
        AddFriendPack toPack = new AddFriendPack();
        BeanUtil.copyProperties(toItem, toPack);
        messageHelper.sendToUser(toItem.getFromId(), FriendshipEventCommand.FRIEND_ADD, toPack, requestBase.getAppId());

        // 之后回调
        if (appConfig.isAddFriendAfterCallback()) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToItem(dto);
            callBackHelper.callback(appId,
                    CallbackCommandConstants.ADD_FRIEND_AFTER, JSONUtil.toJsonStr(callbackDto));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 添加黑名单逻辑
     */
    @Transactional
    public ResponseVO<String> doAddBlack(String fromId, String toId, Integer appId, RelationReq req) {
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
        // TCP通知其他端
        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setFromId(req.getFromId());
        addFriendBlackPack.setToId(req.getToId());
        messageHelper.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIEND_BLACK_ADD, addFriendBlackPack, req.getAppId());

        // 之后回调
        if (appConfig.isAddFriendShipBlackAfterCallback()) {
            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToId(toId);
            callBackHelper.callback(appId,
                    CallbackCommandConstants.ADD_BLACK_AFTER, JSONUtil.toJsonStr(callbackDto));
        }
        return ResponseVO.successResponse();
    }
}
