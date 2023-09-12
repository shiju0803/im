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

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SpeakMemberReq", description = "禁言群成员请求参数")
public class SpeakMemberReq extends RequestBase {
    @ApiModelProperty(value = "群id", required = true, example = "123456")
    @NotBlank(message = "群id不能为空")
    private String groupId;

    @ApiModelProperty(value = "成员id", required = true, example = "7890")
    @NotBlank(message = "memberId不能为空")
    private String memberId;

    @ApiModelProperty(value = "禁言时间，单位毫秒", required = true, example = "60000")
    @NotNull(message = "禁言时间不能为空")
    private Long speakDate;
}