/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sj.im.codec.pack.group.CreateGroupPack;
import com.sj.im.codec.pack.group.DestroyGroupPack;
import com.sj.im.codec.pack.group.UpdateGroupInfoPack;
import com.sj.im.common.constant.CallbackCommandConstants;
import com.sj.im.common.enums.GroupMemberRoleEnum;
import com.sj.im.common.enums.GroupStatusEnum;
import com.sj.im.common.enums.GroupTypeEnum;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.enums.exception.GroupErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.friendship.web.req.GetGroupInfoReq;
import com.sj.im.service.group.entry.ImGroupEntity;
import com.sj.im.service.group.mapper.ImGroupMapper;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.group.web.callback.DestroyGroupCallbackDto;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.GetGroupResp;
import com.sj.im.service.group.web.resp.GetJoinedGroupResp;
import com.sj.im.service.group.web.resp.GetRoleInGroupResp;
import com.sj.im.service.helper.CallbackHelper;
import com.sj.im.service.helper.GroupMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组业务
 */
@Service
@Slf4j
public class ImGroupServiceImpl implements ImGroupService {
    @Resource
    private ImGroupMapper imGroupMapper;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackHelper callBackHelper;
    @Resource
    private GroupMessageHelper groupMessageHelper;

    /**
     * 导入群
     */
    @Override
    public void importGroup(ImportGroupReq req) {
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        // 判断群id是否为空，为空的话就自动生成一个
        if (CharSequenceUtil.isNotBlank(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // 如果groupId不为空的话，就查询一下该group是否已经存在
            lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
            lqw.eq(ImGroupEntity::getAppId, req.getAppId());
            long count = imGroupMapper.selectCount(lqw);
            if (count > 0) {
                throw new BusinessException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        // 导入逻辑
        ImGroupEntity entity = new ImGroupEntity();
        // 如果群所有者为空的话，报错
        if (ObjectUtil.equal(req.getGroupType(), GroupTypeEnum.PUBLIC.getCode()) && CharSequenceUtil.isBlank(req.getOwnerId())) {
            throw new BusinessException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        // 填充数据
        if (ObjectUtil.isNull(req.getCreateTime())) {
            entity.setCreateTime(new Date());
        }
        entity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtil.copyProperties(req, entity);
        int insert = imGroupMapper.insert(entity);

        if (insert != 1) {
            throw new BusinessException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
    }

    /**
     * 新建群组
     */
    @Override
    @Transactional
    public void createGroup(CreateGroupReq req) {
        boolean flag = false;
        if (!flag) {
            req.setOwnerId(req.getOperator());
        }
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        if (CharSequenceUtil.isBlank(req.getGroupId())) {
            // 如果id是空的话，说明也就是新的群组，数据库中没有该群组
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // 除此之外，就是有GroupId，现在判断是否GroupId是否在数据库中存在
            lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
            lqw.eq(ImGroupEntity::getAppId, req.getAppId());
            Long count = imGroupMapper.selectCount(lqw);
            if (count > 0) {
                throw new BusinessException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        // 如果要新建的群组是公开群组的话，但是没有所有者的话，就报错
        if (ObjectUtil.equal(req.getGroupType(), GroupTypeEnum.PUBLIC.getCode()) && CharSequenceUtil.isBlank(req.getOwnerId())) {
            throw new BusinessException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        ImGroupEntity imGroupEntity = BeanUtil.toBean(req, ImGroupEntity.class);
        imGroupEntity.setCreateTime(new Date());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        imGroupMapper.insert(imGroupEntity);

        // 插入群主
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(new Date());
        imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);

        //插入群成员
        for (GroupMemberDto dto : req.getMember()) {
            imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
        }

        // 回调
        if (appConfig.isCreateGroupAfterCallback()) {
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.CREATE_GROUP_AFTER, JSONUtil.toJsonStr(imGroupEntity));
        }

        // TCP通知
        CreateGroupPack pack = new CreateGroupPack();
        BeanUtil.copyProperties(imGroupEntity, pack);
        groupMessageHelper.producer(req.getOperator(), GroupEventCommand.CREATED_GROUP,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
    }

    /**
     * 修改群信息
     * 如果是后台管理员，则不检查权限，如果不是则检查
     * 如果是群主或者管理员可以修改其他信息
     */
    @Override
    @Transactional
    public void updateBaseGroupInfo(UpdateGroupReq req) {
        LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImGroupEntity::getGroupId, req.getGroupId());
        query.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(query);

        // 群组是否存在
        if (ObjectUtil.isNull(imGroupEntity)) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_EXIST);
        }
        // 群组是否解散
        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        boolean isAdmin = false;
        if (!isAdmin) {
            //不是后台调用需要检查权限
            GetRoleInGroupResp role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (ObjectUtil.isNull(role)) {
                throw new BusinessException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }

            Integer roleInfo = role.getRole();
            boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode())
                    || ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());
            //公开群只能群主修改资料
            if (!isManager && ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), imGroupEntity.getGroupType())) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        // 更新群组信息
        ImGroupEntity update = new ImGroupEntity();
        BeanUtil.copyProperties(req, update);
        update.setUpdateTime(new Date());
        int row = imGroupMapper.update(update, query);
        if (row != 1) {
            throw new BusinessException(BaseErrorCode.SYSTEM_ERROR);
        }

        // 之后回调
        if (appConfig.isModifyGroupAfterCallback()) {
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.UPDATE_GROUP_AFTER,
                    JSONUtil.toJsonStr(imGroupMapper.selectOne(query)));
        }

        // TCP通知
        UpdateGroupInfoPack pack = new UpdateGroupInfoPack();
        BeanUtil.copyProperties(req, pack);
        groupMessageHelper.producer(req.getOperator(), GroupEventCommand.UPDATED_GROUP,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
    }

    /**
     * 获取群的具体信息
     */
    @Override
    public GetGroupResp getGroupInfo(GetGroupInfoReq req) {
        // 判读一下群是否合法
        ImGroupEntity group = getGroup(req.getGroupId(), req.getAppId());
        // 准备返回的实体
        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtil.copyProperties(group, getGroupResp);
        try {
            // 返回的实体中也有成员，所以要查询成员信息
            List<GroupMemberDto> groupMember = imGroupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
            if (CollUtil.isNotEmpty(groupMember)) {
                // 查出来以后设置好即可
                getGroupResp.setMemberList(groupMember);
            }
        } catch (Exception e) {
            log.error("查询群信息失败，groupId:{}", req.getGroupId(), e);
        }
        return getGroupResp;
    }

    /**
     * 获取群信息
     */
    @Override
    public ImGroupEntity getGroup(String groupId, Integer appId) {
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, groupId);
        lqw.eq(ImGroupEntity::getAppId, appId);
        ImGroupEntity entity = imGroupMapper.selectOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        return entity;
    }

    /**
     * 获取用户加入的群组
     */
    @Override
    public GetJoinedGroupResp getJoinedGroup(GetJoinedGroupReq req) {
        // 1、获取到所有加入的群的id
        GetJoinedGroupResp resp = new GetJoinedGroupResp();
        List<String> memberJoinedGroup = imGroupMemberService.getMemberJoinedGroup(req);

        if (CollUtil.isEmpty(memberJoinedGroup)) {
            resp.setTotalCount(0L);
            resp.setGroupList(CollUtil.newArrayList());
            return resp;
        }

        // 分页
        Page<ImGroupEntity> page = new Page<>(Math.max(1, req.getOffset() - 1), req.getLimit());

        // 2、根据群id获取群信息
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        lqw.in(ImGroupEntity::getGroupId, memberJoinedGroup);
        if (CollUtil.isEmpty(req.getGroupType())) {
            lqw.in(ImGroupEntity::getGroupType, req.getGroupType());
        }

        Page<ImGroupEntity> selectPage = imGroupMapper.selectPage(page, lqw);
        resp.setGroupList(selectPage.getRecords());
        resp.setTotalCount(selectPage.getTotal());
        return resp;
    }

    /**
     * 解散群组
     * 只支持后台管理员和群主解散
     */
    @Override
    @Transactional
    public void destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;

        // 看看群组是否存在
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(lqw);
        // 判断群组是否存在
        if (ObjectUtil.isNull(imGroupEntity)) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        // 判断群组状态
        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        // 判断操作者是否为管理员
        if (!isAdmin && (ObjectUtil.equal(imGroupEntity.getGroupType(), GroupTypeEnum.PUBLIC.getCode())
                || ObjectUtil.notEqual(imGroupEntity.getOwnerId(), req.getOperator()))) {
            throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 也是软删除
        ImGroupEntity update = new ImGroupEntity();
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = imGroupMapper.update(update, lqw);
        if (update1 != 1) {
            throw new BusinessException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        // 之后回调
        if (appConfig.isDestroyGroupAfterCallback()) {
            DestroyGroupCallbackDto callbackDto = new DestroyGroupCallbackDto();
            callbackDto.setGroupId(req.getGroupId());
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.DESTROY_GROUP_AFTER, JSONUtil.toJsonStr(callbackDto));
        }

        // TCP通知
        DestroyGroupPack pack = new DestroyGroupPack();
        pack.setGroupId(req.getGroupId());
        groupMessageHelper.producer(req.getOperator(), GroupEventCommand.DESTROY_GROUP, pack, new ClientInfo(req.getAppId()
                , req.getClientType(), req.getImei()));
    }

    /**
     * 转让群主
     */
    @Override
    @Transactional
    public void transferGroup(TransferGroupReq req) {
        // 先判断一下转让群主的用户是不是群主
        GetRoleInGroupResp roleInGroupOne = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (ObjectUtil.notEqual(roleInGroupOne.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
            throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 安全检查一下 下一届的群主
        imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());

        // 查询一下该群组是否被解散了
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(lqw);

        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        int update = imGroupMapper.update(updateGroup, lqw);
        if (update != 1) {
            throw new BusinessException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
        }

        // 转让群组成员
        imGroupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());
    }

    /**
     * 群组禁言
     */
    @Override
    public void muteGroup(MuteGroupReq req) {
        // 安全检查群组
        ImGroupEntity groupResp = getGroup(req.getGroupId(), req.getAppId());

        if (ObjectUtil.equal(groupResp.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限
            GetRoleInGroupResp role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            Integer roleInfo = role.getRole();
            boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode()) || ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());

            //公开群只能群主修改资料
            if (!isManager) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }
        ImGroupEntity update = new ImGroupEntity();
        update.setMute(req.getMute());

        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        imGroupMapper.update(update, lqw);
    }

    /**
     * 增量同步群组成员列表
     */
    @Override
    public SyncResp<ImGroupEntity> syncJoinedGroupList(SyncReq req) {
        // 单次拉取最大
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }

        SyncResp<ImGroupEntity> resp = new SyncResp<>();

        // 获取该用户加入的所有的群 的 groupId
        List<String> memberJoinedGroup = imGroupMemberService.syncMemberJoinedGroup(req.getOperator(), req.getAppId());
        if (CollUtil.isNotEmpty(memberJoinedGroup)) {
            LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImGroupEntity::getAppId, req.getAppId());
            lqw.in(ImGroupEntity::getGroupId, memberJoinedGroup);
            lqw.gt(ImGroupEntity::getSequence, req.getLastSequence());
            lqw.last("limit " + req.getMaxLimit());
            lqw.orderByAsc(ImGroupEntity::getSequence);

            // 查询满足条件的群组列表
            List<ImGroupEntity> list = imGroupMapper.selectList(lqw);
            if (CollUtil.isNotEmpty(list)) {
                ImGroupEntity maxSeqEntity = list.get(list.size() - 1);
                resp.setDataList(list);
                // 设置最大seq
                Long maxSeq = imGroupMapper.getGroupMaxSeq(memberJoinedGroup, req.getAppId());
                resp.setMaxSequence(maxSeq);
                // 设置是否拉取完毕
                resp.setCompleted(maxSeqEntity.getSequence() >= maxSeq);
                return resp;
            }
        }
        resp.setCompleted(true);
        return resp;
    }

    /**
     * 动态获取群组中最大的seq
     */
    @Override
    public Long getUserGroupMaxSeq(String userId, Integer appId) {
        // 该用户加入的groupId
        List<String> memberJoinedGroup = imGroupMemberService.syncMemberJoinedGroup(userId, appId);
        if (CollUtil.isEmpty(memberJoinedGroup)) {
            throw new BusinessException(BaseErrorCode.SYSTEM_ERROR);
        }
        // 获取他加入的群组列表中最大的seq
        return imGroupMapper.getGroupMaxSeq(memberJoinedGroup, appId);
    }
}
