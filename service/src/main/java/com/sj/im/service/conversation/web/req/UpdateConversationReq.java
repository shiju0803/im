/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "更新会话请求参数")
public class UpdateConversationReq extends RequestBase {

    @ApiModelProperty(value = "会话id", required = true)
    private String conversationId;

    @ApiModelProperty(value = "是否静音，0表示不静音，1表示静音", example = "0")
    private Integer isMute;

    @ApiModelProperty(value = "是否置顶，0表示不置顶，1表示置顶", example = "0")
    private Integer isTop;

    @ApiModelProperty(value = "发送方id", required = true)
    private String fromId;
}

