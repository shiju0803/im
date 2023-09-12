/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.exception;

import com.sj.im.common.enums.exception.BaseExceptionEnum;
import lombok.Getter;

/**
 * 自定义业务异常类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1264760508056698922L;

    private Integer code;
    private String msg;

    public BusinessException(BaseExceptionEnum errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException() {
        super();
    }

    public BusinessException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public BusinessException(Object data, BaseExceptionEnum errorCode, String arg0, Throwable arg1) {
        super(arg0, arg1);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BusinessException(BaseExceptionEnum errorCode, String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BusinessException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public BusinessException(BaseExceptionEnum errorCode, String arg0, Throwable arg1) {
        super(arg0, arg1);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BusinessException(String arg0) {
        super(arg0);
        this.msg = arg0;
    }

    public BusinessException(Throwable arg0) {
        super(arg0);
    }

    public BusinessException(BaseExceptionEnum errorCode, Throwable arg0) {
        super(arg0);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }
}
