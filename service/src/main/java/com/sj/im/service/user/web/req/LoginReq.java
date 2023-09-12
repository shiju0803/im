/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("登录请求接口入参")
public class LoginReq extends RequestBase {

    @NotNull(message = "用户id不能位空")
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    @ApiModelProperty(value = "连接类型", required = true)
    private Integer clientType;
}