/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.model.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 添加群成员请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("添加群成员请求")
public class AddGroupMemberReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @NotEmpty(message = "群成员不能为空")
    @ApiModelProperty(value = "群成员")
    private List<GroupMemberDto> members;
}