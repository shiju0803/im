/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户注册的请求")
public class RegisterReq extends RequestBase {

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    private String password;

    @NotNull(message = "请选择注册方式")
    @ApiModelProperty(value = "注册方式", example = "1-手机号注册 2-用户名")
    private Integer registerType;
}