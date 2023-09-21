/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.interceptor;

/**
 * 请求持有者类，用于在当前线程中存储请求信息
 *
 * @author ShiJu
 * @version 1.0
 */
public class RequestHolder {

    private static final ThreadLocal<Boolean> REQUEST_HOLDER = new ThreadLocal<>();

    private RequestHolder() {}

    /**
     * 设置请求信息
     *
     * @param isAdmin 是否是管理员
     */
    public static void set(Boolean isAdmin) {
        REQUEST_HOLDER.set(isAdmin);
    }

    /**
     * 获取请求信息
     *
     * @return 是否是管理员
     */
    public static Boolean get() {
        return REQUEST_HOLDER.get();
    }

    /**
     * 移除请求信息
     */
    public static void remove() {
        REQUEST_HOLDER.remove();
    }
}