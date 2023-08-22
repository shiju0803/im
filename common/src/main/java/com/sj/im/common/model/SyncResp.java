/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 同步数据响应类
 */
@Data
public class SyncResp<T> {

    private Long maxSequence;

    private boolean isCompleted;

    private List<T> dataList;
}