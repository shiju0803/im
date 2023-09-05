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

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("删除好友关系请求")
public class DeleteFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String fromId;

    @NotBlank(message = "toId不能为空")
    @ApiModelProperty(value = "好友id", required = true)
    private String toId;
}