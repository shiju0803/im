/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 公共基础请求参数类
 */
@Data
@ApiModel("基础请求参数类")
public class RequestBase {
    @NotNull(message = "appId不能为空")
    @ApiModelProperty(value = "appId", required = true)
    private Integer appId;

    @ApiModelProperty(value = "operator")
    private String operator;

    @ApiModelProperty(value = "clientType")
    private Integer clientType;

    @ApiModelProperty(value = "imei")
    private String imei;
}
