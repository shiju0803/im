/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.entry;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("im_friendship_request")
public class ImFriendShipRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer appId;

    private String fromId;

    private String toId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已读 0未读 1已读
     */
    private Integer readStatus;

    /**
     * 好友来源
     */
    private String addSource;

    /**
     * 好友申请附带信息
     */
    private String addWording;

    /**
     * 审批状态 0待审批 1同意 2拒绝
     */
    private Integer approveStatus;

    /**
     * 序列号
     */
    private Long sequence;

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