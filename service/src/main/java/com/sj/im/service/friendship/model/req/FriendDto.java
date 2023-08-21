/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("好友实体类")
public class FriendDto {
    @ApiModelProperty(value = "添加的好友id", required = true)
    private String toId;

    @ApiModelProperty(value = "好友备注")
    private String remark;

    @ApiModelProperty(value = "添加来源")
    private String addSource;

    @ApiModelProperty(value = "拓展参数")
    private String extra;

    @ApiModelProperty(value = "职位")
    private String addWorking;
}