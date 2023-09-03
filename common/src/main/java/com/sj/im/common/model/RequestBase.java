/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 公共基础请求参数类
 */
@Data
@ApiModel("请求基础信息")
public class RequestBase {

    @ApiModelProperty(value = "应用id", example = "123")
    private Integer appId;

    @ApiModelProperty(value = "操作者", example = "1")
    private String operator;

    @ApiModelProperty(value = "客户端类型", example = "1")
    private Integer clientType;

    @ApiModelProperty(value = "设备IMEI号", example = "123456789")
    private String imei;
}
