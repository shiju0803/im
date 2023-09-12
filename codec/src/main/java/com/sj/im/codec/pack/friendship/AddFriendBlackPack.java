/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

/**
 * 用户添加黑名单以后tcp通知数据包
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class AddFriendBlackPack {
    private String fromId;

    private String toId;

    private Long sequence;
}