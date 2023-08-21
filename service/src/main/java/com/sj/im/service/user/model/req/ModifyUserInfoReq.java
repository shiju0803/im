/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 修改用户信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("修改用户信息的请求")
public class ModifyUserInfoReq extends RequestBase {

    @NotEmpty(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String nickName;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "生日")
    private String birthDay;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "头像")
    private String photo;

    @ApiModelProperty(value = "性别")
    private String userSex;

    @ApiModelProperty(value = "个性签名")
    private String selfSignature;

    @ApiModelProperty(value = "加好友验证类型", example = "1-无需验证 2-需要验证")
    private Integer friendAllowType;

    @ApiModelProperty(value = "拓展参数")
    private String extra;
}