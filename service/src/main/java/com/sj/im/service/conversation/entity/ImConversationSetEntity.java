/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_conversation_set")
public class ImConversationSetEntity {

    @ApiModelProperty(value = "会话id 0_fromId_toId")
    private String conversationId;

    @ApiModelProperty(value = "会话类型")
    private Integer conversationType;

    @ApiModelProperty(value = "发起方id")
    private String fromId;

    @ApiModelProperty(value = "接收方id")
    private String toId;

    @ApiModelProperty(value = "是否静音")
    private int isMute;

    @ApiModelProperty(value = "是否置顶")
    private int isTop;

    @ApiModelProperty(value = "当前会话的最新消息序列号")
    private Long sequence;

    @ApiModelProperty(value = "已读消息序列号")
    private Long readSequence;

    @ApiModelProperty(value = "应用id")
    private Integer appId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}