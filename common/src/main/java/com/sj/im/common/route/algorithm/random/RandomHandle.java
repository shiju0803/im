/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.random;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.enums.UserErrorCode;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 负载均衡策略-随机模式
 */
public class RandomHandle implements RouteHandle {
    @Override
    public String routeServer(List<String> values, String key) {
        if (ObjectUtil.isEmpty(values)) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        int index = ThreadLocalRandom.current().nextInt(values.size());

        return values.get(index);
    }
}
