/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.sj.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.sj.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.sj.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.sj.im.service.friendship.service.ImFriendShipGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友分组业务接口
 */
@Api(tags = "FriendGroup")
@RestController
@RequestMapping("/v1/friendship/group")
public class ImFriendShipGroupController {
    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;
    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @ApiOperation("新增好友分组")
    @PostMapping("/add")
    public ResponseVO<String> add(@RequestBody AddFriendShipGroupReq req)  {
        return imFriendShipGroupService.addGroup(req);
    }

    @ApiOperation("删除好友分组")
    @DeleteMapping("/del")
    public ResponseVO<String> del(@RequestBody DeleteFriendShipGroupReq req)  {
        return imFriendShipGroupService.deleteGroup(req);
    }

    @ApiOperation("获取分组")
    @PostMapping("/list")
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId)  {
        return imFriendShipGroupService.getGroup(fromId, groupName, appId);
    }

    @ApiOperation("添加组内成员")
    @PostMapping("/member/add")
    public ResponseVO<Object> addMember(@RequestBody AddFriendShipGroupMemberReq req)  {
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    @ApiOperation("删除组内成员")
    @DeleteMapping("/member/del")
    public ResponseVO<Object> delMember(@RequestBody DeleteFriendShipGroupMemberReq req)  {
        return imFriendShipGroupMemberService.delGroupMember(req);
    }

    @ApiOperation("清空组内所有成员")
    @DeleteMapping("/member/clear")
    public ResponseVO<Integer> clearMember(Long groupId)  {
        return imFriendShipGroupMemberService.clearGroupMember(groupId);
    }
}