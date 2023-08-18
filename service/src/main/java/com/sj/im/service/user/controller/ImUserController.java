/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.service.ImUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 处理用户数据
 * @author ShiJu
 * @version 1.0
 */
@RestController
@RequestMapping("/v1/user")
public class ImUserController {
    @Resource
    private ImUserService imUserService;

    @RequestMapping("/importUser")
    public ResponseVO importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.importUser(req);
    }
}
