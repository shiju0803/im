/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

import lombok.Getter;

/**
 * 网关错误码枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
@Getter
public enum GateWayErrorCode implements BaseExceptionEnum {

    USER_SIGN_NOT_EXIST(60000,"用户签名不存在"),

    APPID_NOT_EXIST(60001,"appId不存在"),

    OPERATOR_NOT_EXIST(60002, "操作人信息不存在"),

    USER_SIGN_IS_ERROR(60003,"用户签名不正确"),

    USER_SIGN_OPERATE_NOT_MATE(60005,"用户签名与操作人不匹配"),

    USER_SIGN_IS_EXPIRED(60004,"用户签名已过期"),

    ;

    private final int code;
    private final String msg;

    GateWayErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}