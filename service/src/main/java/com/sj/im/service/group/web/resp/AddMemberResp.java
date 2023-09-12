/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加群成员接口响应类")
public class AddMemberResp {
    @ApiModelProperty(value = "成员id")
    private String memberId;

    @ApiModelProperty(value = "加人结果", example = "0 为成功；1 为失败；2 为已经是群成员")
    private Integer result;

    @ApiModelProperty(value = "结果信息")
    private String resultMessage;
}