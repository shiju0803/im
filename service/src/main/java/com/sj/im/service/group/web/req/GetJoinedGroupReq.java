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
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 获取用户加入的群组请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("获取用户加入的群组请求")
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "成员id")
    private String memberId;

    @ApiModelProperty(value = "群类型")
    private List<Integer> groupType;

    @ApiModelProperty(value = "单次拉取的群组数量，如果不填代表所有群组")
    private Integer limit;

    @ApiModelProperty(value = "第几页")
    private Integer offset;
}