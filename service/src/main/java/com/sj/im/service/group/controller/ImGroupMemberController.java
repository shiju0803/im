/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.model.resp.AddMemberResp;
import com.sj.im.service.group.service.ImGroupMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组成员接口
 */
@Api(tags = "GroupMember")
@RestController
@RequestMapping("/v1/group/member")
public class ImGroupMemberController {
    @Autowired
    private ImGroupMemberService imGroupMemberService;

    @ApiOperation("导入群组成员")
    @PostMapping("/importGroupMember")
    public ResponseVO<List<AddMemberResp>> importGroupMember(@RequestBody ImportGroupMemberReq req) {
        return imGroupMemberService.importGroupMember(req);
    }

    @ApiOperation("获取群组中的所有成员信息")
    @GetMapping("/list")
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        return imGroupMemberService.getGroupMember(groupId, appId);
    }

    @ApiOperation("转让群主")
    @GetMapping("/transferGroupMember")
    public ResponseVO<String> transferGroupMember(String owner, String groupId, Integer appId) {
        return imGroupMemberService.transferGroupMember(owner, groupId, appId);
    }

    @ApiOperation("拉人入群")
    @RequestMapping("/addMember")
    public ResponseVO<Object> addMember(@RequestBody AddGroupMemberReq req) {
        return imGroupMemberService.addMember(req);
    }

    @ApiOperation("踢人")
    @RequestMapping("/removeMember")
    public ResponseVO<String> removeMember(@RequestBody RemoveGroupMemberReq req) {
        return imGroupMemberService.removeMember(req);
    }

    @ApiOperation("退出群聊")
    @RequestMapping("/exitGroup")
    public ResponseVO<String> exitGroup(@RequestBody ExitGroupReq req) {
        return imGroupMemberService.exitGroup(req);
    }

    @ApiOperation("修改群成员信息")
    @RequestMapping("/updateGroupMember")
    public ResponseVO<String> updateGroupMember(@RequestBody UpdateGroupMemberReq req) {
        return imGroupMemberService.updateGroupMember(req);
    }

    @ApiOperation("禁言群组成员")
    @RequestMapping("/speak")
    public ResponseVO<String> speak(@RequestBody SpeakMemberReq req) {
        return imGroupMemberService.speak(req);
    }
}