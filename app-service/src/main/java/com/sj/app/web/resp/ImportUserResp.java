/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("导入用户接口响应类")
public class ImportUserResp {

    @ApiModelProperty(value = "导入成功的用户id集合")
    private List<String> successId;

    @ApiModelProperty(value = "导入失败的用户id集合")
    private List<String> errorId;
}
