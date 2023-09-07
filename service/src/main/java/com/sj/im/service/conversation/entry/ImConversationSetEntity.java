/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.entry;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_conversation_set")
public class ImConversationSetEntity {

    /**
     * 会话id 0_fromId_toId
     */
    @MppMultiId
    private String conversationId;

    /**
     * 应用id
     */
    @MppMultiId
    private Integer appId;

    /**
     * 会话类型
     */
    private Integer conversationType;

    /**
     * 发起方id
     */
    private String fromId;

    /**
     * 接收方id
     */
    private String toId;

    /**
     * 是否静音
     */
    private int isMute;

    /**
     * 是否置顶
     */
    private int isTop;

    /**
     * 当前会话的最新消息序列号
     */
    private Long sequence;

    /**
     * 已读消息序列号
     */
    private Long readSequence;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}