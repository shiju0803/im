/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.entry;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_message_body")
public class ImMessageBodyEntity {

    private Integer appId;

    @TableId(value = "message_key")
    private Long messageKey;

    private String messageBody;

    private String securityKey;

    private Long messageTime;

    private String extra;

    private Integer delFlag;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}