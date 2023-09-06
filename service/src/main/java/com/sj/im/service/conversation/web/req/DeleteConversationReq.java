/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "删除会话请求参数")
public class DeleteConversationReq extends RequestBase {

    @ApiModelProperty(value = "会话id", required = true)
    @NotBlank(message = "会话id不能为空")
    private String conversationId;

    @ApiModelProperty(value = "发送方id", required = true)
    @NotBlank(message = "fromId不能为空")
    private String fromId;
}

