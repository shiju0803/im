/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.sj.im.service.group.entry.ImGroupMemberEntity;
import com.sj.im.service.group.web.req.GroupMemberDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImGroupMemberMapper extends MppBaseMapper<ImGroupMemberEntity> {
    @Results({
            @Result(column = "member_id", property = "memberId"),
            @Result(column = "speak_date", property = "speakDate"),
            @Result(column = "role", property = "role"),
            @Result(column = "alias", property = "alias"),
            @Result(column = "join_time", property = "joinTime"),
            @Result(column = "join_type", property = "joinType")
    })
    @Select("select " +
            " member_id, " +
            " speak_date,  " +
            " role, " +
            " alias, " +
            " join_time ," +
            " join_type " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} ")
    List<GroupMemberDto> getGroupMember(@Param("appId") Integer appId, @Param("groupId") String groupId);

    @Select("select group_id from im_group_member where app_id = #{appId} and member_id = #{memberId}")
    List<String> getJoinedGroupId(@Param("appId") Integer appId, @Param("memberId") String memberId);

    @Select("select " +
            " member_id " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} and role != 3")
    List<String> getGroupMemberId(@Param("appId") Integer appId, @Param("groupId") String groupId);

    @Results({
            @Result(column = "member_id", property = "memberId"),
            @Result(column = "speak_flag", property = "speakFlag"),
            @Result(column = "role", property = "role"),
            @Result(column = "alias", property = "alias"),
            @Result(column = "join_time", property = "joinTime"),
            @Result(column = "join_type", property = "joinType")
    })
    @Select("select " +
            " member_id, " +
            " speak_flag,  " +
            " role " +
            " alias, " +
            " join_time ," +
            " join_type " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} and role in (1,2) ")
    List<GroupMemberDto> getGroupManager(@Param("groupId") String groupId, @Param("appId") Integer appId);

    @Select("select group_id from im_group_member where app_id = #{appId} AND member_id = #{memberId} and role != #{role}")
    List<String> syncJoinedGroupId(@Param("appId") Integer appId, @Param("memberId") String memberId, @Param("role") int role);
}