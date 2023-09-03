/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.model;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sj.im.common.enums.exception.BaseExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 公共返回实体类
 */
@Data
@ApiModel("公共返回实体类")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseVO<T> {

    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    public static final String DEFAULT_ERROR_MESSAGE = "error";
    public static final Integer DEFAULT_SUCCESS_CODE = 200;
    public static final Integer DEFAULT_ERROR_CODE = 500;

    @ApiModelProperty(value = "响应状态码")
    private Integer code;

    @ApiModelProperty(value = "提示信息")
    private String msg;

    @ApiModelProperty(value = "逻辑数据")
    private T data;

    public ResponseVO() {
        this.code = DEFAULT_SUCCESS_CODE;
        this.msg = DEFAULT_SUCCESS_MESSAGE;
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseVO<T> successResponse() {
        return new ResponseVO<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE);
    }

    public static <T> ResponseVO<T> successResponse(String msg) {
        return new ResponseVO<>(DEFAULT_SUCCESS_CODE, msg);
    }

    public static <T> ResponseVO<T> successResponse(T data) {
        return new ResponseVO<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ResponseVO<T> successResponse(String msg, T data) {
        return new ResponseVO<>(DEFAULT_SUCCESS_CODE, msg, data);
    }

    public static <T> ResponseVO<T> errorResponse() {
        return new ResponseVO<>(500, "系统内部异常");
    }

    public static <T> ResponseVO<T> errorResponse(String msg) {
        return new ResponseVO<>(DEFAULT_ERROR_CODE, msg);
    }

    public static <T> ResponseVO<T> errorResponse(T data) {
        return new ResponseVO<>(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE, data);
    }

    public static <T> ResponseVO<T> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> errorResponse(int code, T data) {
        return new ResponseVO<>(code, DEFAULT_ERROR_MESSAGE, data);
    }

    public static <T> ResponseVO<T> errorResponse(BaseExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getMsg());
    }

    public boolean isOk() {
        return ObjectUtil.equal(this.code, DEFAULT_SUCCESS_CODE);
    }
}

