/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.user.web.req.GetUserInfoOneReq;
import com.sj.im.service.user.web.req.GetUserInfoReq;
import com.sj.im.service.user.web.req.ModifyUserInfoReq;
import com.sj.im.service.user.web.resp.GetUserInfoResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户数据处理
 *
 * @author ShiJu
 * @version 1.0
 */
@Api(tags = "V1/用户数据控制器")
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {
    @Resource
    private ImUserService imUserService;

    @ApiOperation(value = "批量获取用户信息的方法")
    @GetMapping("/getBatchUserInfo")
    public ResponseVO<GetUserInfoResp> getBatchUserInfo(@RequestBody @Validated GetUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        GetUserInfoResp userInfo = imUserService.getUserInfo(req);
        return ResponseVO.successResponse(userInfo);
    }

    @ApiOperation(value = "单独获取用户信息的方法")
    @GetMapping("/getSingleUserInfo")
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(@RequestBody @Validated GetUserInfoOneReq req,
                                                          Integer appId) {
        req.setAppId(appId);
        ImUserDataEntity userInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        return ResponseVO.successResponse(userInfo);
    }

    @ApiOperation(value = "修改用户信息的方法")
    @PutMapping("/modifyUserInfo")
    public ResponseVO<String> modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        imUserService.modifyUserInfo(req);
        return ResponseVO.successResponse();
    }
}
