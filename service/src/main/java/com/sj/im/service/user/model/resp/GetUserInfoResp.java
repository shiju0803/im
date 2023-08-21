/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.resp;

import com.sj.im.service.user.dao.ImUserDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取用户信息的接口响应类
 */
@Data
@ApiModel("获取用户信息的接口响应类")
public class GetUserInfoResp {

    @ApiModelProperty(value = "用户id集合")
    private List<ImUserDataEntity> userDataItem;

    @ApiModelProperty(value = "查询失败的用户id集合")
    private List<String> failUser;
}