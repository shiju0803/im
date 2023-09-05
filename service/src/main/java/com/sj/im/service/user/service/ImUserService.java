/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sj.im.common.route.RouteInfo;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.web.req.*;
import com.sj.im.service.user.web.resp.GetUserInfoResp;
import com.sj.im.service.user.web.resp.ImportUserResp;

import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户相关接口
 */
public interface ImUserService extends IService<ImUserDataEntity> {

    /**
     * 导入IM用户的方法
     *
     * @param req 导入IM用户参数
     * @return 导入结果
     */
    ImportUserResp importUser(ImportUserReq req);

    /**
     * 删除IM用户的方法
     *
     * @param req 删除IM用户参数
     * @return 删除结果
     */
    ImportUserResp deleteUser(DeleteUserReq req);

    /**
     * 获取IM用户信息的方法
     *
     * @param req 获取IM用户信息参数
     * @return IM用户信息
     */
    GetUserInfoResp getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个IM用户信息的方法
     *
     * @param userId 用户ID
     * @param appId  应用ID
     * @return 单个IM用户信息
     */
    ImUserDataEntity getSingleUserInfo(String userId, Integer appId);

    /**
     * 修改IM用户信息的方法
     *
     * @param req 修改IM用户信息参数
     */
    void modifyUserInfo(ModifyUserInfoReq req);

    /**
     * IM用户登录方法
     *
     * @param req 登录参数
     * @return 路由信息
     */
    RouteInfo login(LoginReq req);

    /**
     * 获取用户序列
     *
     * @param req 用户信息
     * @return 用户最大的序列号
     */
    Map<Object, Object> getUserSequence(GetUserSequenceReq req);
}
