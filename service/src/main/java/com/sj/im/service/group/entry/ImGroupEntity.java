/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.entry;

import com.baomidou.mybatisplus.annotation.FieldFill;
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

    /**
     * 群组ID
     */
    @TableId(value = "group_id")
    private String groupId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 群主id
     */
    private String ownerId;

    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    private Integer groupType;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 是否全员禁言，0 不禁言；1 全员禁言。
     */
    private Integer mute;

    /**
     * 申请加群选项包括如下几种：
     * 0 表示禁止任何人申请加入
     * 1 表示需要群主或管理员审批
     * 2 表示允许无需审批自由加入群组
     */
    private Integer applyJoinType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 群头像
     */
    private String photo;

    /**
     * 群成员上限
     */
    private Integer maxMemberCount;

    /**
     * 群状态 0正常 1解散
     */
    private Integer status;

    /**
     * 是否禁止私聊，0 允许群成员发起私聊；1 不允许群成员发起私聊
     */
    private Integer privateChat;

    /**
     * 序列号
     */
    private Long sequence;

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