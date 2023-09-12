/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.constant;

/**
 * 公共常量
 *
 * @author ShiJu
 * @version 1.0
 */
public class TcpConstants {
    /**
     * channel绑定的userId Key
     */
    public static final String USERID = "userId";

    /**
     * channel绑定的appId
     */
    public static final String APPID = "appId";

    /**
     * 客户端类型
     */
    public static final String CLIENT_TYPE = "clientType";

    /**
     * 读时间
     */
    public static final String READTIME = "readTime";

    /**
     * 设备编号
     */
    public static final String IMEI = "imei";

    public static final String IM_CORE_ZK_ROOT = "/im-coreRoot";

    public static final String IM_CORE_ZK_ROOT_TCP = "/tcp";

    public static final String IM_CORE_ZK_ROOT_WEB = "/web";


    /**
     * 隐藏无参构造
     */
    private TcpConstants() {}
}
