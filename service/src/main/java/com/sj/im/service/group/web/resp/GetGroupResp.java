/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.resp;

import com.sj.im.service.group.web.req.GroupMemberDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取群信息接口响应类
 */
@Data
@ApiModel("获取群信息接口响应类")
public class GetGroupResp {
    @ApiModelProperty(value = "群id")
    private String groupId;

    @ApiModelProperty(value = "appId")
    private Integer appId;

    @ApiModelProperty(value = "群主id")
    private String ownerId;

    @ApiModelProperty(value = "群类型", example = "1私有群（类似微信） 2公开群(类似qq）")
    private Integer groupType;

    @ApiModelProperty(value = "群名称")
    private String groupName;

    @ApiModelProperty(value = "是否全员禁言", example = "0 不禁言；1 全员禁言")
    private Integer mute;

    @ApiModelProperty(value = "加入群权限", example = "0 所有人可以加入；1 群成员可以拉人；2 群管理员或群组可以拉人")
    private Integer applyJoinType;

    @ApiModelProperty(value = "是否禁止私聊，0 允许群成员发起私聊；1 不允许群成员发起私聊")
    private Integer privateChat;

    @ApiModelProperty(value = "群简介")
    private String introduction;

    @ApiModelProperty(value = "群公告")
    private String notification;

    @ApiModelProperty(value = "群头像")
    private String photo;

    @ApiModelProperty(value = "群成员上限")
    private Integer maxMemberCount;

    @ApiModelProperty(value = "群状态", example = "0正常 1解散")
    private Integer status;

    @ApiModelProperty(value = "群成员")
    private List<GroupMemberDto> memberList;
}