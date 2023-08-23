/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.model.req;

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
 * @description: 群成员禁言请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("群成员禁言请求")
public class SpeakMemberReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    @ApiModelProperty(value = "成员id")
    private String memberId;

    @NotNull(message = "禁言时间不能为空")
    @ApiModelProperty(value = "禁言时间，单位分钟")
    private Integer speakDate;
}