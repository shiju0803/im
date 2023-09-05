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
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("添加好友请求")
public class AddFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    @ApiModelProperty(value = "用户id", example = "1")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    @ApiModelProperty(value = "好友信息")
    private FriendDto toItem;
}