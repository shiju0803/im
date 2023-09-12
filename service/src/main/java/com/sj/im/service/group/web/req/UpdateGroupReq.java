/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("修改群请求")
public class UpdateGroupReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @ApiModelProperty(value = "群名称")
    private String groupName;

    @ApiModelProperty(value = "是否全员禁言", example = "0 不禁言；1 全员禁言")
    private Integer mute;

    @ApiModelProperty(value = "加入群权限", example = "0 所有人可以加入；1 群成员可以拉人；2 群管理员或群组可以拉人")
    private Integer applyJoinType;

    @ApiModelProperty(value = "群简介")
    private String introduction;

    @ApiModelProperty(value = "群公告")
    private String notification;

    @ApiModelProperty(value = "群头像")
    private String photo;

    @ApiModelProperty(value = "群成员上限")
    private Integer maxMemberCount;

    @ApiModelProperty(value = "拓展参数")
    private String extra;
}
