/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("设置用户客户端在线状态的请求")
public class SetUserCustomerStatusReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    @NotBlank(message = "用户客户端id不能为空")
    @ApiModelProperty(value = "用户客户端id", required = true)
    private String customText;

    @NotNull(message = "用户客户端状态不能为空")
    @ApiModelProperty(value = "用户客户端id", required = true)
    private Integer customStatus;
}
