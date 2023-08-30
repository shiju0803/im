/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.callback;

import com.sj.im.common.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 回调接口
 */
@Slf4j
@RestController
public class CallbackController {
    @PostMapping("/callback")
    public ResponseVO<String> callback(@RequestBody String data, String command, Integer appId) {
        log.info("{}收到{}回调数据：{}", appId, command, data);
        return ResponseVO.successResponse();
    }
}
