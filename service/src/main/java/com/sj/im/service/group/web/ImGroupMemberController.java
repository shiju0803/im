/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.AddMemberResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组成员接口
 */
@Api(tags = "V1/群组成员接口")
@RestController
@RequestMapping("/v1/group/member")
public class ImGroupMemberController {
    @Resource
    private ImGroupMemberService imGroupMemberService;

    @ApiOperation("导入群组成员")
    @PostMapping("/importGroupMember")
    public ResponseVO<List<AddMemberResp>> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        List<AddMemberResp> addMember = imGroupMemberService.importGroupMember(req);
        return ResponseVO.successResponse(addMember);
    }

    @ApiOperation("获取群组中的所有成员信息")
    @GetMapping("/list")
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDto> groupMember = imGroupMemberService.getGroupMember(groupId, appId);
        return ResponseVO.successResponse(groupMember);
    }

    @ApiOperation("转让群主")
    @GetMapping("/transferGroupMember")
    public ResponseVO<String> transferGroupMember(String owner, String groupId, Integer appId) {
        imGroupMemberService.transferGroupMember(owner, groupId, appId);
        return ResponseVO.successResponse();
    }

    @ApiOperation("拉人入群")
    @RequestMapping("/addMember")
    public ResponseVO<List<AddMemberResp>> addMember(@RequestBody @Validated AddGroupMemberReq req) {
        List<AddMemberResp> respList = imGroupMemberService.addMember(req);
        return ResponseVO.successResponse(respList);
    }

    @ApiOperation("踢人")
    @RequestMapping("/removeMember")
    public ResponseVO<String> removeMember(@RequestBody @Validated RemoveGroupMemberReq req) {
        imGroupMemberService.removeMember(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("退出群聊")
    @RequestMapping("/exitGroup")
    public ResponseVO<String> exitGroup(@RequestBody @Validated ExitGroupReq req) {
        imGroupMemberService.exitGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("修改群成员信息")
    @RequestMapping("/updateGroupMember")
    public ResponseVO<String> updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req) {
        imGroupMemberService.updateGroupMember(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("禁言群组成员")
    @RequestMapping("/speak")
    public ResponseVO<String> speak(@RequestBody @Validated SpeakMemberReq req) {
        imGroupMemberService.speak(req);
        return ResponseVO.successResponse();
    }
}