/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.model.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取群角色接口响应类
 */
@Data
@ApiModel("获取群角色接口响应类")
public class GetRoleInGroupResp {
    @ApiModelProperty(value = "群成员id")
    private Long groupMemberId;

    @ApiModelProperty(value = "成员id")
    private String memberId;

    @ApiModelProperty(value = "角色")
    private Integer role;

    @ApiModelProperty(value = "禁言到期时间")
    private Date speakDate;
}