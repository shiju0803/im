/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.sj.im.service.group.entry.ImGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;

@Mapper
public interface ImGroupMapper extends MppBaseMapper<ImGroupEntity> {
    /**
     * @param groupId 群id
     * @return java.lang.Long
     * @description 获取加入的群的最大seq
     * @author ShiJu
     */
    @Select(" <script> " + " select max(sequence) from im_group where app_id = #{appId} and group_id in " + "<foreach collection='groupId' index='index' item='id' separator=',' close=')' open='('>" + " #{id} " + "</foreach>" + " </script> ")
    Long getGroupMaxSeq(@Param("groupId") Collection<String> groupId, @Param("appId") Integer appId);
}