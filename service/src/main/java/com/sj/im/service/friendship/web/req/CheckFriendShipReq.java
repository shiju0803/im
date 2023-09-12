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
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("检查好友关系请求")
public class CheckFriendShipReq extends RequestBase {
    @NotBlank(message = "fromId不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String fromId;

    @NotEmpty(message = "请选择要检查的好友")
    @ApiModelProperty(value = "好友id集合", required = true)
    private List<String> toIds;

    @NotNull(message = "请选择检查类型")
    @ApiModelProperty(value = "检查类型", required = true, example = "1单方校验 2双方校验")
    private Integer checkType;
}