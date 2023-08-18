/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.model.req.ImportUserReq;

/**
 * @description: 用户相关接口
 * @author ShiJu
 * @version 1.0
 */
public interface ImUserService {

    ResponseVO importUser(ImportUserReq req);
}
