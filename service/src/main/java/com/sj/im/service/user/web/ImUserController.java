/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.route.RouteInfo;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.user.web.req.DeleteUserReq;
import com.sj.im.service.user.web.req.GetUserSequenceReq;
import com.sj.im.service.user.web.req.ImportUserReq;
import com.sj.im.service.user.web.req.LoginReq;
import com.sj.im.service.user.web.resp.ImportUserResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 处理用户数据
 */
@Api(tags = "V1/User")
@RestController
@RequestMapping("/v1/user")
public class ImUserController {
    @Resource
    private ImUserService imUserService;

    @ApiOperation(value = "导入用户的方法")
    @PutMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody @Validated ImportUserReq req) {
        ImportUserResp importUserResp = imUserService.importUser(req);
        return ResponseVO.successResponse(importUserResp);
    }

    @ApiOperation(value = "删除用户的方法")
    @DeleteMapping("/deleteUser")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req) {
        ImportUserResp deletedUser = imUserService.deleteUser(req);
        return ResponseVO.successResponse(deletedUser);
    }

    @ApiOperation(value = "获取登录地址")
    @GetMapping("/login")
    public ResponseVO<RouteInfo> login(@RequestBody @Validated LoginReq req) {
        RouteInfo login = imUserService.login(req);
        return ResponseVO.successResponse(login);
    }


    @ApiOperation(value = "获取用户序列号", notes = "用于客户端判断是否需要进行数据同步")
    @PostMapping("/getUserSequence")
    public ResponseVO<Map<Object, Object>> getUserSequence(@RequestBody @Validated GetUserSequenceReq req) {
        Map<Object, Object> map = imUserService.getUserSequence(req);
        return ResponseVO.successResponse(map);
    }
}
