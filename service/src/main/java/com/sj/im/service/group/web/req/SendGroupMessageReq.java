/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel("群组消息")
@EqualsAndHashCode(callSuper = true)
public class SendGroupMessageReq extends RequestBase {

    @ApiModelProperty(value = "客户端传的messageId", required = true)
    private String messageId;

    @ApiModelProperty(value = "发送者 ID", required = true)
    private String fromId;

    @ApiModelProperty(value = "群组 ID", required = true)
    private String groupId;

    @ApiModelProperty(value = "消息随机数", required = true)
    private int messageRandom;

    @ApiModelProperty(value = "消息发送时间", required = true)
    private long messageTime;

    @ApiModelProperty(value = "消息内容", required = true)
    private String messageBody;

    @ApiModelProperty(value = "这个字段缺省或者为 0 表示需要计数，为 1 表示本条消息不需要计数，即右上角图标数字不增加", required = true)
    private int badgeMode;

    @ApiModelProperty(value = "消息过期时间")
    private Long messageLifeTime;
}