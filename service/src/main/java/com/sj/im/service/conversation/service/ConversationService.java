/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.service.conversation.entry.ImConversationSetEntity;
import com.sj.im.service.conversation.web.req.DeleteConversationReq;
import com.sj.im.service.conversation.web.req.UpdateConversationReq;

/**
 * 会话服务接口
 *
 * @author ShiJu
 * @version 1.0
 */
public interface ConversationService extends IMppService<ImConversationSetEntity> {

    /**
     * 将会话 ID 转换为字符串形式
     *
     * @param type   会话类型
     * @param fromId 发送者 ID
     * @param toId   接收者 ID
     * @return 转换后的会话 ID 字符串
     */
    String convertConversationId(Integer type, String fromId, String toId);

    /**
     * 将消息标记为已读
     *
     * @param messageReadContent 消息已读内容
     */
    void messageMarkRead(MessageReadContent messageReadContent);

    /**
     * 删除会话
     *
     * @param req 删除会话请求
     * @return 删除结果响应
     */
    void deleteConversation(DeleteConversationReq req);

    /**
     * 更新会话，包括置顶或免打扰
     *
     * @param req 更新会话请求
     * @return 更新结果响应
     */
    void updateConversation(UpdateConversationReq req);

    /**
     * 同步会话集合
     *
     * @param req 同步请求
     * @return 同步结果响应
     */
    SyncResp<ImConversationSetEntity> syncConversationSet(SyncReq req);
}