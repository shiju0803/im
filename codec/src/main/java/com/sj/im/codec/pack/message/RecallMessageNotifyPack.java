/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 聊天消息撤回回调
 */
@Data
@NoArgsConstructor
public class RecallMessageNotifyPack {

    private String fromId;

    private String toId;

    private Long messageKey;

    private Long messageSequence;
}