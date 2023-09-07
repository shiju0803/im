/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.entry;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.util.Date;

@Data
@TableName("im_friendship")
public class ImFriendShipEntity {

    /**
     * 应用 ID
     */
    @MppMultiId
    private Integer appId;

    /**
     * 发起好友请求的用户 ID
     */
    @MppMultiId
    private String fromId;

    /**
     * 接收好友请求的用户 ID
     */
    @MppMultiId
    private String toId;

    /**
     * 好友备注
     */
    private String remark;

    /**
     * 好友关系状态：1-正常，2-删除
     */
    private Integer status;

    /**
     * 是否加入黑名单：1-正常，2-拉黑
     */
    @MppMultiId
    private Integer black;

    /**
     * 好友关系序列号
     */
    private Long friendSequence;

    /**
     * 黑名单关系序列号
     */
    private Long blackSequence;

    /**
     * 好友来源
     */
    private String addSource;

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