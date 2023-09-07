/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.common.model.message.CheckSendMessageReq;
import com.sj.im.common.model.message.OfflineMessageContent;
import com.sj.im.service.message.service.MessageSyncService;
import com.sj.im.service.message.service.P2PMessageService;
import com.sj.im.service.message.web.rep.SendMessageReq;
import com.sj.im.service.message.web.resp.SendMessageResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息管理
 */
@RestController
@RequestMapping("v1/message")
@Api(tags = "V1/消息管理")
public class MessageController {
    @Resource
    private P2PMessageService p2PMessageService;
    @Resource
    private MessageSyncService messageSyncService;

    @ApiOperation(value = "发送消息")
    @PostMapping("/send")
    public ResponseVO<SendMessageResp> send(@RequestBody @Validated SendMessageReq req) {
        return ResponseVO.successResponse(p2PMessageService.send(req));
    }

    @ApiOperation(value = "检查发送消息权限")
    @PostMapping("/checkSend")
    public ResponseVO<Object> checkSend(@RequestBody @Validated CheckSendMessageReq req) {
        p2PMessageService.imServerPermissionCheck(req.getFromId(), req.getToId(), req.getAppId());
        return ResponseVO.successResponse();
    }

    @ApiOperation(value = "同步离线消息")
    @PostMapping("/syncOfflineMessage")
    public ResponseVO<SyncResp<OfflineMessageContent>> syncOfflineMessage(@RequestBody @Validated SyncReq req) {
        return ResponseVO.successResponse(messageSyncService.syncOfflineMessage(req));
    }
}
