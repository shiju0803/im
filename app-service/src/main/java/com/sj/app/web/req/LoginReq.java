/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web.req;

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

    @NotNull(message = "用户名不能位空")
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    @NotNull(message = "请选择登录方式")
    @ApiModelProperty(value = "登录方式", required = true)
    private Integer loginType;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "短信验证码")
    private String code;
}