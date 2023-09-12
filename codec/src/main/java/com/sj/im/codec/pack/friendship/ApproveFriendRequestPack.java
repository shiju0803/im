/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.friendship;

import lombok.Data;

/**
 * 审批好友申请通知报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class ApproveFriendRequestPack {

    private Long id;

    //1同意 2拒绝
    private Integer status;

    private Long sequence;
}