/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

/**
 * 修改好友通知报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class UpdateFriendPack {

    private String fromId;

    private String toId;

    private String remark;

    private Long sequence;
}