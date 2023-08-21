/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.model.req.ModifyUserInfoReq;
import com.sj.im.service.user.model.req.UserBatchReq;
import com.sj.im.service.user.model.req.UserSingleReq;
import com.sj.im.service.user.model.resp.GetUserInfoResp;
import com.sj.im.service.user.model.resp.ImportUserResp;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户相关接口
 */
public interface ImUserService {
    /**
     * 导入用户的请求
     *
     * @param req 导入到用户数据
     * @return ImportUserResp 返回给用户的结果
     */
    ResponseVO<ImportUserResp> importUser(ImportUserReq req);

    /**
     * 批量获取用户信息
     *
     * @param req 用户的id
     * @return GetUserInfoResp 用户信息
     */
    ResponseVO<GetUserInfoResp> getUserInfo(UserBatchReq req);

    /**
     * 获取单个用户信息
     *
     * @param req 用户参数
     * @return ImUserDataEntity 用户信息
     */
    ResponseVO<ImUserDataEntity> getSingleUserInfo(UserSingleReq req);

    /**
     * 批量删除用户信息
     *
     * @param req 要删除的用户id
     * @return 操作结果
     */
    ResponseVO<ImportUserResp> deleteUser(UserBatchReq req);

    /**
     * 修改用户信息
     *
     * @param req 修改的用户信息
     * @return 操作结果
     */
    ResponseVO modifyUserInfo(ModifyUserInfoReq req);
}
