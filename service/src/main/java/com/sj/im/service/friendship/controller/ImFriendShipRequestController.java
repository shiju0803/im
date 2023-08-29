/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.model.req.FriendShipReq;
import com.sj.im.service.friendship.service.ImFriendShipRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友申请接口
 */
@Api(tags = "V1/FriendRequest")
@RestController
@RequestMapping("/v1/friendshipRequest")
public class ImFriendShipRequestController {
    @Resource
    ImFriendShipRequestService imFriendShipRequestService;

    @ApiOperation("新增好友请求")
    @PostMapping("/addFriendRequest")
    public ResponseVO<String> addFriendRequest(@RequestBody FriendShipReq req) {
        return imFriendShipRequestService.addFriendshipRequest(req);
    }

    @ApiOperation("审批好友请求")
    @PutMapping("/approveFriendRequest")
    public ResponseVO<String> approveFriendRequest(@RequestBody ApproveFriendRequestReq req) {
        return imFriendShipRequestService.approveFriendRequest(req);
    }

    @ApiOperation("已读请求")
    @PutMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody FriendShipReq req) {
        return imFriendShipRequestService.readFriendShipRequestReq(req);
    }

    @ApiOperation("获得好友请求")
    @GetMapping("/getFriendRequest")
    public ResponseVO<List<ImFriendShipRequestEntity>> getFriendRequest(@RequestBody @Validated FriendShipReq req) {
        return imFriendShipRequestService.getFriendRequest(req);
    }
}