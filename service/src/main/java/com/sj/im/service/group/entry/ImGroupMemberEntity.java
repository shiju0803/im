/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.entry;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_group_member")
public class ImGroupMemberEntity {

    @TableId(value = "group_member_id", type = IdType.AUTO)
    private Long groupMemberId;

    private Integer appId;

    private String groupId;

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 群成员类型，0 普通成员, 1 管理员, 2 群主， 3 禁言，4 已经移除的成员
     */
    private Integer role;

    /**
     * 禁言到期时间
     */
    private Long speakDate;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 离开时间
     */
    private Date leaveTime;

    /**
     * 进入方式
     */
    private String joinType;

    /**
     * 拓展参数
     */
    private String extra;

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