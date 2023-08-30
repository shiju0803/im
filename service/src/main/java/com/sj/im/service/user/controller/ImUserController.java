/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.controller;

import com.sj.im.common.ResponseVO;
import com.sj.im.common.route.RouteHandle;
import com.sj.im.common.route.RouteInfo;
import com.sj.im.common.util.RouteInfoParseUtil;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.model.req.LoginReq;
import com.sj.im.service.user.model.req.UserBatchReq;
import com.sj.im.service.user.model.resp.ImportUserResp;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.helper.ZookeeperHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private RouteHandle routeHandle;
    @Resource
    private ZookeeperHelper zookeeperHelper;

    @ApiOperation(value = "导入用户的方法")
    @PutMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody @Validated ImportUserReq req) {
        return imUserService.importUser(req);
    }

    @ApiOperation(value = "删除用户的方法")
    @DeleteMapping("/deleteUser")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated UserBatchReq req) {
        return imUserService.deleteUser(req);
    }

    @ApiOperation(value = "获取登录地址")
    @GetMapping("/login")
    public ResponseVO<RouteInfo> login(@RequestBody @Validated LoginReq req) {
        ResponseVO<String> login = imUserService.login(req);
        if (login.isOk()) {
            // 去zk获取一个im的地址，返回给sdk
            List<String> nodes = zookeeperHelper.getAllNodeByClientType(req.getClientType());
            // ip:port
            String nodeInfo = routeHandle.routeServer(nodes, req.getUserId());
            RouteInfo routeInfo = RouteInfoParseUtil.parse(nodeInfo);

            return ResponseVO.successResponse(routeInfo);
        }
        return ResponseVO.successResponse();
    }
}
