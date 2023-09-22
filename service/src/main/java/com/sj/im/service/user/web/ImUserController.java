/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.route.RouteInfo;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.user.service.ImUserStatusService;
import com.sj.im.service.user.web.req.*;
import com.sj.im.service.user.web.resp.ImportUserResp;
import com.sj.im.service.user.web.resp.UserOnlineStatusResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 处理用户数据
 *
 * @author ShiJu
 * @version 1.0
 */
@Api(tags = "V1/处理用户数据")
@RestController
@RequestMapping("v1/user")
public class ImUserController {
    @Resource
    private ImUserService imUserService;
    @Resource
    private ImUserStatusService imUserStatusService;

    @ApiOperation(value = "导入用户的方法")
    @PostMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody @Validated ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        ImportUserResp importUserResp = imUserService.importUser(req);
        return ResponseVO.successResponse(importUserResp);
    }

    @ApiOperation(value = "删除用户的方法")
    @DeleteMapping("/deleteUser")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        ImportUserResp deletedUser = imUserService.deleteUser(req);
        return ResponseVO.successResponse(deletedUser);
    }

    @ApiOperation(value = "获取登录地址")
    @GetMapping("/login")
    public ResponseVO<RouteInfo> login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);
        RouteInfo login = imUserService.login(req);
        return ResponseVO.successResponse(login);
    }

    @ApiOperation(value = "获取用户序列号", notes = "用于客户端判断是否需要进行数据同步")
    @PostMapping("/getUserSequence")
    public ResponseVO<Map<Object, Object>> getUserSequence(@RequestBody @Validated GetUserSequenceReq req,
                                                           Integer appId) {
        req.setAppId(appId);
        Map<Object, Object> map = imUserService.getUserSequence(req);
        return ResponseVO.successResponse(map);
    }

    @ApiOperation("订阅用户在线状态")
    @PostMapping("/subscribeUserOnlineStatus")
    public ResponseVO<String> subscribeUserOnlineStatus(@RequestBody @Validated SubscribeUserOnlineStatusReq req,
                                                        Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        imUserStatusService.subscribeUserOnlineStatus(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("设置用户自定义状态")
    @PostMapping("/setUserCustomerStatus")
    public ResponseVO<String> setUserCustomerStatus(@RequestBody @Validated SetUserCustomerStatusReq req, Integer appId,
                                                    String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        imUserStatusService.setUserCustomerStatus(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("查询好友在线状态")
    @PostMapping("/queryFriendOnlineStatus")
    public ResponseVO<Map<String, UserOnlineStatusResp>> queryFriendOnlineStatus(
            @RequestBody @Validated PullFriendOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryFriendOnlineStatus(req));
    }

    @ApiOperation("查询用户在线状态")
    @PostMapping("/queryUserOnlineStatus")
    public ResponseVO<Map<String, UserOnlineStatusResp>> queryUserOnlineStatus(
            @RequestBody @Validated PullUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryUserOnlineStatus(req));
    }
}
