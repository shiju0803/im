/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("删除好友分组成员请求")
public class DeleteFriendShipGroupMemberReq extends RequestBase {
    @NotBlank(message = "fromId不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String fromId;

    @NotBlank(message = "分组名称不能为空")
    @ApiModelProperty(value = "分组名称", required = true)
    private String groupName;

    @NotEmpty(message = "请选择用户")
    @ApiModelProperty(value = "好友id集合", required = true)
    private List<String> toIds;
}