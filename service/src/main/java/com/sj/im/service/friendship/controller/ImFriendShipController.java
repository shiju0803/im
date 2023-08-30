/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.controller;

import com.sj.im.common.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.dao.ImFriendShipEntity;
import com.sj.im.service.friendship.model.req.CheckFriendShipReq;
import com.sj.im.service.friendship.model.req.FriendShipReq;
import com.sj.im.service.friendship.model.req.ImportFriendShipReq;
import com.sj.im.service.friendship.model.req.RelationReq;
import com.sj.im.service.friendship.model.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.model.resp.ImportFriendShipResp;
import com.sj.im.service.friendship.service.ImFriendShipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 好友业务接口
 */
@Api(tags = "V1/FriendShip")
@RestController
@RequestMapping("/v1/friendship")
public class ImFriendShipController {
    @Resource
    private ImFriendShipService imFriendShipService;

    @ApiOperation("导入关系链")
    @PostMapping("/importFriendShip")
    public ResponseVO<ImportFriendShipResp> importFriendShip(@RequestBody @Validated ImportFriendShipReq req) {
        return imFriendShipService.importFriendShip(req);
    }

    @ApiOperation("添加好友")
    @PostMapping("/addFriendShip")
    public ResponseVO<String> addFriendShip(@RequestBody @Validated FriendShipReq req) {
        return imFriendShipService.addFriend(req);
    }

    @ApiOperation("修改好友")
    @PutMapping("/updateFriendShip")
    public ResponseVO<String> updateFriendShip(@RequestBody @Validated FriendShipReq req) {
        return imFriendShipService.updateFriend(req);
    }

    @ApiOperation("删除好友关系")
    @DeleteMapping("/deleteFriendShip")
    public ResponseVO<String> deleteFriendShip(@RequestBody @Validated RelationReq req) {
        return imFriendShipService.deleteFriend(req);
    }

    @ApiOperation("删除所有好友关系")
    @DeleteMapping("/deleteAllFriendShip")
    public ResponseVO<String> deleteAllFriendShip(@RequestBody @Validated FriendShipReq req) {
        return imFriendShipService.deleteAllFriend(req);
    }

    @ApiOperation("拉取指定好友信息")
    @GetMapping("/getRelation")
    public ResponseVO<ImFriendShipEntity> getRelation(@RequestBody @Validated RelationReq req) {
        return imFriendShipService.getRelation(req);
    }

    @ApiOperation("拉取指定用户所有好友信息")
    @GetMapping("/getAllFriendShip")
    public ResponseVO<List<ImFriendShipEntity>> getAllFriendShip(@RequestBody @Validated RelationReq req) {
        return imFriendShipService.getAllFriend(req);
    }

    @ApiOperation("校验好友关系")
    @GetMapping("/checkFriendShip")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendShip(@RequestBody @Validated CheckFriendShipReq req) {
        return imFriendShipService.checkFriendShip(req);
    }

    @ApiOperation("拉入黑名单")
    @PutMapping("/addFriendSipBlack")
    public ResponseVO<String> addFriendSipBlack(@RequestBody @Validated RelationReq req) {
        return imFriendShipService.addBlack(req);
    }

    @ApiOperation("拉出黑名单")
    @PutMapping("/deleteFriendSipBlack")
    public ResponseVO<String> deleteFriendSipBlack(@RequestBody @Validated RelationReq req) {
        return imFriendShipService.deleteBlack(req);
    }

    @ApiOperation("校验黑名单")
    @GetMapping("/checkFriendBlack")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendBlack(@RequestBody @Validated CheckFriendShipReq req) {
        return imFriendShipService.checkFriendBlack(req);
    }

    @ApiOperation("同步好友列表")
    @PostMapping("/syncFriendShipList")
    public ResponseVO<SyncResp<ImFriendShipEntity>> syncFriendShipList(@RequestBody @Validated SyncReq req) {
        return imFriendShipService.syncFriendShipList(req);
    }
}
