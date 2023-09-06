/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sj.im.service.conversation.entity.ImConversationSetEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ImConversationSetMapper extends BaseMapper<ImConversationSetEntity> {

    // 标记已读消息
    @Update(" update im_conversation_set set read_sequence = #{readSequence},sequence = #{sequence} " +
            " where conversation_id = #{conversationId} and app_id = #{appId} AND read_sequence < #{readSequence}")
    void readMark(ImConversationSetEntity imConversationSetEntity);

    // 获取会话最大序列号
    @Select(" select max(sequence) from im_conversation_set where app_id = #{appId} AND from_id = #{userId} ")
    Long geConversationSetMaxSeq(Integer appId, String userId);
}

