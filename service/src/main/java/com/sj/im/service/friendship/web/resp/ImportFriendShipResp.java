/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 导入好友关系接口响应类
 */
@Data
@ApiModel("导入好友关系接口响应类")
public class ImportFriendShipResp {
    @ApiModelProperty(value = "导入成功的好友id集合")
    private List<String> successId;

    @ApiModelProperty(value = "导入失败的好友id集合")
    private List<String> errorId;
}
