/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.conversation.entry.ImConversationSetEntity;
import com.sj.im.service.conversation.service.ConversationService;
import com.sj.im.service.conversation.web.req.DeleteConversationReq;
import com.sj.im.service.conversation.web.req.UpdateConversationReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("v1/conversation")
@Api(tags = "V1/会话相关接口")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除会话", notes = "根据会话 ID 删除会话")
    public ResponseVO<String> deleteConversation(@RequestBody @Validated DeleteConversationReq req, Integer appId) {
        req.setAppId(appId);
        conversationService.deleteConversation(req);
        return ResponseVO.successResponse();
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "更新会话", notes = "根据会话 ID 更新会话")
    public ResponseVO<String> updateConversation(@RequestBody @Validated UpdateConversationReq req, Integer appId) {
        req.setAppId(appId);
        conversationService.updateConversation(req);
        return ResponseVO.successResponse();
    }

    @PostMapping(value = "/sync")
    @ApiOperation(value = "同步会话列表", notes = "同步会话列表")
    public ResponseVO<SyncResp<ImConversationSetEntity>> syncFriendShipList(@RequestBody @Validated SyncReq req,
                                                                            Integer appId) {
        req.setAppId(appId);
        SyncResp<ImConversationSetEntity> syncResp = conversationService.syncConversationSet(req);
        return ResponseVO.successResponse(syncResp);
    }
}