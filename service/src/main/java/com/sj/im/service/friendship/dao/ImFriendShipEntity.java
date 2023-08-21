/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.AutoMap;
import lombok.Data;

import java.util.Date;

@Data
@AutoMap
@TableName("im_friendship")
public class ImFriendShipEntity {

    @TableField(value = "app_id")
    private Integer appId;

    @TableField(value = "from_id")
    private String fromId;

    @TableField(value = "to_id")
    private String toId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 状态 1正常 2删除
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 状态 1正常 2拉黑
     */
    @TableField(value = "black")
    private Integer black;

    /**
     * 好友关系序列号
     */
    @TableField(value = "friend_sequence")
    private Long friendSequence;

    /**
     * 黑名单关系序列号
     */
    @TableField(value = "black_sequence")
    private Long blackSequence;

    /**
     * 好友来源
     */
    @TableField(value = "add_source")
    private String addSource;

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