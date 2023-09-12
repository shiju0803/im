/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.consistenthash;

import com.sj.im.common.route.RouteHandle;

import java.util.List;

/**
 * 负载均衡策略-一致性哈希算法模式
 *
 * @author ShiJu
 * @version 1.0
 */
public class ConsistentHashHandle implements RouteHandle {

    // 使用TreeMap实现的一致性哈希算法
    private AbstractConsistentHash hash;

    /**
     * 设置一致性哈希算法实现
     *
     * @param hash 一致性哈希算法实现类
     */
    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    /**
     * 根据给定的key和服务器列表，使用一致性哈希算法选择一个服务器
     *
     * @param values 服务器列表
     * @param key    用于选择服务器的键
     * @return 选择的服务器
     */
    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values, key);
    }
}
