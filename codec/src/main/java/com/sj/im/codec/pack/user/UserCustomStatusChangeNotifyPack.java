/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.user;

import lombok.Data;

/**
 * 用户客户端状态变化通知报文
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class UserCustomStatusChangeNotifyPack {

    private String customText;

    private Integer customStatus;

    private String userId;
}