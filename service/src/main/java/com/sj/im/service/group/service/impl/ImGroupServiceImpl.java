/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.im.common.enums.*;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.model.req.GetGroupInfoReq;
import com.sj.im.service.group.dao.ImGroupEntity;
import com.sj.im.service.group.dao.mapper.ImGroupMapper;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.model.resp.GetGroupResp;
import com.sj.im.service.group.model.resp.GetRoleInGroupResp;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.service.ImGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
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

    /**
     * 导入群
     */
    @Override
    public ResponseVO<String> importGroup(ImportGroupReq req) {
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
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        // 导入逻辑
        ImGroupEntity entity = new ImGroupEntity();
        // 如果群所有者为空的话，报错
        if (ObjectUtil.equal(req.getGroupType(), GroupTypeEnum.PUBLIC.getCode()) && CharSequenceUtil.isBlank(req.getOwnerId())) {
            return ResponseVO.errorResponse(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        entity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, entity);

        // 填充数据
        if (ObjectUtil.isNull(req.getCreateTime())) {
            entity.setCreateTime(new Date());
        }
        int insert = imGroupMapper.insert(entity);

        if (insert != 1) {
            return ResponseVO.errorResponse(GroupErrorCode.IMPORT_GROUP_ERROR);
        }

        return ResponseVO.successResponse();
    }

    /**
     * 新建群组
     */
    @Override
    @Transactional
    public ResponseVO<String> createGroup(CreateGroupReq req) {
        boolean flag = false;
        if (!flag) {
            req.setOwnerId(req.getOperator());
        }
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        if (CharSequenceUtil.isEmpty(req.getGroupId())) {
            // 如果id是空的话，说明也就是新的群组，数据库中没有该群组
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // 除此之外，就是有GroupId，现在判断是否GroupId是否在数据库中存在
            lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
            lqw.eq(ImGroupEntity::getAppId, req.getAppId());
            Long count = imGroupMapper.selectCount(lqw);
            if (count > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        // 如果要新建的群组是公开群组的话，但是没有所有者的话，就报错
        if (ObjectUtil.equal(req.getGroupType(), GroupTypeEnum.PUBLIC.getCode()) && CharSequenceUtil.isBlank(req.getOwnerId())) {
            return ResponseVO.errorResponse(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        ImGroupEntity imGroupEntity = new ImGroupEntity();

        BeanUtils.copyProperties(req, imGroupEntity);
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

        //TODO 回调

        //TODO TCP通知
        return ResponseVO.successResponse();
    }

    /**
     * 修改群信息
     */
    @Override
    public ResponseVO<String> updateBaseGroupInfo(UpdateGroupReq req) {
        LambdaQueryWrapper<ImGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(ImGroupEntity::getGroupId, req.getGroupId());
        query.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(query);

        // 群组是否存在
        if (ObjectUtil.isNull(imGroupEntity)) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        // 群组是否解散
        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

            if (!role.isOk()) {
                return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode()) || ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());

            //公开群只能群主修改资料
            if (!isManager && ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), imGroupEntity.getGroupType())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        BeanUtils.copyProperties(req, update);
        update.setUpdateTime(new Date());
        int row = imGroupMapper.update(update, query);

        if (row != 1) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        //TODO TCP通知
        return ResponseVO.successResponse();
    }

    /**
     * 获取群的具体信息
     */
    @Override
    public ResponseVO<GetGroupResp> getGroupInfo(GetGroupInfoReq req) {
        // 判读一下群是否合法
        ResponseVO<ImGroupEntity> group = this.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        // 准备返回的实体
        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtils.copyProperties(group.getData(), getGroupResp);
        try {
            // 返回的实体中也有成员，所以要查询成员信息
            ResponseVO<List<GroupMemberDto>> groupMember = imGroupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
            if (!groupMember.isOk()) {
                return ResponseVO.successResponse(getGroupResp);
            }
            // 查出来以后设置好即可
            getGroupResp.setMemberList(groupMember.getData());
        } catch (Exception e) {
            log.error("查询群信息失败，groupId:{}", req.getGroupId(), e);
        }
        return ResponseVO.successResponse(getGroupResp);
    }

    /**
     * 获取群信息
     */
    @Override
    public ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId) {
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, groupId);
        lqw.eq(ImGroupEntity::getAppId, appId);
        ImGroupEntity entity = imGroupMapper.selectOne(lqw);
        if (ObjectUtil.isNull(entity)) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    /**
     * 获取用户加入的群组
     */
    @Override
    public ResponseVO<List<ImGroupEntity>> getJoinedGroup(GetJoinedGroupReq req) {
        // 1、获取到所有加入的群的id
        ResponseVO<Collection<String>> memberJoinedGroup = imGroupMemberService.getMemberJoinedGroup(req);

        if (CollectionUtils.isEmpty(memberJoinedGroup.getData())) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_LIST_ERROR);
        }

        List<ImGroupEntity> list;

        // 2、根据群id获取群信息
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());

        lqw.in(ImGroupEntity::getGroupId, memberJoinedGroup.getData());
        if (CollectionUtils.isEmpty(req.getGroupType())) {
            lqw.in(ImGroupEntity::getGroupType, req.getGroupType());
        }
        list = imGroupMapper.selectList(lqw);

        return ResponseVO.successResponse(list);
    }

    /**
     * 解散群组
     */
    @Override
    public ResponseVO<String> destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;

        // 看看群组是否存在
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(lqw);

        if (ObjectUtil.isNull(imGroupEntity)) {
            return ResponseVO.errorResponse(GroupErrorCode.PRIVATE_GROUP_CAN_NOT_DESTROY);
        }

        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }

        if (!isAdmin) {
            if (ObjectUtil.equal(imGroupEntity.getGroupType(), GroupTypeEnum.PUBLIC.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }

            if (ObjectUtil.equal(imGroupEntity.getGroupType(), GroupTypeEnum.PUBLIC.getCode()) && ObjectUtil.notEqual(imGroupEntity.getOwnerId(), req.getOperator())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();

        // 也是软删除
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = imGroupMapper.update(update, lqw);

        if (update1 != 1) {
            return ResponseVO.errorResponse(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        //TODO TCP通知
        return ResponseVO.successResponse();
    }

    /**
     * 转让群主
     */
    @Override
    public ResponseVO<String> transferGroup(TransferGroupReq req) {
        // 先判断一下转让群主的用户是不是群主
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        if (ObjectUtil.notEqual(roleInGroupOne.getData().getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 安全检查一下 下一届的群主
        ResponseVO<GetRoleInGroupResp> newOwnerRole = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        // 查询一下该群组是否被解散了
        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        ImGroupEntity imGroupEntity = imGroupMapper.selectOne(lqw);

        if (ObjectUtil.equal(imGroupEntity.getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        int update = imGroupMapper.update(updateGroup, lqw);

        if (update != 1) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
        }

        imGroupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());

        return ResponseVO.successResponse();
    }

    /**
     * 群组禁言
     */
    @Override
    public ResponseVO<String> muteGroup(MuteGroupReq req) {
        // 安全检查群组
        ResponseVO<ImGroupEntity> groupResp = getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if (ObjectUtil.equal(groupResp.getData().getStatus(), GroupStatusEnum.DESTROY.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }

        boolean isadmin = false;

        if (!isadmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

            if (!role.isOk()) {
                return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode()) || ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());

            //公开群只能群主修改资料
            if (!isManager) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }
        ImGroupEntity update = new ImGroupEntity();
        update.setMute(req.getMute());

        LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupEntity::getAppId, req.getAppId());
        imGroupMapper.update(update, lqw);

        return ResponseVO.successResponse();
    }

    /**
     * 增量同步群组成员列表
     */
    @Override
    public ResponseVO<SyncResp<ImGroupEntity>> syncJoinedGroupList(SyncReq req) {
        // 单次拉取最大
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }

        SyncResp<ImGroupEntity> resp = new SyncResp<>();

        // 获取该用户加入的所有的群 的 groupId
        ResponseVO<Collection<String>> collectionResponseVO = imGroupMemberService.syncMemberJoinedGroup(req.getOperator(), req.getAppId());

        if (collectionResponseVO.isOk()) {
            Collection<String> data = collectionResponseVO.getData();
            LambdaQueryWrapper<ImGroupEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImGroupEntity::getAppId, req.getAppId());
            lqw.in(ImGroupEntity::getGroupId, data);
            lqw.gt(ImGroupEntity::getSequence, req.getLastSequence());
            lqw.last("limit " + req.getMaxLimit());
            lqw.orderByAsc(ImGroupEntity::getSequence);
            List<ImGroupEntity> list = imGroupMapper.selectList(lqw);

            if (!CollectionUtils.isEmpty(list)) {
                ImGroupEntity maxSeqEntity = list.get(list.size() - 1);
                resp.setDataList(list);
                // 设置最大seq
                Long maxSeq = imGroupMapper.getGroupMaxSeq(data, req.getAppId());
                resp.setMaxSequence(maxSeq);

                // 设置是否拉取完毕
                resp.setCompleted(maxSeqEntity.getSequence() >= maxSeq);
                return ResponseVO.successResponse(resp);
            }
        }
        resp.setCompleted(true);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 动态获取群组中最大的seq
     */
    @Override
    public Long getUserGroupMaxSeq(String userId, Integer appId) {
        // 该用户加入的groupId
        ResponseVO<Collection<String>> memberJoinedGroup = imGroupMemberService.syncMemberJoinedGroup(userId, appId);
        if (!memberJoinedGroup.isOk()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        // 获取他加入的群组列表中最大的seq
        return imGroupMapper.getGroupMaxSeq(memberJoinedGroup.getData(), appId);
    }
}
