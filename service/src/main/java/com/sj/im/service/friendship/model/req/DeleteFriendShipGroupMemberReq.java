/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.model.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 删除好友分组成员请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("删除好友分组成员请求")
public class DeleteFriendShipGroupMemberReq extends RequestBase {
    @NotBlank(message = "fromId不能为空")
    @ApiModelProperty(value = "fromId", required = true)
    private String fromId;

    @NotBlank(message = "分组名称不能为空")
    @ApiModelProperty(value = "groupName", required = true)
    private String groupName;

    @NotEmpty(message = "请选择用户")
    @ApiModelProperty(value = "toIds", required = true)
    private List<String> toIds;
}