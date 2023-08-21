/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.controller;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.model.req.CheckFriendShipReq;
import com.sj.im.service.friendship.model.req.FriendShipReq;
import com.sj.im.service.friendship.model.req.GetRelationReq;
import com.sj.im.service.friendship.model.req.ImportFriendShipReq;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.user.dao.ImUserDataEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友业务接口
 */
@RestController("/v1/friendship")
public class ImFriendShipController {
    @Resource
    private ImFriendShipService imFriendShipService;

    @ApiOperation("导入关系链")
    @RequestMapping("/importFriendShip")
    public ResponseVO<ImportFriendShipResp> importFriendShip(@RequestBody ImportFriendShipReq req) {
        return imFriendShipService.importFriendShip(req);
    }

    @ApiOperation("添加好友")
    @RequestMapping("/addFriendShip")
    public ResponseVO<ImUserDataEntity> addFriendShip(@RequestBody FriendShipReq req) {
        return imFriendShipService.addFriendShip(req);
    }

    @ApiOperation("修改好友")
    @RequestMapping("/updateFriendShip")
    public ResponseVO<ImUserDataEntity> updateFriendShip(@RequestBody FriendShipReq req) {
        return imFriendShipService.updateFriend(req);
    }

    @ApiOperation("删除好友关系")
    @RequestMapping("/deleteFriendShip")
    public ResponseVO<String> deleteFriendShip(@RequestBody FriendShipReq req) {
        return imFriendShipService.deleteFriend(req);
    }

    @ApiOperation("删除所有好友关系")
    @RequestMapping("/deleteAllFriendShip")
    public ResponseVO<String> deleteAllFriendShip(@RequestBody FriendShipReq req) {
        return imFriendShipService.deleteAllFriend(req);
    }

    @ApiOperation("拉取指定好友信息")
    @RequestMapping("/getRelation")
    public ResponseVO<ImFriendShipEntity> getRelation(@RequestBody GetRelationReq req) {
        return imFriendShipService.getRelation(req);
    }

    @ApiOperation("拉取指定用户所有好友信息")
    @RequestMapping("/getAllFriendShip")
    public ResponseVO<List<ImFriendShipEntity>> getAllFriendShip(@RequestBody GetRelationReq req) {
        return imFriendShipService.getAllFriend(req);
    }

    @ApiOperation("校验好友关系")
    @RequestMapping("/checkFriendShip")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendShip(@RequestBody CheckFriendShipReq req) {
        return imFriendShipService.checkFriendShip(req);
    }
}
