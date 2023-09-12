/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * 删除好友分组成员通知报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class DeleteFriendGroupMemberPack {

    private String fromId;

    private String groupName;

    private List<String> toIds;

    /**
     * 序列号
     */
    private Long sequence;
}