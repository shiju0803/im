/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "订阅用户在线状态请求")
public class SubscribeUserOnlineStatusReq extends RequestBase {

    @NotEmpty(message = "请选择要订阅的用户")
    @ApiModelProperty(value = "订阅的用户ID列表")
    private List<String> subUserId;

    @ApiModelProperty(value = "订阅时间")
    private Long subTime;
}
