/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.enums.ResponseVO;
import com.sj.im.common.enums.UserErrorCode;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.dao.mapper.ImUserDataMapper;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.model.req.ModifyUserInfoReq;
import com.sj.im.service.user.model.req.UserBatchReq;
import com.sj.im.service.user.model.req.UserSingleReq;
import com.sj.im.service.user.model.resp.GetUserInfoResp;
import com.sj.im.service.user.model.resp.ImportUserResp;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户相关接口实现类
 */
@Service
@Slf4j
public class ImUserServiceImpl implements ImUserService {
    @Resource
    ImUserDataMapper imUserDataMapper;

    // 导入用户
    @Override
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req) {
        // 导入用户数量的限制
        if (req.getUserData().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        // 这个要返回给客户，导入成功的和导入失败的
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        // 遍历然后填充成功和失败列表
        for (ImUserDataEntity user : req.getUserData()) {
            try {
                user.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(user);
                if (insert == 1) {
                    successId.add(user.getUserId());
                } else {
                    errorId.add(user.getUserId());
                }
            } catch (Exception e) {
                log.error("导入用户失败", e);
                errorId.add(user.getUserId());
            }
        }

        // 定义返回的类
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return ResponseVO.successResponse(resp);
    }

    /**
     * 批量获取用户信息
     *
     * @param req 用户的id
     * @return GetUserInfoResp 用户信息
     */
    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(UserBatchReq req) {
        // 查询用户的条件
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getAppId, req.getAppId());
        qw.in(ImUserDataEntity::getUserId, req.getUserIds());
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        // 查询出符合条件的用户
        List<ImUserDataEntity> imUserDataEntities = imUserDataMapper.selectList(qw);

        // 过滤出查询失败的用户一并返回
        HashMap<String, ImUserDataEntity> map = new HashMap<>();
        // 先把查询成功的用户找出来
        for (ImUserDataEntity data : imUserDataEntities) {
            map.put(data.getUserId(), data);
        }
        // 通过Map的containsKey来判断没有在查询成功中的用户，将他们添加到查询失败的用户中去
        List<String> failUser = new ArrayList<>();
        for (String userId : req.getUserIds()) {
            if (!map.containsKey(userId)) {
                failUser.add(userId);
            }
        }
        // 包装Resp
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(imUserDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取单个用户信息
     *
     * @param req 用户参数
     * @return ImUserDataEntity 用户信息
     */
    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(UserSingleReq req) {
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getAppId, req.getAppId());
        qw.eq(ImUserDataEntity::getUserId, req.getUserId());
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity data = imUserDataMapper.selectOne(qw);

        if (ObjectUtil.isNull(data)) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(data);
    }

    /**
     * 批量删除用户信息
     *
     * @param req 要删除的用户id
     * @return 操作结果
     */
    @Override
    public ResponseVO<ImportUserResp> deleteUser(UserBatchReq req) {
        // 这里的删除都是逻辑删除，也就是把那个DelFlag变成1就成了删除
        // ，不用delete语句，使用的是update语句，所以下面这个就是为了update使用的
        ImUserDataEntity data = new ImUserDataEntity();
        data.setDelFlag(DelFlagEnum.DELETE.getCode());

        // 删除成功和失败的情况，返回给客户端
        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId : req.getUserIds()) {
            // 构建一个update的条件
            LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(ImUserDataEntity::getAppId, req.getAppId());
            qw.in(ImUserDataEntity::getUserId, req.getUserIds());
            qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
            int update = 0;
            // try catch用的很好，思维缜密
            try {
                update = imUserDataMapper.update(data, qw);
                // 分堆
                if (update > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);
            }
        }
        // 包装一下Resp
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return ResponseVO.successResponse(resp);
    }

    /**
     * 修改用户信息
     *
     * @param req 修改的用户信息
     * @return 操作结果
     */
    @Override
    public ResponseVO<String> modifyUserInfo(ModifyUserInfoReq req) {
        // 先查询要修改的用户是否合法
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getAppId, req.getAppId());
        qw.eq(ImUserDataEntity::getUserId, req.getUserId());
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity data = imUserDataMapper.selectOne(qw);

        if (ObjectUtil.isNull(data)) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req, update);

        // 这里不需要修改AppId和userId，如果要修改这两个的话，还不如新建一个用户
        update.setAppId(null);
        update.setUserId(null);
        int update1 = imUserDataMapper.update(update, qw);

        if (update1 == 1) {
            // TODO 通知


            // TODO 回调

            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }
}
