/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.req;

import com.sj.im.common.model.RequestBase;
import com.sj.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @description: 导入用户接口入参
 * @author ShiJu
 * @version 1.0
 */
@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;
}
