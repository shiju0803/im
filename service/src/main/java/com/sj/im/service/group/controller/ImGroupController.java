/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.model.req.GetGroupInfoReq;
import com.sj.im.service.group.dao.ImGroupEntity;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.service.ImGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组接口
 */
@Api(tags = "Group")
@RestController
@RequestMapping("/v1/group")
public class ImGroupController {
    @Resource
    private ImGroupService groupService;

    @ApiOperation("导入群组")
    @PostMapping("/importGroup")
    public ResponseVO<String> importGroup(@RequestBody ImportGroupReq req) {
        return groupService.importGroup(req);
    }

    @ApiOperation("新建群组")
    @PostMapping("/createGroup")
    public ResponseVO<String> createGroup(@RequestBody CreateGroupReq req) {
        return groupService.createGroup(req);
    }

    @ApiOperation("修改群消息")
    @PutMapping("/updateGroupInfo")
    public ResponseVO<String> updateGroupInfo(@RequestBody UpdateGroupReq req) {
        return groupService.updateBaseGroupInfo(req);
    }

    @ApiOperation("获取群组的具体信息")
    @GetMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(@RequestBody GetGroupInfoReq req) {
        return groupService.getGroupInfo(req);
    }

    @ApiOperation("获取用户加入的群组列表")
    @GetMapping("/getJoinedGroup")
    public ResponseVO<List<ImGroupEntity>> getJoinedGroup(@RequestBody GetJoinedGroupReq req) {
        return groupService.getJoinedGroup(req);
    }

    @ApiOperation("解散群组")
    @PutMapping("/destroyGroup")
    public ResponseVO<String> destroyGroup(@RequestBody DestroyGroupReq req) {
        return groupService.destroyGroup(req);
    }

    @ApiOperation("转让群主")
    @PutMapping("/transferGroup")
    public ResponseVO<String> transferGroup(@RequestBody TransferGroupReq req) {
        return groupService.transferGroup(req);
    }

    @ApiOperation("群组禁言")
    @PutMapping("/forbidSendMessage")
    public ResponseVO<String> forbidSendMessage(@RequestBody MuteGroupReq req) {
        return groupService.muteGroup(req);
    }

    @ApiOperation("同步加入的群组列表（增量拉取）")
    @PostMapping("/syncJoinedGroup")
    public ResponseVO<SyncResp<ImGroupEntity>> syncJoinedGroup(@RequestBody SyncReq req) {
        return groupService.syncJoinedGroupList(req);
    }
}