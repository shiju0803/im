/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.message.MessageReadPack;
import com.sj.im.codec.pack.message.RecallMessageNotifyPack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.enums.command.MessageCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.common.model.message.MessageReceiveAckContent;
import com.sj.im.common.model.message.OfflineMessageContent;
import com.sj.im.common.model.message.RecallMessageContent;
import com.sj.im.service.conversation.service.ConversationService;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.message.service.MessageSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 消息同步服务
 */
@Slf4j
@Service
public class MessageSyncServiceImpl implements MessageSyncService {

    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ConversationService conversationService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 接收消息确认，发送给用户
     *
     * @param receiveAckContent 消息接收确认内容
     */
    public void receiveMark(MessageReceiveAckContent receiveAckContent) {
        messageHelper.sendToUser(receiveAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, receiveAckContent, receiveAckContent.getAppId());
    }

    /**
     * 消息已读。更新会话的seq，通知在线的同步端发送指定command ，发送已读回执通知对方（消息发起方）我已读
     *
     * @param readContent 消息已读内容
     */
    @Override
    public void readMark(MessageReadContent readContent) {
        conversationService.messageMarkRead(readContent);
        MessageReadPack pack = BeanUtil.toBean(readContent, MessageReadPack.class);
        syncToSender(pack, readContent, MessageCommand.MSG_READ_NOTIFY);
        messageHelper.sendToUser(readContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, readContent, readContent.getAppId());
    }

    /**
     * 将消息读取状态同步到接收者的其他客户端
     *
     * @param pack    消息读取包
     * @param content 消息读取内容
     * @param command 命令
     */
    @Override
    public void syncToSender(MessageReadPack pack, MessageReadContent content, Command command) {
        // 该方法会向所有与发送者不同的客户端发送消息，以通知它们该消息已被读取
        messageHelper.sendToUserExceptClient(pack.getFromId(), command, pack, content);
    }

    /**
     * 将群组消息标记为已读
     *
     * @param readContent 包含已读消息信息的对象
     */
    @Override
    public void groupReadMark(MessageReadContent readContent) {
        // 调用conversationService的messageMarkRead方法，将消息标记为已读
        conversationService.messageMarkRead(readContent);
        // 创建MessageReadPack对象，并将messageRead对象的属性复制到messageReadPack对象中
        MessageReadPack messageReadPack = BeanUtil.toBean(readContent, MessageReadPack.class);
        // 调用syncToSender方法，将消息已读状态同步给接收方的其他客户端
        syncToSender(messageReadPack, readContent, GroupEventCommand.MSG_GROUP_READ_NOTIFY);
        // 如果消息发送方和接收方不是同一个人，则调用messageProducer的sendToUser方法，将已读消息发送给发送方
        if (ObjectUtil.notEqual(readContent.getFromId(), readContent.getToId())) {
            List<ClientInfo> clientInfos = messageHelper.sendToUser(messageReadPack.getToId(), GroupEventCommand.MSG_GROUP_READ_RECEIPT, readContent, readContent.getAppId());
            log.info("将已读消息发送给发送方[{}]的所有客户端，发送成功的客户端列表：{}", messageReadPack.getToId(), JSONUtil.toJsonStr(clientInfos));
        }
    }

    /**
     * 同步离线消息
     *
     * @param req 请求参数，包括appId、operater、lastSequence和maxLimit
     * @return RestResponse对象，包括SyncResp对象
     */
    @Override
    public SyncResp<OfflineMessageContent> syncOfflineMessage(SyncReq req) {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();

        String key = req.getAppId() + ":" + RedisConstants.OFFLINE_MESSAGE + ":" + req.getOperator();
        // 获取最大seq
        long maxSeq = 0L;
        ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> set = zSetOperations.reverseRangeWithScores(key, 0, 0);
        if (CollUtil.isNotEmpty(set)) {
            List<ZSetOperations.TypedTuple<String>> list = new ArrayList<>(set);
            DefaultTypedTuple<String> o = (DefaultTypedTuple<String>) list.get(0);
            maxSeq = ObjectUtil.defaultIfNull(o.getScore(), 0D).longValue();
        }

        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);

        Set<ZSetOperations.TypedTuple<String>> querySet = zSetOperations.rangeByScoreWithScores(key, req.getLastSequence(), maxSeq, 0, req.getMaxLimit());

        if (CollUtil.isNotEmpty(querySet)) {
            for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
                String value = typedTuple.getValue();
                OfflineMessageContent messageContent = JSONUtil.toBean(value, OfflineMessageContent.class);
                respList.add(messageContent);
            }
            resp.setDataList(respList);

            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        }
        return resp;
    }

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
    @Override
    public void recallMessage(RecallMessageContent content) {

    }

    /**
     * 处理消息撤回的确认操作。
     *
     * @param recallPack 撤回通知包含的信息
     * @param success    撤回操作成功的响应对象
     * @param clientInfo 客户端信息
     */
    @Override
    public void recallAck(RecallMessageNotifyPack recallPack, ResponseVO<Object> success, ClientInfo clientInfo) {

    }
}
