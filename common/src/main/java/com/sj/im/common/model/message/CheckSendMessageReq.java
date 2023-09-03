/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "检查发送消息请求参数")
public class CheckSendMessageReq {

    @ApiModelProperty(value = "发送方用户 ID", required = true)
    private String fromId;

    @ApiModelProperty(value = "接收方用户 ID", required = true)
    private String toId;

    @ApiModelProperty(value = "应用程序 ID", required = true)
    private Integer appId;

    @ApiModelProperty(value = "消息指令类型", required = true)
    private Integer command;
}