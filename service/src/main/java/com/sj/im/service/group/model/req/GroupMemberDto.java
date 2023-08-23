/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.model.req;

import lombok.Data;

import java.util.Date;

@Data
public class GroupMemberDto {

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 群成员类型，0 普通成员, 1 管理员, 2 群主， 3 已经移除的成员，当修改群成员信息时，只能取值0/1，其他值由其他接口实现，暂不支持3
     */
    private Integer role;

    /**
     * 禁言到期时间
     */
    private Long speakDate;

    /**
     * 进入方式
     */
    private String joinType;

    /**
     * 加入时间
     */
    private Date joinTime;
}