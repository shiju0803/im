/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.sj.im.service.friendship.entry.ImFriendShipEntity;
import com.sj.im.service.friendship.web.req.CheckFriendShipReq;
import com.sj.im.service.friendship.web.resp.CheckFriendShipResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImFriendShipMapper extends MppBaseMapper<ImFriendShipEntity> {
    @Select("<script>" +
            "select from_id as fromId, to_id as toId, if(status = 1, 1, 0) as status from im_friendship where from_id = #{fromId} and to_id in " +
            "<foreach collection='toIds' index = 'index' item = 'id' separator = ',' close = ')' open = '('>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req);


    @Select("<script>" +
            "select a.fromId, a.toId, ( " +
            "case " +
            "when a.status = 1 and b.status = 1 then 1 " +
            "when a.status = 1 and b.status != 1 then 2 " +
            "when a.status != 1 and b.status = 1 then 3 " +
            "when a.status != 1 and b.status != 1 then 4 " +
            "end" +
            ")" +
            "as status from " +
            "(select from_id as fromId, to_id as toId, if(status = 1, 1, 0) as status from im_friendship where app_id = #{appId} and from_id = #{fromId} and to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            "#{id}" +
            "</foreach>" +
            ") as a inner join" +
            "(select from_id as fromId, to_id as toId, if(status = 1, 1, 0) as status from im_friendship where app_id = #{appId} and to_id = #{fromId} and from_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            "#{id}" +
            "</foreach>" +
            ") as b " +
            "on a.fromId = b.toId and a.toId = b.fromId" +
            "</script>")
    List<CheckFriendShipResp> checkFriendShipBoth(CheckFriendShipReq req);


    @Select("<script>" +
            " select from_id AS fromId, to_id AS toId , if(black = 1,1,0) as status from im_friendship where app_id = #{appId} and from_id = #{fromId} and to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            "</script>"
    )
    List<CheckFriendShipResp> checkFriendShipBlack(CheckFriendShipReq req);

    @Select("<script>" +
            " select a.fromId,a.toId , ( \n" +
            " case \n" +
            " when a.black = 1 and b.black = 1 then 1 \n" +
            " when a.black = 1 and b.black != 1 then 2 \n" +
            " when a.black != 1 and b.black = 1 then 3 \n" +
            " when a.black != 1 and b.black != 1 then 4 \n" +
            " end \n" +
            " ) \n " +
            " as status from " +
            " (select from_id AS fromId , to_id AS toId , if(black = 1,1,0) as black from im_friendship where app_id = #{appId} and from_id = #{fromId} AND to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as a INNER join" +
            " (select from_id AS fromId, to_id AS toId , if(black = 1,1,0) as black from im_friendship where app_id = #{appId} and to_id = #{fromId} AND from_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as b " +
            " on a.fromId = b.toId AND b.fromId = a.toId " +
            "</script>"
    )
    List<CheckFriendShipResp> checkFriendShipBlackBoth(CheckFriendShipReq toId);

    @Select("select max(friend_sequence) from im_friendship where app_id = #{appId} AND from_id = #{userId} ")
    Long getFriendShipMaxSeq(@Param("appId") Integer appId, @Param("userId") String userId);

    @Select("select to_id from im_friendship where from_id = #{userId} AND app_id = #{appId} and status = 1 and black = 1")
    List<String> getAllFriendId(@Param("userId") String userId, @Param("appId") Integer appId);
}
