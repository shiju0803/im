/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.service;

import com.sj.app.entry.AppUserEntity;
import com.sj.app.web.resp.ImportUserResp;
import com.sj.im.common.model.ResponseVO;

import java.util.List;

/**
 * 调用IM服务接口
 *
 * @author ShiJu
 * @version 1.0
 */
public interface ImService {
    /**
     * 导入IM用户的方法
     *
     * @param users 用户信息
     */
    ResponseVO<ImportUserResp> importUser(List<AppUserEntity> users);
}
