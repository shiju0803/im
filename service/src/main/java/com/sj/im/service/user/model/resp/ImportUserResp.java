/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @description: 导入用户接口响应类
 * @author ShiJu
 * @version 1.0
 */
@Data
public class ImportUserResp {
    private List<String> successId;

    private List<String> errorId;
}
