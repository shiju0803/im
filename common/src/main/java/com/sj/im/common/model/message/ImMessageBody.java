/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "IM消息体")
public class ImMessageBody {

    @ApiModelProperty(value = "应用ID，用于区分不同的应用")
    private Integer appId;

    @ApiModelProperty(value = "消息体ID，唯一标识一条消息")
    private Long messageKey;

    @ApiModelProperty(value = "消息体内容")
    private String messageBody;

    @ApiModelProperty(value = "安全密钥，用于加密消息体")
    private String securityKey;

    @ApiModelProperty(value = "消息发送时间")
    private Long messageTime;

    @ApiModelProperty(value = "额外信息，如消息类型等")
    private String extra;

    @ApiModelProperty(value = "删除标记，0为未删除，1为已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "消息体创建时间")
    private Date createTime;

    @ApiModelProperty(value = "消息体更新时间")
    private Date updateTime;
}