/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.constant;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 回调公共常量
 */
public class CallbackCommandConstants {
    /**
     * 修改用户之后
     */
    public static final String MODIFY_USER_AFTER = "user.modify.after";

    /**
     * 删群创建之后
     */
    public static final String CREATE_GROUP_AFTER = "group.create.after";

    /**
     * 群更新之后
     */
    public static final String UPDATE_GROUP_AFTER = "group.update.after";

    /**
     * 群解散之后
     */
    public static final String DESTROY_GROUP_AFTER = "group.destroy.after";

    /**
     * 群转让之后
     */
    public static final String TRANSFER_GROUP_AFTER = "group.transfer.after";

    /**
     * 添加群成员之前
     */
    public static final String GROUP_MEMBER_ADD_BEFORE = "group.member.add.before";

    /**
     * 添加群成员之后
     */
    public static final String GROUP_MEMBER_ADD_AFTER = "group.member.add.after";

    /**
     * 删除群成员之后
     */
    public static final String GROUP_MEMBER_DELETE_AFTER = "group.member.delete.after";

    /**
     * 添加好友之前
     */
    public static final String ADD_FRIEND_BEFORE = "friend.add.before";

    /**
     * 添加好友之后
     */
    public static final String ADD_FRIEND_AFTER = "friend.add.after";

    /**
     * 更新好友之前
     */
    public static final String UPDATE_FRIEND_BEFORE = "friend.update.before";

    /**
     * 更新好友之后
     */
    public static final String UPDATE_FRIEND_AFTER = "friend.update.after";

    /**
     * 删除好友之后
     */
    public static final String DELETE_FRIEND_AFTER = "friend.delete.after";

    /**
     * 添加黑名单之后
     */
    public static final String ADD_BLACK_AFTER = "black.add.after";

    /**
     * 移除黑名单
     */
    public static final String DELETE_BLACK = "black.delete";

    /**
     * 发送消息之后
     */
    public static final String SEND_MESSAGE_AFTER = "message.send.after";

    /**
     * 发送消息之前
     */
    public static final String SEND_MESSAGE_BEFORE = "message.send.before";
}