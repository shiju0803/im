/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户信息的请求")
public class UserBatchReq extends RequestBase {

    @ApiModelProperty(value = "用户id集合", required = true)
    private List<String> userIds;
}