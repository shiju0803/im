/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.consistenthash;

import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TreeMapConsistentHash类继承了AbstractConsistentHash抽象类，实现了基于TreeMap的一致性哈希算法
 *
 * @author ShiJu
 * @version 1.0
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    // 使用TreeMap存储节点和对应的值，键为节点哈希值，值为节点对应的服务器
    private final TreeMap<Long, String> treeMap = new TreeMap<>();

    // 虚拟节点的数量
    private static final int NODE_SIZE = 2;

    /**
     * 添加节点到一致性哈希环中。
     *
     * @param key   节点的哈希值
     * @param value 节点对应的服务器
     */
    @Override
    protected void add(long key, String value) {
        // 为每个服务器添加虚拟节点
        for (int i = 0; i < NODE_SIZE; i++) {
            treeMap.put(super.hash("node" + key + "i"), value);
        }
        // 添加真实节点
        treeMap.put(key, value);
    }

    /**
     * 获取哈希环中第一个满足条件的节点对应的服务器。
     *
     * @param value 用于计算哈希值的字符串
     * @return 符合条件的节点对应的服务器
     */
    @Override
    protected String getFirstNodeValue(String value) {

        Long hash = super.hash(value);
        // 获取哈希环中大于等于给定哈希值的部分
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        // 如果存在满足条件的节点，返回第一个节点对应的服务器
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }

        // 如果哈希环为空，抛出异常
        if (treeMap.isEmpty()) {
            throw new BusinessException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        // 如果不存在满足条件的节点，返回哈希环中的第一个节点对应的服务器
        return treeMap.firstEntry().getValue();
    }

    /**
     * 在进行一致性哈希处理之前，清空TreeMap。
     */
    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}
