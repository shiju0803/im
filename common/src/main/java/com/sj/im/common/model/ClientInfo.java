/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 客户端信息类
 */
@Data
@NoArgsConstructor
public class ClientInfo {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * 手机设备的 IMEI 号
     */
    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }
}