/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户网络信息
 */
@Data
public class UserSession {
    // 用户Id
    private String userId;

    // 应用id
    private Integer appId;

    // 端的标识
    private Integer clientType;

    // sdk 版本号
    private Integer version;

    // 连接状态 1在线 2离线
    private Integer connectState;

    // brokerId
    private Integer brokerId;

    // brokerHost
    private String brokerHost;

    private String imei;
}