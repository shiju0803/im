/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.callback;

import lombok.Data;

@Data
public class AddFriendBlackAfterCallbackDto {

    private String fromId;

    private String toId;
}