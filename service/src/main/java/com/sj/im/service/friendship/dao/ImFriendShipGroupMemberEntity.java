/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("im_friendship_group_member")
public class ImFriendShipGroupMemberEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组编号
     */
    @TableField(value = "group_id")
    private Long groupId;

    /**
     * 好友id
     */
    @TableField(value = "to_id")
    private String toId;
}
