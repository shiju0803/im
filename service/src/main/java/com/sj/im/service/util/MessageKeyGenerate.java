/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 消息key生成
 *
 * @author ShiJu
 * @version 1.0
 */
public class MessageKeyGenerate {

    //标识从2023.1.1开始
    private static final long T202301010000 = 1672502400000L;
    private static final int ROTATE_ID_WIDTH = 15;
    private static final int ROTATE_ID_MASK = 32767;
    private static final int NODE_ID_WIDTH = 6;
    private static final int NODE_ID_MASK = 63;
    private static volatile int rotateId = 0;
    private static volatile long timeId = 0;
    AtomicReference<Thread> owner = new AtomicReference<>();
    private int nodeId = 0;

    public static int getSharding(long mid) {

        Calendar calendar = Calendar.getInstance();

        mid >>= NODE_ID_WIDTH;
        mid >>= ROTATE_ID_WIDTH;

        calendar.setTime(new Date(T202301010000 + mid));

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        year %= 3;

        return (year * 12 + month);
    }

    public static long getMsgIdFromTimestamp(long timestamp) {
        long id = timestamp - T202301010000;

        id <<= ROTATE_ID_WIDTH;
        id <<= NODE_ID_WIDTH;

        return id;
    }

    public static void main(String[] args) throws Exception {

        MessageKeyGenerate messageKeyGenerate = new MessageKeyGenerate();
        for (int i = 0; i < 10; i++) {
            long l = messageKeyGenerate.generateId();
            System.out.println(l);
        }

        //        long l = messageKeyGenerate.generateId();
        //        System.out.println("生成了一个id：" + l);
        //        int sharding = getSharding(l);
        //        System.out.println("解密id的时间戳：" + sharding);

        //im_message_history_12


        //10000  10001
        //0      1

        long msgIdFromTimestamp = getMsgIdFromTimestamp(1734529845000L);
        int sharding = getSharding(msgIdFromTimestamp);
        System.out.println(sharding);
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * ID = timestamp(43) + nodeId(6) + rotateId(15)
     */
    public synchronized long generateId() throws Exception {

        this.lock();

        rotateId = rotateId + 1;

        long id = System.currentTimeMillis() - T202301010000;

        //不同毫秒数生成的id要重置timeId和自选次数
        if (id > timeId) {
            timeId = id;
            rotateId = 1;
        } else {
            if (id == timeId) {
                //表示是同一毫秒的请求
                if (rotateId == ROTATE_ID_MASK) {
                    //一毫秒只能发送32768到这里表示当前毫秒数已经超过了
                    while (id <= timeId) {
                        //重新给id赋值
                        id = System.currentTimeMillis() - T202301010000;
                    }
                    this.unLock();
                    return generateId();
                }
            }
        }

        id <<= NODE_ID_WIDTH;
        id += (nodeId & NODE_ID_MASK);


        id <<= ROTATE_ID_WIDTH;
        id += rotateId;

        this.unLock();
        return id;
    }

    public void lock() {
        Thread cur = Thread.currentThread();
        //lock函数将owner设置为当前线程，并且预测原来的值为空。
        // unlock函数将owner设置为null，并且预测值为当前线程。
        // 当有第二个线程调用lock操作时由于owner值不为空，导致循环
        //一直被执行，直至第一个线程调用unlock函数将owner设置为null，第二个线程才能进入临界区。
        while (!owner.compareAndSet(null, cur)) {
        }
    }

    public void unLock() {
        Thread cur = Thread.currentThread();
        owner.compareAndSet(cur, null);
    }
}
