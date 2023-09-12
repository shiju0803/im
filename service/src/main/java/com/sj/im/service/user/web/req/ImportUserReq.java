/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.web.req;

import com.sj.im.common.model.RequestBase;
import com.sj.im.service.user.entry.ImUserDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("导入用户接口入参")
public class ImportUserReq extends RequestBase {

    @ApiModelProperty(value = "用户信息集合", required = true)
    private List<ImUserDataEntity> userData;
}
