/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.web.resp;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 发送消息响应类
 */
@Data
public class SendMessageResp {

    private Long messageKey;

    private Long messageTime;
}