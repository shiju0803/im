/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.model.req.ModifyUserInfoReq;
import com.sj.im.service.user.model.req.UserBatchReq;
import com.sj.im.service.user.model.req.UserSingleReq;
import com.sj.im.service.user.service.ImUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户数据处理
 */
@Api(tags = "UserDate")
@RestController("/v1/user/data")
public class ImUserDataController {
    @Resource
    private ImUserService imUserService;

    @ApiOperation(value = "批量获取用户信息的方法")
    @GetMapping("/getBatchUserInfo")
    public ResponseVO getBatchUserInfo(@RequestBody UserBatchReq req){
        return imUserService.getUserInfo(req);
    }

    @ApiOperation(value = "单独获取用户信息的方法")
    @GetMapping("/getSingleUserInfo")
    public ResponseVO getSingleUserInfo(@RequestBody UserSingleReq req){
        return imUserService.getSingleUserInfo(req);
    }

    @ApiOperation(value = "修改用户信息的方法")
    @PutMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody ModifyUserInfoReq req){
        return imUserService.modifyUserInfo(req);
    }
}