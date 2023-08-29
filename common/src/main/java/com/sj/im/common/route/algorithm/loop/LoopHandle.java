/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.loop;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.enums.UserErrorCode;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 负载均衡策略-轮询模式
 */
public class LoopHandle implements RouteHandle {

    private static final AtomicLong index = new AtomicLong();
    @Override
    public String routeServer(List<String> values, String key) {
        if (ObjectUtil.isEmpty(values)) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        int size = values.size();
        long l = index.incrementAndGet() % size;
        if (l < 0) {
            l = 0L;
        }
        return values.get((int) l);
    }
}
