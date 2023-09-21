/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sj.app.entry.AppUserEntity;
import com.sj.app.web.req.RegisterReq;

/**
 * 用户相关接口
 *
 * @author ShiJu
 * @version 1.0
 */
public interface AppUserService extends IService<AppUserEntity> {
    /**
     * 根据用户名和密码查询用户信息
     *
     * @param userName 用户名
     * @param password 密码
     */
    AppUserEntity getUserByUserNameAndPassword(String userName, String password);

    /**
     * 根据手机号查询用户信息
     *
     * @param mobile 手机号
     */
    AppUserEntity getUserByMobile(String mobile);

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名称
     */
    AppUserEntity getUserByUserName(String userName);

    /**
     * 用户注册具体逻辑
     *
     * @param req 注册信息
     */
    AppUserEntity registerUser(RegisterReq req);
}
