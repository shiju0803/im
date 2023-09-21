/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web;

import com.sj.app.service.LoginService;
import com.sj.app.web.req.LoginReq;
import com.sj.app.web.req.RegisterReq;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.route.RouteInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录注册接口
 *
 * @author ShiJu
 * @version 1.0
 */
@RestController
@RequestMapping("v1")
@Api(tags = "V1/用户登录控制器")
public class LoginController {
    @Resource
    private LoginService loginService;

    @ApiOperation(value = "用户登录")
    @GetMapping("/login")
    public ResponseVO<RouteInfo> login(@RequestBody @Validated LoginReq req) {
        RouteInfo login = loginService.login(req);
        return ResponseVO.successResponse(login);
    }

    @ApiOperation(value = "用户注册")
    @GetMapping("/register")
    public ResponseVO<String> register(@RequestBody @Validated RegisterReq req) {
        loginService.register(req);
        return ResponseVO.successResponse();
    }
}
