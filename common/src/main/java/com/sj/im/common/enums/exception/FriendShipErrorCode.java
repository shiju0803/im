/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.enums.exception;

/**
 * 好友错误枚举类
 *
 * @author ShiJu
 * @version 1.0
 */
public enum FriendShipErrorCode implements BaseExceptionEnum {

    IMPORT_SIZE_BEYOND(30000, "导入數量超出上限"),

    ADD_FRIEND_ERROR(30001, "添加好友失败"),

    TO_IS_YOUR_FRIEND(30002, "对方已经是你的好友"),

    TO_IS_NOT_YOUR_FRIEND(30003, "对方不是你的好友"),

    FRIEND_IS_DELETED(30004, "好友已被删除"),

    ADD_FRIEND_REQUEST_ERROR(30005, "添加好友申请失败"),

    FRIEND_IS_BLACK(30006, "好友已被拉黑"),

    TARGET_IS_BLACK_YOU(30007, "对方把你拉黑"),

    REPEAT_SHIP_IS_NOT_EXIST(30008, "关系链记录不存在"),

    ADD_BLACK_ERROR(30009, "添加黑名單失败"),

    FRIEND_IS_NOT_YOUR_BLACK(30010, "好友已經不在你的黑名單内"),

    NOT_APPROVAL_OTHER_MAN_REQUEST(30011, "无法审批其他人的好友请求"),

    FRIEND_REQUEST_IS_NOT_EXIST(30012, "好友申请不存在"),

    FRIEND_SHIP_GROUP_CREATE_ERROR(30014, "好友分组创建失败"),

    FRIEND_SHIP_GROUP_IS_EXIST(30015, "好友分组已存在"),

    FRIEND_SHIP_GROUP_IS_NOT_EXIST(30016, "好友分组不存在"),

    ;

    private final int code;
    private final String msg;

    FriendShipErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误代码
     */
    @Override
    public int getCode() {
        return this.code;
    }

    /**
     * 错误提示信息
     */
    @Override
    public String getMsg() {
        return this.msg;
    }
}