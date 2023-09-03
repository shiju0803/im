/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route;

import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: RouteInfo类表示服务器路由信息，包括服务器的IP地址和端口号
 */
@Data
public final class RouteInfo {

    /**
     * 服务器IP地址
     */
    private String ip;

    /**
     * 服务器端口号
     */
    private Integer port;

    /**
     * 构造一个RouteInfo对象，用于保存服务器的IP地址和端口号。
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口号
     */
    public RouteInfo(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
}