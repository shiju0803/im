/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import lombok.Data;

@Data
public class UserClientDto {

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 设备imei号
     */
    private String imei;
}