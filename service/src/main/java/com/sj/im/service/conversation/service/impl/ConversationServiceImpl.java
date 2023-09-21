/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.conversation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.sj.im.codec.pack.conversation.DeleteConversationPack;
import com.sj.im.codec.pack.conversation.UpdateConversationPack;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.ConversationTypeEnum;
import com.sj.im.common.enums.command.ConversationEventCommand;
import com.sj.im.common.enums.exception.ConversationErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.common.model.message.MessageReadContent;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.conversation.entry.ImConversationSetEntity;
import com.sj.im.service.conversation.mapper.ImConversationSetMapper;
import com.sj.im.service.conversation.service.ConversationService;
import com.sj.im.service.conversation.web.req.DeleteConversationReq;
import com.sj.im.service.conversation.web.req.UpdateConversationReq;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.util.RedisSeq;
import com.sj.im.service.util.WriteUserSeq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * ConversationServiceImpl 是实现 ConversationService 接口的具体实现类
 *
 * @author ShiJu
 * @version 1.0
 */
@Service
public class ConversationServiceImpl extends MppServiceImpl<ImConversationSetMapper, ImConversationSetEntity>
        implements ConversationService {
    @Resource
    private ImConversationSetMapper imConversationSetMapper;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisSeq redisSeq;
    @Resource
    private WriteUserSeq writeUserSeq;

    /**
     * 将 fromId 和 toId 组合成 conversationId
     *
     * @param type   会话类型
     * @param fromId 发送者ID
     * @param toId   接收者ID
     * @return 组合后的 conversationId
     */
    public String convertConversationId(Integer type, String fromId, String toId) {
        return type + "_" + fromId + "_" + toId;
    }

    /**
     * 将消息标记为已读
     *
     * @param messageReadContent 消息已读内容
     */
    @Transactional
    public void messageMarkRead(MessageReadContent messageReadContent) {
        String toId = messageReadContent.getToId();
        if (ObjectUtil.equal(messageReadContent.getConversationType(), ConversationTypeEnum.GROUP.getCode())) {
            toId = messageReadContent.getGroupId();
        }
        String conversationId =
                convertConversationId(messageReadContent.getConversationType(), messageReadContent.getFromId(), toId);
        // 查询是否存在该会话
        LambdaQueryWrapper<ImConversationSetEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImConversationSetEntity::getConversationId, conversationId);
        query.eq(ImConversationSetEntity::getAppId, messageReadContent.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(query);
        // 如果不存在，则新建一个会话
        if (ObjectUtil.isNull(imConversationSetEntity)) {
            imConversationSetEntity = BeanUtil.toBean(messageReadContent, ImConversationSetEntity.class);
            long seq = redisSeq.doGetSeq(messageReadContent.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
            imConversationSetEntity.setConversationId(conversationId);
            imConversationSetEntity.setReadSequence(messageReadContent.getMessageSequence());
            imConversationSetEntity.setToId(toId);
            imConversationSetEntity.setSequence(seq);
            imConversationSetMapper.insert(imConversationSetEntity);
            writeUserSeq.writeUserSeq(messageReadContent.getAppId(), messageReadContent.getFromId(),
                                      SeqConstants.CONVERSATION_SEQ, seq);
            return;
        }

        // 如果存在，则更新该会话的已读消息序号和 seq
        long seq = redisSeq.doGetSeq(messageReadContent.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
        imConversationSetEntity.setSequence(seq);
        imConversationSetEntity.setReadSequence(messageReadContent.getMessageSequence());
        imConversationSetMapper.readMark(imConversationSetEntity);
        writeUserSeq.writeUserSeq(messageReadContent.getAppId(), messageReadContent.getFromId(),
                                  SeqConstants.CONVERSATION_SEQ, seq);
    }

    /**
     * 删除会话
     *
     * @param req 删除会话请求
     * @return 删除结果
     */
    @Transactional
    public void deleteConversation(DeleteConversationReq req) {
        // 有置顶/免打扰, 清除状态
        LambdaQueryWrapper<ImConversationSetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImConversationSetEntity::getConversationId, req.getConversationId());
        wrapper.eq(ImConversationSetEntity::getAppId, req.getAppId());
        ImConversationSetEntity conversationSet = imConversationSetMapper.selectOne(wrapper);
        if (ObjectUtil.isNotNull(conversationSet)) {
            conversationSet.setIsMute(0);
            conversationSet.setIsTop(0);
            imConversationSetMapper.update(conversationSet, wrapper);
        }

        if (ObjectUtil.equal(appConfig.getDeleteConversationSyncMode(), 1)) {
            DeleteConversationPack pack = new DeleteConversationPack();
            pack.setConversationId(req.getConversationId());
            messageHelper.sendToUserExceptClient(req.getFromId(), ConversationEventCommand.CONVERSATION_DELETE, pack,
                                                 new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }
    }

    /**
     * 更新会话信息(置顶、免打扰)
     *
     * @param req 更新会话请求
     * @return 更新结果
     */
    @Transactional
    public void updateConversation(UpdateConversationReq req) {
        // 如果更新参数为空，则返回参数错误
        if (ObjectUtil.isNull(req.getIsTop()) && ObjectUtil.isNull(req.getIsMute())) {
            throw new BusinessException(ConversationErrorCode.CONVERSATION_UPDATE_PARAM_ERROR);
        }

        LambdaQueryWrapper<ImConversationSetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImConversationSetEntity::getConversationId, req.getConversationId());
        wrapper.eq(ImConversationSetEntity::getAppId, req.getAppId());
        ImConversationSetEntity conversationSet = imConversationSetMapper.selectOne(wrapper);

        // 如果会话存在，则更新会话信息
        if (ObjectUtil.isNotNull(conversationSet)) {
            long seq = redisSeq.doGetSeq(req.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
            if (ObjectUtil.isNotNull(req.getIsTop())) {
                conversationSet.setIsTop(req.getIsTop());
            }
            if (ObjectUtil.isNotNull(req.getIsMute())) {
                conversationSet.setIsMute(req.getIsMute());
            }
            conversationSet.setSequence(seq);
            imConversationSetMapper.update(conversationSet, wrapper);
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), SeqConstants.CONVERSATION_SEQ, seq);

            // 向客户端发送更新会话事件
            UpdateConversationPack pack = new UpdateConversationPack();
            pack.setConversationId(req.getConversationId());
            pack.setIsMute(req.getIsMute());
            pack.setIsTop(req.getIsTop());
            pack.setSequence(seq);
            pack.setConversationType(conversationSet.getConversationType());
            messageHelper.sendToUserExceptClient(req.getFromId(), ConversationEventCommand.CONVERSATION_UPDATE, pack,
                                                 new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }
    }

    /**
     * 同步会话列表
     *
     * @param req 同步请求
     * @return 同步结果
     */
    public SyncResp<ImConversationSetEntity> syncConversationSet(SyncReq req) {
        // 如果 maxLimit 超过 100，则将其设为 100
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }
        SyncResp<ImConversationSetEntity> resp = new SyncResp<>();
        LambdaQueryWrapper<ImConversationSetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImConversationSetEntity::getFromId, req.getOperator());
        queryWrapper.gt(ImConversationSetEntity::getSequence, req.getLastSequence());
        queryWrapper.eq(ImConversationSetEntity::getAppId, req.getAppId());
        queryWrapper.last(" limit " + req.getMaxLimit());
        queryWrapper.orderByAsc(ImConversationSetEntity::getSequence);
        List<ImConversationSetEntity> list = imConversationSetMapper.selectList(queryWrapper);

        // 如果查询结果不为空，则设置同步结果的 dataList、maxSequence 和 completed 属性
        if (!CollectionUtils.isEmpty(list)) {
            ImConversationSetEntity maxSeqEntity = list.get(list.size() - 1);
            resp.setDataList(list);
            // 设置最大 seq
            Long friendShipMaxSeq = imConversationSetMapper.geConversationSetMaxSeq(req.getAppId(), req.getOperator());
            resp.setMaxSequence(friendShipMaxSeq);
            // 设置是否拉取完毕
            resp.setCompleted(maxSeqEntity.getSequence() >= friendShipMaxSeq);
            return resp;
        }

        // 如果查询结果为空，则设置同步结果的 completed 属性为 true
        resp.setCompleted(true);
        return resp;
    }
}
