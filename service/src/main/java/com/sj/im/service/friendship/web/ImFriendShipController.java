/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.entry.ImFriendShipEntity;
import com.sj.im.service.friendship.web.req.*;
import com.sj.im.service.friendship.web.resp.CheckFriendShipResp;
import com.sj.im.service.friendship.web.resp.ImportFriendShipResp;
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
@Api(tags = "V1/好友关系控制器")
@RestController
@RequestMapping("v1/friendship")
public class ImFriendShipController {
    @Resource
    private ImFriendShipService imFriendShipService;

    @ApiOperation("导入朋友关系")
    @PostMapping("/importFriendShip")
    public ResponseVO<ImportFriendShipResp> importFriendShip(@RequestBody @Validated ImportFriendShipReq req) {
        ImportFriendShipResp importFriendShipResp = imFriendShipService.importFriendShip(req);
        return ResponseVO.successResponse(importFriendShipResp);
    }

    @ApiOperation("添加朋友关系")
    @PostMapping("/addFriendShip")
    public ResponseVO<String> addFriendShip(@RequestBody @Validated AddFriendReq req) {
        imFriendShipService.addFriend(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("更新好友关系")
    @PutMapping("/updateFriendShip")
    public ResponseVO<String> updateFriendShip(@RequestBody @Validated UpdateFriendReq req) {
        imFriendShipService.updateFriend(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("删除好友关系")
    @DeleteMapping("/deleteFriendShip")
    public ResponseVO<String> deleteFriendShip(@RequestBody @Validated DeleteFriendReq req) {
        imFriendShipService.deleteFriend(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("删除所有好友关系")
    @DeleteMapping("/deleteAllFriendShip")
    public ResponseVO<String> deleteAllFriendShip(@RequestBody @Validated DeleteFriendReq req) {
        imFriendShipService.deleteAllFriend(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("拉取指定好友信息")
    @GetMapping("/getRelation")
    public ResponseVO<ImFriendShipEntity> getRelation(@RequestBody @Validated GetRelationReq req) {
        ImFriendShipEntity relation = imFriendShipService.getRelation(req);
        return ResponseVO.successResponse(relation);
    }

    @ApiOperation("拉取指定用户所有好友信息")
    @GetMapping("/getAllFriendShip")
    public ResponseVO<List<ImFriendShipEntity>> getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req) {
        List<ImFriendShipEntity> allFriend = imFriendShipService.getAllFriend(req);
        return ResponseVO.successResponse(allFriend);
    }

    @ApiOperation("校验好友关系")
    @GetMapping("/checkFriendShip")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendShip(@RequestBody @Validated CheckFriendShipReq req) {
        List<CheckFriendShipResp> shipResp = imFriendShipService.checkFriendShip(req);
        return ResponseVO.successResponse(shipResp);
    }

    @ApiOperation("拉入黑名单")
    @PutMapping("/addFriendSipBlack")
    public ResponseVO<String> addFriendSipBlack(@RequestBody @Validated AddFriendShipBlackReq req) {
        imFriendShipService.addBlack(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("拉出黑名单")
    @PutMapping("/deleteFriendSipBlack")
    public ResponseVO<String> deleteFriendSipBlack(@RequestBody @Validated DeleteBlackReq req) {
        imFriendShipService.deleteBlack(req);
        return ResponseVO.successResponse();
    }

    @ApiOperation("校验黑名单")
    @GetMapping("/checkFriendBlack")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendBlack(@RequestBody @Validated CheckFriendShipReq req) {
        List<CheckFriendShipResp> resp = imFriendShipService.checkFriendBlack(req);
        return ResponseVO.successResponse(resp);
    }

    @ApiOperation("同步好友列表")
    @PostMapping("/syncFriendShipList")
    public ResponseVO<SyncResp<ImFriendShipEntity>> syncFriendShipList(@RequestBody @Validated SyncReq req) {
        SyncResp<ImFriendShipEntity> syncResp = imFriendShipService.syncFriendShipList(req);
        return ResponseVO.successResponse(syncResp);
    }
}
