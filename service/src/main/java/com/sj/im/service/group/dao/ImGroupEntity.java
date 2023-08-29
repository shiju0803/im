/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("im_group")
public class ImGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "group_id")
    private String groupId;

    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 群主id
     */
    @TableField(value = "owner_id")
    private String ownerId;

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    @TableField(value = "group_type")
    private Integer groupType;

    /**
     * 群名称
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 是否全员禁言，0 不禁言；1 全员禁言。
     */
    @TableField(value = "mute")
    private Integer mute;

    /**
     * 申请加群选项包括如下几种：
     * 0 表示禁止任何人申请加入
     * 1 表示需要群主或管理员审批
     * 2 表示允许无需审批自由加入群组
     */
    @TableField(value = "apply_join_type")
    private Integer applyJoinType;

    /**
     * 群简介
     */
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 群公告
     */
    @TableField(value = "notification")
    private String notification;

    /**
     * 群头像
     */
    @TableField(value = "photo")
    private String photo;

    /**
     * 群成员上限
     */
    @TableField(value = "max_member_count")
    private Integer maxMemberCount;

    /**
     * 群状态 0正常 1解散
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否禁止私聊，0 允许群成员发起私聊；1 不允许群成员发起私聊
     */
    @TableField(value = "private_chat")
    private Integer privateChat;

    /**
     * 序列号
     */
    @TableField(value = "sequence")
    private Long sequence;

    /**
     * 拓展参数
     */
    @TableField(value = "extra")
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