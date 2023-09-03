/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.route.algorithm.consistenthash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用于实现一致性哈希算法的抽象类
 */
public abstract class AbstractConsistentHash {

    /**
     * 将具有指定键和值的节点添加到一致性哈希中。
     *
     * @param key   节点的键。
     * @param value 节点的值。
     */
    protected abstract void add(long key, String value);

    /**
     * 在将节点添加到一致性哈希后，可选的方法来对节点进行排序。
     */
    protected void sort() {
    }

    /**
     * 获取一致性哈希中与指定值匹配的第一个节点值。
     *
     * @param value 要搜索的值。
     * @return 与指定值匹配的第一个节点值。
     */
    protected abstract String getFirstNodeValue(String value);

    /**
     * 在运行一致性哈希之前执行任何必要的预处理的可选方法。
     */
    protected abstract void processBefore();

    /**
     * 使用给定的值和键处理一致性哈希。
     *
     * @param values 要添加到一致性哈希的值列表。
     * @param key    要在一致性哈希中搜索的键。
     * @return 与指定键匹配的第一个节点值。
     */
    public synchronized String process(List<String> values, String key) {
        processBefore();
        for (String value : values) {
            add(hash(value), value);
        }
        sort();
        return getFirstNodeValue(key);
    }

    /**
     * 使用 MD5 算法对给定值进行哈希。
     *
     * @param value 要哈希的值。
     * @return 作为 long 类型的哈希值。
     */
    public Long hash(String value) {
        // 创建 MD5 消息摘要实例
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        // 重置 MD5 消息摘要
        md5.reset();
        // 将字符串转换为 UTF-8 编码的字节数组
        byte[] keyBytes = value.getBytes(StandardCharsets.UTF_8);

        // 使用字节数组更新 MD5 消息摘要
        md5.update(keyBytes);
        // 计算 MD5 消息摘要
        byte[] digest = md5.digest();

        // 计算哈希码，并将其截断为 32 位
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        // 返回截断后的 32 位哈希码
        return hashCode & 0xffffffffL;
    }
}
