/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.web.rep;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel("发送消息请求参数")
@EqualsAndHashCode(callSuper = true)
public class SendMessageReq extends RequestBase {

    @ApiModelProperty(value = "消息ID", required = true)
    private String messageId;

    @ApiModelProperty(value = "发送方ID", required = true)
    private String fromId;

    @ApiModelProperty(value = "接收方ID", required = true)
    private String toId;

    @ApiModelProperty(value = "消息随机数", required = true)
    private int messageRandom;

    @ApiModelProperty(value = "消息发送时间戳", required = true)
    private long messageTime;

    @ApiModelProperty(value = "消息内容", required = true)
    private String messageBody;

    @ApiModelProperty(value = "消息计数模式，0表示需要计数，1表示不需要计数", required = true)
    private int badgeMode;

    @ApiModelProperty(value = "消息生命周期")
    private Long messageLifeTime;
}
