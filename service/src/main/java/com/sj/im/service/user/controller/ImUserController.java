/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.model.req.UserBatchReq;
import com.sj.im.service.user.model.resp.ImportUserResp;
import com.sj.im.service.user.service.ImUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 处理用户数据
 */
@Api(tags = "User")
@RestController("/v1/user")
public class ImUserController {
    @Resource
    private ImUserService imUserService;

    @ApiOperation(value = "导入用户的方法")
    @PutMapping("/importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req) {
        return imUserService.importUser(req);
    }

    @ApiOperation(value = "删除用户的方法")
    @DeleteMapping("/deleteUser")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated UserBatchReq req) {
        return imUserService.deleteUser(req);
    }
}
