/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import com.sj.im.common.ResponseVO;
import com.sj.im.common.config.AppConfig;
import com.sj.im.common.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 回调服务
 */
@Slf4j
@Component
public class CallbackHelper {
    @Resource
    private HttpRequestUtil httpRequestUtil;
    @Resource
    private AppConfig appConfig;

    public void callback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            httpRequestUtil.doPost(appConfig.getCallbackUrl(), Object.class, builderUrlParams(appId, callbackCommand), jsonBody, null);
        } catch (Exception e) {
            log.error("callback 回调{} : {}出现异常 ： {} ",callbackCommand , appId, e.getMessage());
        }
    }

    public ResponseVO beforeCallback(Integer appId, String callbackCommand, String jsonBody) {
        try {
            return httpRequestUtil.doPost(appConfig.getCallbackUrl(), ResponseVO.class, builderUrlParams(appId, callbackCommand), jsonBody, null);
        } catch (Exception e) {
            log.error("callback 之前回调{} : {}出现异常 ： {} ",callbackCommand , appId, e.getMessage());
        }
        return ResponseVO.successResponse();
    }

    public Map<String, Object> builderUrlParams(Integer appId, String command) {
        Map<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        map.put("command", command);
        return map;
    }
}
