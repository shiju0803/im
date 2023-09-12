/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.exception;

import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @Order(4)
    public ResponseVO<Object> handleException(HttpServletRequest request, Exception e) {
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            return ResponseVO.errorResponse(ex.getCode(), ex.getMsg());
        } else if (e instanceof NoHandlerFoundException) {
            return ResponseVO.errorResponse(404, "找不到资源", request.getServletPath());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return ResponseVO.errorResponse(405, "method 方法不支持", request.getServletPath());
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return ResponseVO.errorResponse(415, "不支持媒体类型", request.getServletPath());
        } else if (e instanceof MethodArgumentNotValidException) {
            return ResponseVO.errorResponse(416, Objects.requireNonNull(((MethodArgumentNotValidException) e).getBindingResult().getFieldError()).getDefaultMessage(), request.getServletPath());
        }
        log.error("服务器内部错误: {}", e.getMessage(), e);
        // 未知异常的话，这里写逻辑，发邮件，发短信都可以、、
        return ResponseVO.errorResponse(BaseErrorCode.SYSTEM_ERROR.getCode(), BaseErrorCode.SYSTEM_ERROR.getMsg());
    }

    // <1> 处理 form data方式调用接口校验失败抛出的异常
    @ExceptionHandler(BindException.class)
    @Order(3)
    public ResponseVO<Object> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), BaseErrorCode.PARAMETER_ERROR.getMsg(), collect);
    }

    // <2> 处理 json 请求体调用接口校验失败抛出的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Order(2)
    public ResponseVO<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), BaseErrorCode.PARAMETER_ERROR.getMsg(), collect);
    }

    // <3> 处理单个参数校验失败抛出的异常
    @Order(1)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseVO<Object> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), BaseErrorCode.PARAMETER_ERROR.getMsg(), collect);
    }

    /**
     * BusinessException 自定义异常处理
     *
     * @param e 自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseVO<Object> applicationExceptionHandler(BusinessException e) {
        // 使用公共的结果类封装返回结果, 这里我指定状态码为
        return ResponseVO.errorResponse(e.getCode(), e.getMsg());
    }
}
