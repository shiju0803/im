/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web.resp;

import com.sj.im.common.model.UserSession;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取用户在线状态的接口响应类
 */
@Data
@ApiModel("获取用户在线状态的接口响应类")
public class UserOnlineStatusResp {

    @ApiModelProperty(value = "用户网络信息集合")
    private List<UserSession> session;

    @ApiModelProperty(value = "用户在线状态")
    private String customText;

    @ApiModelProperty(value = "用户在线状态")
    private Integer customStatus;
}