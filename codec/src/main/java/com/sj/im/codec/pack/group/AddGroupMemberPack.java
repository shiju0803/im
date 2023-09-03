/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群内添加群成员通知报文
 */
@Data
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;
}