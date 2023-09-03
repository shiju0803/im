/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 同步数据请求参数类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("基础请求参数类")
public class SyncReq extends RequestBase {

    @NotNull(message = "lastSequence不能为空")
    @ApiModelProperty("客户端最大seq")
    private Long lastSequence;

    @NotNull(message = "maxLimit不能为空")
    @ApiModelProperty("一次拉取多少")
    private Integer maxLimit;
}