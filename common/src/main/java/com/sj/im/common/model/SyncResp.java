/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 同步数据响应类
 */
@Data
@ApiModel(description = "同步响应")
public class SyncResp<T> {

    @ApiModelProperty(value = "最大序列号")
    private Long maxSequence;

    @ApiModelProperty(value = "是否完成，true表示完成，false表示未完成")
    private boolean isCompleted;

    @ApiModelProperty(value = "数据列表")
    private List<T> dataList;
}