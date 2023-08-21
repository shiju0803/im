/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取用户关系接口入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("获取用户关系接口入参")
public class UserSingleReq extends RequestBase {

    @ApiModelProperty(value = "用户id", required = true)
    private String userId;
}