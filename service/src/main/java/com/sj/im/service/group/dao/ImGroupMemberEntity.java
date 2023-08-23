/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("im_group_member")
public class ImGroupMemberEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "group_member_id", type = IdType.AUTO)
    private Long groupMemberId;

    @TableId(value = "app_id")
    private Integer appId;

    @TableId(value = "group_id")
    private String groupId;

    /**
     * 成员id
     */
    @TableId(value = "member_id")
    private String memberId;

    /**
     * 群成员类型，0 普通成员, 1 管理员, 2 群主， 3 禁言，4 已经移除的成员
     */
    @TableId(value = "role")
    private Integer role;

    /**
     * 禁言到期时间
     */
    @TableId(value = "speak_date")
    private Date speakDate;

    /**
     * 群昵称
     */
    @TableId(value = "alias")
    private String alias;

    /**
     * 加入时间
     */
    @TableId(value = "join_time")
    private Date joinTime;

    /**
     * 离开时间
     */
    @TableId(value = "leave_time")
    private Date leaveTime;

    /**
     * 进入方式
     */
    @TableId(value = "join_type")
    private String joinType;

    /**
     * 拓展参数
     */
    @TableId(value = "extra")
    private String extra;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}