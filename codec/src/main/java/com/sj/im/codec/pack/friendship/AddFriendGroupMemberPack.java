/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * 好友分组添加成员通知包
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class AddFriendGroupMemberPack {

    private String fromId;

    private String groupName;

    private List<String> toIds;

    /**
     * 序列号
     */
    private Long sequence;
}
