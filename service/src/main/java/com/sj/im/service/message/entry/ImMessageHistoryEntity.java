/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.entry;

import com.baomidou.mybatisplus.annotation.*;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_message_history")
public class ImMessageHistoryEntity {

    @MppMultiId
    private Integer appId;

    private String fromId;

    private String toId;

    @MppMultiId
    private String ownerId;

    @MppMultiId
    private Long messageKey;

    private Long sequence;

    private String messageRandom;

    private Long messageTime;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
