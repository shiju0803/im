/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 数据库用户数据实体类
 */
@Data
@TableName("im_user_data")
public class ImUserDataEntity {
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户名称
     */
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 位置
     */
    @TableField(value = "location")
    private String location;

    /**
     * 生日
     */
    @TableField(value = "birth_day")
    private String birthDay;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 头像
     */
    @TableField(value = "photo")
    private String photo;

    /**
     * 性别
     */
    @TableField(value = "user_sex")
    private Integer userSex;

    /**
     * 个性签名
     */
    @TableField(value = "self_signature")
    private String selfSignature;

    /**
     * 添加好友方式 1无需验证 2需要验证
     */
    @TableField(value = "friend_allow_type")
    private Integer friendAllowType;

    /**
     * 管理员禁止用户添加加好友：0 未禁用 1 已禁用
     */
    @TableField(value = "disable_add_friend")
    private Integer disableAddFriend;

    /**
     * 禁用标识(0 未禁用 1 已禁用)
     */
    @TableField(value = "forbidden_flag")
    private Integer forbiddenFlag;

    /**
     * 禁言标识 0未禁言 1禁言
     */
    @TableField(value = "silent_flag")
    private Integer silentFlag;

    /**
     * 用户类型 1普通用户 2客服 3机器人
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * appId
     */
    @TableField(value = "app_id")
    private Integer appId;

    /**
     * 删除标识 0未删除 1已删除
     */
    @TableField(value = "del_flag")
    private Integer delFlag;

    /**
     * 拓展参数
     */
    @TableField(value = "extra")
    private String extra;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
