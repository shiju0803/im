/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 已读好友申请通知报文
 */
@Data
public class ReadAllFriendRequestPack {

    private String fromId;

    private Long sequence;
}