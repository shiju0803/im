/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.random;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 负载均衡策略-随机模式
 */
public class RandomHandle implements RouteHandle {

    /**
     * 根据给定的服务器列表和键，使用随机算法选择一个服务器进行路由。
     *
     * @param values 服务器列表
     * @param key    用于路由选择的键（在本实现中不使用该参数）
     * @return 被选中的服务器
     */
    @Override
    public String routeServer(List<String> values, String key) {
        if (ObjectUtil.isEmpty(values)) {
            throw new BusinessException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        // 生成一个随机索引
        int index = ThreadLocalRandom.current().nextInt(values.size());
        return values.get(index);
    }
}
