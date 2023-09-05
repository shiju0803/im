/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组禁言请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("群组禁言请求")
public class MuteGroupReq extends RequestBase {

    @NotBlank(message = "groupId不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @NotNull(message = "mute不能为空")
    @ApiModelProperty(value = "是否全员禁言", example = "0 不禁言；1 全员禁言")
    private Integer mute;
}