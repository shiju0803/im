/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.exception;

import cn.hutool.core.text.CharSequenceUtil;
import com.sj.im.common.BaseErrorCode;
import com.sj.im.common.ResponseVO;
import com.sj.im.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Exception 未知异常处理
     *
     * @param e 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO<Object> unknownException(Exception e) {
        log.error("未知异常:", e);
        // 未知异常的话，这里写逻辑，发邮件，发短信都可以、、
        return ResponseVO.errorResponse(BaseErrorCode.SYSTEM_ERROR.getCode(), BaseErrorCode.SYSTEM_ERROR.getError());
    }

    /**
     * ConstraintViolationException 参数校验异常处理
     *
     * @param ex 参数校验异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseVO<Object> handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());

            return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), message);
        }
        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), BaseErrorCode.PARAMETER_ERROR.getError() + ex.getMessage());
    }

    /**
     * ApplicationException 自定义异常处理
     *
     * @param e 自定义异常
     */
    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseVO<Object> applicationExceptionHandler(ApplicationException e) {
        // 使用公共的结果类封装返回结果, 这里我指定状态码为
        return ResponseVO.errorResponse(e.getCode(), e.getError());
    }

    /**
     * BindException 绑定异常处理
     *
     * @param ex 绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseVO<Object> handleException2(BindException ex) {
        FieldError err = ex.getFieldError();
        String message = CharSequenceUtil.EMPTY;
        if (err != null) {
            message = "参数{".concat(err.getField()).concat("}").concat(Objects.requireNonNull(err.getDefaultMessage()));
        }
        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), message);
    }

    /**
     * MethodArgumentNotValidException 方法参数无效异常处理
     *
     * @param ex 方法参数无效异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVO<Object> handleException1(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());

        return ResponseVO.errorResponse(BaseErrorCode.PARAMETER_ERROR.getCode(), BaseErrorCode.PARAMETER_ERROR.getError() + " : " + errorMsg);
    }
}
