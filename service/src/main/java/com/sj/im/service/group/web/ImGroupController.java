/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.web.req.GetGroupInfoReq;
import com.sj.im.service.group.entry.ImGroupEntity;
import com.sj.im.service.group.service.GroupMessageService;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.GetGroupResp;
import com.sj.im.service.group.web.resp.GetJoinedGroupResp;
import com.sj.im.service.message.web.resp.SendMessageResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 群组接口
 *
 * @author ShiJu
 * @version 1.0
 */
@Api(tags = "V1/群组接口")
@RestController
@RequestMapping("/v1/group")
public class ImGroupController {
    @Resource
    private ImGroupService groupService;
    @Resource
    private GroupMessageService groupMessageService;

    @ApiOperation("导入群组")
    @PostMapping("/importGroup")
    public ResponseVO<String> importGroup(@RequestBody @Validated ImportGroupReq req) {
        groupService.importGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("新建群组")
    @PostMapping("/createGroup")
    public ResponseVO<String> createGroup(@RequestBody @Validated CreateGroupReq req) {
        groupService.createGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("修改群消息")
    @PutMapping("/updateGroupInfo")
    public ResponseVO<String> updateGroupInfo(@RequestBody @Validated UpdateGroupReq req) {
        groupService.updateBaseGroupInfo(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("获取群组的具体信息")
    @GetMapping("/getGroupInfo")
    public ResponseVO<GetGroupResp> getGroupInfo(@RequestBody @Validated GetGroupInfoReq req) {
        GetGroupResp groupInfo = groupService.getGroupInfo(req);
        return ResponseVO.successResponse(groupInfo);
    }

    @ApiOperation("获取用户加入的群组列表")
    @GetMapping("/getJoinedGroup")
    public ResponseVO<GetJoinedGroupResp> getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req) {
        GetJoinedGroupResp joinedGroup = groupService.getJoinedGroup(req);
        return ResponseVO.successResponse(joinedGroup);
    }

    @ApiOperation("解散群组")
    @PutMapping("/destroyGroup")
    public ResponseVO<String> destroyGroup(@RequestBody @Validated DestroyGroupReq req) {
        groupService.destroyGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("转让群主")
    @PutMapping("/transferGroup")
    public ResponseVO<String> transferGroup(@RequestBody @Validated TransferGroupReq req) {
        groupService.transferGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("群组禁言")
    @PutMapping("/forbidSendMessage")
    public ResponseVO<String> forbidSendMessage(@RequestBody @Validated MuteGroupReq req) {
        groupService.muteGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("同步加入的群组列表（增量拉取）")
    @PostMapping("/syncJoinedGroup")
    public ResponseVO<SyncResp<ImGroupEntity>> syncJoinedGroup(@RequestBody @Validated SyncReq req) {
        SyncResp<ImGroupEntity> syncResp = groupService.syncJoinedGroupList(req);
        return ResponseVO.successResponse(syncResp);
    }

    @ApiOperation(value = "发送群聊消息")
    @PostMapping("/send")
    public ResponseVO<SendMessageResp> send(@RequestBody @Validated SendGroupMessageReq req) {
        return ResponseVO.successResponse(groupMessageService.send(req));
    }
}