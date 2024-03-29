/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.service.friendship.entry.ImFriendShipRequestEntity;
import com.sj.im.service.friendship.service.ImFriendShipRequestService;
import com.sj.im.service.friendship.web.req.ApproveFriendRequestReq;
import com.sj.im.service.friendship.web.req.ReadFriendShipRequestReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 好友申请接口
 *
 * @author ShiJu
 * @version 1.0
 */
@Api(tags = "V1/好友请求控制器")
@RestController
@RequestMapping("/v1/friendshipRequest")
public class ImFriendShipRequestController {
    @Resource
    ImFriendShipRequestService imFriendShipRequestService;

    @ApiOperation("审批好友请求")
    @PutMapping("/approveFriendRequest")
    public ResponseVO<String> approveFriendRequest(@RequestBody @Validated ApproveFriendRequestReq req, Integer appId,
                                                   String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        imFriendShipRequestService.approveFriendRequest(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("已读请求")
    @PutMapping("/readFriendShipRequestReq")
    public ResponseVO<String> readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req,
                                                       Integer appId) {
        req.setAppId(appId);
        imFriendShipRequestService.readFriendShipRequestReq(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("获得好友请求")
    @GetMapping("/getFriendRequest")
    public ResponseVO<List<ImFriendShipRequestEntity>> getFriendRequest(String fromId, Integer appId) {
        List<ImFriendShipRequestEntity> request = imFriendShipRequestService.getFriendRequest(fromId, appId);
        return ResponseVO.successResponse(request);
    }
}