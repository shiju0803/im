/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.service.friendship.entry.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupMemberReq;
import com.sj.im.service.friendship.web.req.DeleteFriendShipGroupReq;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务接口
 */
@Api(tags = "V1/好友分组控制器")
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendShipGroupController {
    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;
    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @ApiOperation("新增好友分组")
    @PostMapping("/add")
    public ResponseVO<String> add(@RequestBody @Validated AddFriendShipGroupReq req) {
        imFriendShipGroupService.addGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("删除好友分组")
    @DeleteMapping("/del")
    public ResponseVO<String> del(@RequestBody @Validated DeleteFriendShipGroupReq req) {
        imFriendShipGroupService.deleteGroup(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("获取分组")
    @PostMapping("/list")
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId) {
        ImFriendShipGroupEntity group = imFriendShipGroupService.getGroup(fromId, groupName, appId);
        return ResponseVO.successResponse(group);
    }

    @ApiOperation("添加组内成员")
    @PostMapping("/member/add")
    public ResponseVO<List<String>> addMember(@RequestBody @Validated AddFriendShipGroupMemberReq req) {
        List<String> list = imFriendShipGroupMemberService.addGroupMember(req);
        return ResponseVO.successResponse(list);
    }

    @ApiOperation("删除组内成员")
    @DeleteMapping("/member/del")
    public ResponseVO<List<String>> delMember(@RequestBody @Validated DeleteFriendShipGroupMemberReq req) {
        List<String> list = imFriendShipGroupMemberService.delGroupMember(req);
        return ResponseVO.successResponse(list);
    }

    @ApiOperation("清空组内所有成员")
    @DeleteMapping("/member/clear")
    public ResponseVO<Integer> clearMember(Long groupId) {
        int cleared = imFriendShipGroupMemberService.clearGroupMember(groupId);
        return ResponseVO.successResponse(cleared);
    }
}