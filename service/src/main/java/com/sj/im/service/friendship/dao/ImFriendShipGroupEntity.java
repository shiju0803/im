/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("im_friendship_group")
public class ImFriendShipGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组编号
     */
    @TableId(value = "group_id",type = IdType.AUTO)
    private Long groupId;

    @TableField(value = "from_id")
    private String fromId;

    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 分组名称
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 序列号
     */
    @TableField(value = "sequence")
    private Long sequence;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    @TableField(value = "del_flag")
    private int delFlag;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Long updateTime;
}