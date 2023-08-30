/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.dao;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
    private String userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 位置
     */
    private String location;

    /**
     * 生日
     */
    private String birthDay;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String photo;

    /**
     * 性别
     */
    private Integer userSex;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 添加好友方式 1无需验证 2需要验证
     */
    private Integer friendAllowType;

    /**
     * 管理员禁止用户添加加好友：0 未禁用 1 已禁用
     */
    private Integer disableAddFriend;

    /**
     * 禁用标识(0 未禁用 1 已禁用)
     */
    private Integer forbiddenFlag;

    /**
     * 禁言标识 0未禁言 1禁言
     */
    private Integer silentFlag;

    /**
     * 用户类型 1普通用户 2客服 3 机器人
     */
    private Integer userType;

    /**
     * appId
     */
    private Integer appId;

    /**
     * 删除标识 0未删除 1已删除
     */
    private Integer delFlag;

    /**
     * 拓展参数
     */
    private String extra;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
