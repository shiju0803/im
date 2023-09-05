/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener.process;

/**
 * @author ShiJu
 * @version 1.0
 * @description: ProcessFactory类用于获取消息处理器
 */
public class ProcessFactory {

    /**
     * 默认的消息处理器，用于未找到对应消息处理器时使用
     */
    private static final BaseProcess defaultProcess;

    static {
        defaultProcess = new BaseProcess() {
            @Override
            public void processBefore() {
                // 默认消息处理器执行前不做任何处理
            }

            @Override
            public void processAfter() {
                // 默认消息处理器执行后不做任何处理
            }
        };
    }

    /**
     * 根据指令获取对应的消息处理器
     *
     * @param command 消息指令
     * @return 对应的消息处理器
     */
    public static BaseProcess getMessageProcess(Integer command) {
        // 未找到对应消息处理器时返回默认的消息处理器
        return defaultProcess;
    }

    private ProcessFactory() {}
}
