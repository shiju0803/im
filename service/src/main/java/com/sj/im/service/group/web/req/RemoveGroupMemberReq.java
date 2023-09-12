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

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("踢出群成员请求")
public class RemoveGroupMemberReq extends RequestBase {
    @NotBlank(message = "群id不能为空")
    @ApiModelProperty(value = "群id")
    private String groupId;

    @ApiModelProperty(value = "成员id")
    private String memberId;
}