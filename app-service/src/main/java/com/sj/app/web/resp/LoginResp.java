/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户登录接口响应类")
public class LoginResp {

    @ApiModelProperty(value = "im的token")
    private String imUserSign;

    @ApiModelProperty(value = "自己的token")
    private String userSign;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "应用id")
    private Integer appId;
}
