/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.codec.pack.user;

import com.sj.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

/**
 * 用户状态变更通知消息封装类
 *
 * @author ShiJu
 * @version 1.0
 */
@Data
public class UserStatusChangeNotifyPack {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户状态，例如在线、离线等
     */
    private Integer status;

    /**
     * 客户端会话列表
     */
    private List<UserSession> client;
}