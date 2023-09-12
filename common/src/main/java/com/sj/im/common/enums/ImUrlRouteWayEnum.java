/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums;

import lombok.Getter;

/**
 * 服务器地址获取策略枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum ImUrlRouteWayEnum {

    /**
     * 随机
     */
    RANDOM(1, "com.sj.im.common.route.algorithm.random.RandomHandle"),

    /**
     * 轮循
     */
    LOOP(2, "com.sj.im.common.route.algorithm.loop.LoopHandle"),

    /**
     * HASH
     */
    HASH(3, "com.sj.im.common.route.algorithm.consistenthash.ConsistentHashHandle"),

    ;


    private final int code;
    private final String clazz;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     */
    public static ImUrlRouteWayEnum getHandler(int ordinal) {
        for (int i = 0; i < ImUrlRouteWayEnum.values().length; i++) {
            if (ImUrlRouteWayEnum.values()[i].getCode() == ordinal) {
                return ImUrlRouteWayEnum.values()[i];
            }
        }
        return null;
    }

    ImUrlRouteWayEnum(int code, String clazz) {
        this.code = code;
        this.clazz = clazz;
    }
}