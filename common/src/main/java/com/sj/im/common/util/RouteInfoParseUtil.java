/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.util;

import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.route.RouteInfo;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 路由信息解析工具类
 */
public class RouteInfoParseUtil {

    private RouteInfoParseUtil() {}

    /**
     * 解析包含路由信息的字符串，并返回一个 RouteInfo 对象。
     *
     * @param info 格式为 "host:port" 的包含路由信息的字符串。
     * @return 一个包含解析后的主机和端口的 RouteInfo 对象。
     * @throws BusinessException 如果输入字符串的格式不正确。
     */
    public static RouteInfo parse(String info) {
        try {
            // 使用冒号作为分隔符，将输入字符串分割成主机和端口
            String[] serverInfo = info.split(":");
            // 使用解析后的主机和端口创建一个 RouteInfo 对象
            return new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]));
        } catch (Exception e) {
            // 如果输入字符串的格式不正确，抛出 BusinessException
            throw new BusinessException(BaseErrorCode.PARAMETER_ERROR);
        }
    }
}
