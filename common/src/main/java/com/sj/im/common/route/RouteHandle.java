/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route;

import java.util.List;

/**
 * 服务器路由接口，定义了路由服务器的方法。
 *
 * @author ShiJu
 * @version 1.0
 */
public interface RouteHandle {

    /**
     * 根据给定的服务器列表和键，选择一个服务器进行路由。
     *
     * @param values 服务器列表
     * @param key    用于路由选择的键
     * @return 被选中的服务器
     */
    String routeServer(List<String> values, String key);
}
