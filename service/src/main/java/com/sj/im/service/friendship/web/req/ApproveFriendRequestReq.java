/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 审批好友申请请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("审批好友申请请求")
public class ApproveFriendRequestReq extends RequestBase {

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "审批状态", required = true, example = "1同意 2拒绝")
    private Integer status;
}
