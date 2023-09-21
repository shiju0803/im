/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sj.app.entry.AppUserEntity;
import com.sj.app.mapper.AppUserMapper;
import com.sj.app.service.AppUserService;
import com.sj.app.service.ImService;
import com.sj.app.web.req.RegisterReq;
import com.sj.app.web.resp.ImportUserResp;
import com.sj.im.common.enums.RegisterTypeEnum;
import com.sj.im.common.enums.exception.AppUserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户相关接口实现类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUserEntity> implements AppUserService {
    @Resource
    private AppUserMapper appUserMapper;
    @Resource
    private ImService imService;

    /**
     * 根据用户名和密码查询用户信息
     *
     * @param userName 用户名
     * @param password 密码
     */
    @Override
    public AppUserEntity getUserByUserNameAndPassword(String userName, String password) {
        LambdaQueryWrapper<AppUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUserEntity::getUserName, userName);
        wrapper.eq(AppUserEntity::getPassword, password);
        return appUserMapper.selectOne(wrapper);
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param mobile 手机号
     */
    @Override
    public AppUserEntity getUserByMobile(String mobile) {
        LambdaQueryWrapper<AppUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUserEntity::getMobile, mobile);
        return appUserMapper.selectOne(wrapper);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名称
     */
    @Override
    public AppUserEntity getUserByUserName(String userName) {
        LambdaQueryWrapper<AppUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUserEntity::getUserName, userName);
        return appUserMapper.selectOne(wrapper);
    }

    /**
     * 用户注册具体逻辑
     *
     * @param req 注册信息
     */
    @Override
    public AppUserEntity registerUser(RegisterReq req) {
        AppUserEntity user = new AppUserEntity();
        user.setUserName(req.getUserName());
        user.setPassword(req.getPassword());
        user.setMobile(req.getMobile());
        if (ObjectUtil.equal(req.getRegisterType(), RegisterTypeEnum.MOBILE.getCode())) {
            user.setUserId(req.getMobile());
        }
        if (ObjectUtil.equal(req.getRegisterType(), RegisterTypeEnum.USER_NAME.getCode())) {
            user.setUserId(req.getUserName());
        }
        appUserMapper.insert(user);

        List<AppUserEntity> users = new ArrayList<>();
        users.add(user);
        ResponseVO<ImportUserResp> responseVO = imService.importUser(users);
        if (responseVO.isOk()) {
            ImportUserResp importUserResp = JSONUtil.parseObj(responseVO.getData()).toBean(ImportUserResp.class);
            List<String> successId = importUserResp.getSuccessId();
            if (!successId.contains(user.getUserId())) {
                throw new BusinessException(AppUserErrorCode.REGISTER_ERROR);
            }
            return user;
        }
        throw new BusinessException(responseVO.getCode(), responseVO.getMsg());
    }
}
