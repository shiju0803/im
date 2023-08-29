/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.util;

import com.sj.im.common.enums.BaseErrorCode;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.route.RouteInfo;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 路由信息解析工具类
 */
public class RouteInfoParseUtil {

    public static RouteInfo parse(String info) {
        try {
            String[] serverInfo = info.split(":");
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]));
        } catch (Exception e) {
            throw new ApplicationException(BaseErrorCode.PARAMETER_ERROR);
        }
    }
}
