/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.enums.exception.BaseExceptionEnum;
import com.sj.im.common.enums.exception.GateWayErrorCode;
import com.sj.im.common.model.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 网关拦截器
 */
@Slf4j
@Component
public class GateWayInterceptor implements HandlerInterceptor {
    @Resource
    private IdentityCheck identityCheck;

    @Value("${spring.debug}")
    private String debug;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        String dev = request.getParameter("debug");
        if (ObjectUtil.equal(debug, dev)) {
            return true;
        }

        // 获取appId 操作人 userSign
        String appId = request.getParameter("appId");
        if (CharSequenceUtil.isBlank(appId)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.APPID_NOT_EXIST), response);
            return false;
        }

        String identifier = request.getParameter("identifier");
        if (CharSequenceUtil.isBlank(identifier)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.OPERATOR_NOT_EXIST), response);
            return false;
        }

        String userSign = request.getParameter("userSign");
        if (CharSequenceUtil.isBlank(userSign)) {
            resp(ResponseVO.errorResponse(GateWayErrorCode.USER_SIGN_NOT_EXIST), response);
            return false;
        }

        // 签名和操作人和appId是否匹配
        BaseExceptionEnum baseExceptionEnum = identityCheck.checkUserSign(identifier, appId, userSign);
        if (ObjectUtil.notEqual(baseExceptionEnum, BaseErrorCode.SUCCESS)) {
            resp(ResponseVO.errorResponse(baseExceptionEnum), response);
            return false;
        }

        return true;
    }

    private void resp(ResponseVO<Object> responseVO, HttpServletResponse response) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");

        try {
            String resp = JSONUtil.toJsonStr(responseVO);
            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e) {
            log.error("网关拦截器响应异常: {}", e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
