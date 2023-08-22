package com.sj.im.common.enums;

import com.sj.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 公共返回实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> {
    // 状态码
    private Integer code;

    private String msg;

    private T data;

    public static <T> ResponseVO<T> successResponse(T data) {
        return new ResponseVO<>(200, "success", data);
    }

    public static <T> ResponseVO<T> successResponse() {
        return new ResponseVO<>(200, "success");
    }

    public static <T> ResponseVO<T> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> errorResponse(T data) {
        return new ResponseVO<>(501, "参数错误", data);
    }

    public static <T> ResponseVO<T> errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public static <T> ResponseVO<T> errorResponse() {
        return new ResponseVO<>(500, "系统内部异常");
    }

    public boolean isOk(){
        return this.code == 200;
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

