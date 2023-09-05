/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.resp;

import com.sj.im.service.group.entry.ImGroupEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取加入的群组响应")
public class GetJoinedGroupResp {

    @ApiModelProperty("总数")
    private Long totalCount;

    @ApiModelProperty("群组列表")
    private List<ImGroupEntity> groupList;
}