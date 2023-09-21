/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sj.app.config.AppConfig;
import com.sj.app.entry.AppUserEntity;
import com.sj.app.service.AppUserService;
import com.sj.app.service.LoginService;
import com.sj.app.web.req.LoginReq;
import com.sj.app.web.req.RegisterReq;
import com.sj.app.web.resp.LoginResp;
import com.sj.im.common.enums.LoginTypeEnum;
import com.sj.im.common.enums.RegisterTypeEnum;
import com.sj.im.common.enums.exception.AppUserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.util.SigAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户相关接口实现类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private AppUserService appUserService;
    @Resource
    private AppConfig appConfig;

    /**
     * 用户登录
     *
     * @param req 登录参数
     * @return 登录服务，需要返回im userSign和app的userSign
     */
    @Override
    public ResponseVO<LoginResp> login(LoginReq req) {
        LoginResp resp = new LoginResp();
        if (ObjectUtil.equal(LoginTypeEnum.USERNAME_PASSWORD.getCode(), req.getLoginType())) {
            AppUserEntity user = appUserService.getUserByUserNameAndPassword(req.getUserName(), req.getPassword());
            if (ObjectUtil.isNotNull(user)) {
                SigAPI sigAPI = new SigAPI(appConfig.getAppId(), appConfig.getPrivateKey());
                String userSign = sigAPI.genUserSign(user.getUserId(), 500000);
                resp.setImUserSign(userSign);
                resp.setUserSign(userSign); // 根据自己app业务生成token
                resp.setUserId(user.getUserId());
            } else {
                return ResponseVO.errorResponse(AppUserErrorCode.USERNAME_OR_PASSWORD_ERROR);
            }
        }
        if (ObjectUtil.equal(LoginTypeEnum.SMS_CODE.getCode(), req.getLoginType())) {
            // TODO 校验短信验证码
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 注册我们的服务并向im导入用户
     *
     * @param req 登录参数
     * @return 路由信息
     */
    @Override
    @Transactional
    public AppUserEntity register(RegisterReq req) {
        if (ObjectUtil.equal(RegisterTypeEnum.USER_NAME.getCode(), req.getRegisterType())) {
            AppUserEntity user = appUserService.getUserByUserName(req.getUserName());
            if (ObjectUtil.isNotNull(user)) {
                throw new BusinessException(AppUserErrorCode.USER_REGISTERED);
            }
        }
        if (ObjectUtil.equal(RegisterTypeEnum.MOBILE.getCode(), req.getRegisterType())) {
            AppUserEntity user = appUserService.getUserByMobile(req.getMobile());
            if (ObjectUtil.isNotNull(user)) {
                throw new BusinessException(AppUserErrorCode.USER_REGISTERED);
            }
        }
        return appUserService.registerUser(req);
    }
}
