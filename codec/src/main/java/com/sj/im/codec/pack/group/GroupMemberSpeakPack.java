/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.group;

import lombok.Data;

/**
 * 群成员禁言通知报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class GroupMemberSpeakPack {

    private String groupId;

    private String memberId;

    private Long speakDate;
}
