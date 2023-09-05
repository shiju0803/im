/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 修改群成员请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("修改群成员请求")
public class UpdateGroupMemberReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    @ApiModelProperty(value = "成员id")
    private String memberId;

    @ApiModelProperty(value = "群昵称")
    private String alias;

    @ApiModelProperty(value = "群角色")
    private Integer role;

    @ApiModelProperty(value = "拓展参数")
    private String extra;
}