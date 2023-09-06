/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service;

import com.sj.im.codec.pack.message.MessageReadPack;
import com.sj.im.codec.pack.message.RecallMessageNotifyPack;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.common.model.message.RecallMessageContent;

public interface MessageSyncService {

    /**
     * 接收消息确认，发送给用户
     *
     * @param receiveAckContent 消息接收确认内容
     */
    void receiveMark(MessageReceiveAckContent receiveAckContent);

    /**
     * 消息已读。更新会话的seq，通知在线的同步端发送指定command ，发送已读回执通知对方（消息发起方）我已读
     *
     * @param readContent 消息已读内容
     */
    void readMark(MessageReadContent readContent);

    /**
     * 将消息读取状态同步到发送者的其他客户端
     *
     * @param pack    消息读取包
     * @param content 消息读取内容
     * @param command 命令
     */
    void syncToSender(MessageReadPack pack, MessageReadContent content, Command command);

    /**
     * 将群组消息标记为已读
     *
     * @param readContent 包含已读消息信息的对象
     */
    void groupReadMark(MessageReadContent readContent);

    /**
     * 同步离线消息
     *
     * @param req 请求参数，包括appId、operator、lastSequence和maxLimit
     * @return RestResponse对象，包括SyncResp对象
     */
    SyncResp syncOfflineMessage(SyncReq req);

    /**
     * 处理消息撤回操作，具体步骤如下:
     * <p>
     * 1.修改历史消息的状态
     * 2.修改离线消息的状态
     * 3.发送确认消息给发送方
     * 4.发送同步消息给同步端
     * 5.分发撤回通知给消息接收方
     * </p>
     *
     * @param content 撤回消息内容
     */
    void recallMessage(RecallMessageContent content);

    /**
     * 处理消息撤回的确认操作。
     *
     * @param recallPack 撤回通知包含的信息
     * @param success    撤回操作成功的响应对象
     * @param clientInfo 客户端信息
     */
    void recallAck(RecallMessageNotifyPack recallPack, ResponseVO<Object> success, ClientInfo clientInfo);
}