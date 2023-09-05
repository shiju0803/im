/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sj.im.codec.pack.group.AddGroupMemberPack;
import com.sj.im.codec.pack.group.GroupMemberSpeakPack;
import com.sj.im.codec.pack.group.RemoveGroupMemberPack;
import com.sj.im.codec.pack.group.UpdateGroupMemberPack;
import com.sj.im.common.constant.CallbackCommandConstants;
import com.sj.im.common.enums.GroupMemberRoleEnum;
import com.sj.im.common.enums.GroupStatusEnum;
import com.sj.im.common.enums.GroupTypeEnum;
import com.sj.im.common.enums.command.GroupEventCommand;
import com.sj.im.common.enums.exception.GroupErrorCode;
import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.group.entry.ImGroupEntity;
import com.sj.im.service.group.entry.ImGroupMemberEntity;
import com.sj.im.service.group.mapper.ImGroupMemberMapper;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.group.web.callback.AddMemberAfterCallback;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.AddMemberResp;
import com.sj.im.service.group.web.resp.GetRoleInGroupResp;
import com.sj.im.service.helper.CallbackHelper;
import com.sj.im.service.helper.GroupMessageHelper;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 群组成员业务
 */
@Service
@Slf4j
public class ImGroupMemberServiceImpl implements ImGroupMemberService {
    @Resource
    private ImGroupService imGroupService;
    @Resource
    private ImGroupMemberService thisService;
    @Resource
    private ImUserService userService;
    @Resource
    private ImGroupMemberMapper imGroupMemberMapper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackHelper callBackHelper;
    @Resource
    private GroupMessageHelper groupMessageHelper;

    /**
     * 导入群组成员
     */
    @Override
    public List<AddMemberResp> importGroupMember(ImportGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        // 检验一下该群组是否存在
        imGroupService.getGroup(req.getGroupId(), req.getAppId());

        for (GroupMemberDto memberId : req.getMembers()) {
            AddMemberResp addMemberResp = new AddMemberResp();
            try {
                thisService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
                addMemberResp.setResult(0);
            } catch (Exception e) {
                log.error("群成员添加失败, memberId:{}", memberId.getMemberId(), e);
                if (ObjectUtil.equal(e.getMessage(), GroupErrorCode.USER_JOIN_GROUP_ERROR.getMsg())) {
                    addMemberResp.setResult(2);
                } else {
                    addMemberResp.setResult(1);
                }
            }
            addMemberResp.setMemberId(memberId.getMemberId());
            resp.add(addMemberResp);
        }
        return resp;
    }

    /**
     * 导入群组成员具体逻辑
     *
     * @param groupId 群组id
     * @param appId   appId
     * @param dto     群成员参数
     */
    @Override
    public void addGroupMember(String groupId, Integer appId, GroupMemberDto dto) {
        // 判断该成员是否合法
        ImUserDataEntity singleUserInfo = userService.getSingleUserInfo(dto.getMemberId(), appId);
        if (ObjectUtil.isNull(singleUserInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        // 判断群是否已经有群主了
        if (ObjectUtil.equal(dto.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
            LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImGroupMemberEntity::getGroupId, groupId);
            lqw.eq(ImGroupMemberEntity::getAppId, appId);
            lqw.eq(ImGroupMemberEntity::getRole, GroupMemberRoleEnum.OWNER.getCode());
            long count = imGroupMemberMapper.selectCount(lqw);
            if (count > 0) {
                throw new BusinessException(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }

        LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupMemberEntity::getGroupId, groupId);
        lqw.eq(ImGroupMemberEntity::getAppId, appId);
        lqw.eq(ImGroupMemberEntity::getMemberId, dto.getMemberId());
        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(lqw);

        Date now = new Date();
        if (ObjectUtil.isNull(imGroupMemberEntity)) {
            // 如果是空的话，就是首次加群
            imGroupMemberEntity = new ImGroupMemberEntity();
            BeanUtil.copyProperties(dto, imGroupMemberEntity);
            imGroupMemberEntity.setGroupId(groupId);
            imGroupMemberEntity.setAppId(appId);
            imGroupMemberEntity.setJoinTime(now);
            int insert = imGroupMemberMapper.insert(imGroupMemberEntity);
            if (insert != 1) {
                throw new BusinessException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
        } else if (ObjectUtil.equal(GroupMemberRoleEnum.LEAVE.getCode(), imGroupMemberEntity.getRole())) {
            // 如果不为空且Role是3离开的话就是再次进入群
            imGroupMemberEntity = new ImGroupMemberEntity();
            BeanUtil.copyProperties(dto, imGroupMemberEntity);
            imGroupMemberEntity.setJoinTime(now);
            int update = imGroupMemberMapper.update(imGroupMemberEntity, lqw);
            if (update != 1) {
                throw new BusinessException(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
        }
        throw new BusinessException(GroupErrorCode.USER_IS_JOINED_GROUP);
    }

    /**
     * 获得群组中某成员的成员类型
     *
     * @param groupId  群id
     * @param memberId 成员id
     * @param appId    appId
     */
    @Override
    public GetRoleInGroupResp getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();

        LambdaQueryWrapper<ImGroupMemberEntity> queryOwner = new LambdaQueryWrapper<>();
        queryOwner.eq(ImGroupMemberEntity::getGroupId, groupId);
        queryOwner.eq(ImGroupMemberEntity::getAppId, appId);
        queryOwner.eq(ImGroupMemberEntity::getMemberId, memberId);

        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(queryOwner);
        if (ObjectUtil.isNull(imGroupMemberEntity) || ObjectUtil.equal(imGroupMemberEntity.getRole(), GroupMemberRoleEnum.LEAVE.getCode())) {
            throw new BusinessException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());

        return resp;
    }

    /**
     * 获取群组中的所有成员信息
     *
     * @param groupId 群id
     * @param appId   appId
     */
    @Override
    public List<GroupMemberDto> getGroupMember(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupMember(appId, groupId);
    }

    /**
     * 获取到指定用户加入的所有群的id
     */
    @Override
    public List<String> getMemberJoinedGroup(GetJoinedGroupReq req) {
        return imGroupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId());
    }

    /**
     * 转让群主
     *
     * @param owner   群主id
     * @param groupId 群id
     * @param appId   appId
     */
    @Override
    @Transactional
    public void transferGroupMember(String owner, String groupId, Integer appId) {
        //更新旧群主
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        LambdaUpdateWrapper<ImGroupMemberEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ImGroupMemberEntity::getAppId, appId);
        updateWrapper.eq(ImGroupMemberEntity::getGroupId, groupId);
        updateWrapper.eq(ImGroupMemberEntity::getRole, GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMemberEntity, updateWrapper);

        //更新新群主
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        LambdaUpdateWrapper<ImGroupMemberEntity> ownerWrapper = new LambdaUpdateWrapper<>();
        ownerWrapper.eq(ImGroupMemberEntity::getAppId, appId);
        ownerWrapper.eq(ImGroupMemberEntity::getGroupId, groupId);
        ownerWrapper.eq(ImGroupMemberEntity::getMemberId, owner);
        imGroupMemberMapper.update(newOwner, ownerWrapper);
    }

    /**
     * 添加群成员，拉人入群的逻辑，直接进入群聊。如果是后台管理员，则直接拉入群，
     * 否则只有私有群可以调用本接口，并且群成员也可以拉人入群.只有私有群可以调用本接口
     */
    @Override
    public List<AddMemberResp> addMember(AddGroupMemberReq req) {
        boolean isAdmin = false;
        // 先判断群组是否存在
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());

        List<GroupMemberDto> memberDtos = req.getMembers();
        if (appConfig.isAddGroupMemberBeforeCallback()) {
            ResponseVO responseVO = callBackHelper.beforeCallback(req.getAppId(), CallbackCommandConstants.GROUP_MEMBER_ADD_BEFORE,
                    JSONUtil.toJsonStr(req));
            if (!responseVO.isOk()) {
                throw new BusinessException(responseVO.getMsg());
            }

            try {
                memberDtos = JSONUtil.toList(JSONUtil.toJsonStr(responseVO.getData()), GroupMemberDto.class);
            } catch (Exception e) {
                log.error("GroupMemberAddBefore 回调失败：{}, msg:{}", req.getAppId(), e.getMessage());
            }
        }

        /**
         * 私有群（private） 类似普通微信群，创建后支持已在群内的好友邀请加群，且无需群主审批
         * 公开群（public） 类似QQ群，创建后群主可以指定群管理员，需要群主或管理员审核
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         */
        if (!isAdmin && ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), group.getGroupType())) {
            throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        List<AddMemberResp> resp = new ArrayList<>();
        // 成功的
        List<String> successId = new ArrayList<>();
        AddMemberResp addMemberResp = new AddMemberResp();
        for (GroupMemberDto memberId : memberDtos) {
            try {
                // 添加组成员
                thisService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
                successId.add(memberId.getMemberId());
                addMemberResp.setResult(0);
            } catch (Exception e) {
                log.error("添加组成员失败, memberId:{}", memberId.getMemberId(), e);
                if (ObjectUtil.equal(e.getMessage(), GroupErrorCode.USER_IS_JOINED_GROUP.getMsg())) {
                    addMemberResp.setResult(2);
                } else {
                    addMemberResp.setResult(1);
                }
            }
            addMemberResp.setMemberId(memberId.getMemberId());
            resp.add(addMemberResp);
        }

        // 回调
        if (appConfig.isAddGroupMemberAfterCallback()) {
            AddMemberAfterCallback callbackDto = new AddMemberAfterCallback();
            callbackDto.setGroupId(req.getGroupId());
            callbackDto.setGroupType(group.getGroupType());
            callbackDto.setMemberId(resp);
            callbackDto.setOperator(req.getOperator());
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.GROUP_MEMBER_ADD_AFTER,
                    JSONUtil.toJsonStr(callbackDto));
        }

        // TCP通知
        AddGroupMemberPack addGroupMemberPack = new AddGroupMemberPack();
        addGroupMemberPack.setGroupId(req.getGroupId());
        addGroupMemberPack.setMembers(successId);
        groupMessageHelper.producer(req.getOperator(), GroupEventCommand.ADDED_MEMBER, addGroupMemberPack
                , new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return resp;
    }

    /**
     * 踢人出群
     */
    @Override
    public void removeMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;
        // 判断群是否存在
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!isAdmin) {
            // 获取操作人的权限 是管理员or群主or群成员
            GetRoleInGroupResp role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            Integer roleInfo = role.getRole();

            boolean isOwner = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());
            boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode());

            if (!isOwner && !isManager) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            // 私有群必须是群主才能踢人
            if (!isOwner && ObjectUtil.equal(group.getGroupType(), GroupTypeEnum.PRIVATE.getCode())) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }

            // 公开群管理员和群主可踢人，但管理员只能踢普通群成员
            if (ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), group.getGroupType())) {
                // 获取被踢人的权限
                GetRoleInGroupResp memberRole = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if (ObjectUtil.equal(memberRole.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
                    throw new BusinessException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                }
                //是管理员并且被踢人不是群成员，无法操作
                if (isManager && ObjectUtil.notEqual(memberRole.getRole(), GroupMemberRoleEnum.ORDINARY.getCode())) {
                    throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }
        }
        thisService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());
        // TCP通知
        RemoveGroupMemberPack removeGroupMemberPack = new RemoveGroupMemberPack();
        removeGroupMemberPack.setGroupId(req.getGroupId());
        removeGroupMemberPack.setMember(req.getMemberId());
        groupMessageHelper.producer(req.getMemberId(), GroupEventCommand.DELETED_MEMBER, removeGroupMemberPack
                , new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        // 回调
        if (appConfig.isDeleteGroupMemberAfterCallback()) {
            callBackHelper.callback(req.getAppId(), CallbackCommandConstants.GROUP_MEMBER_DELETE_AFTER, JSONUtil.toJsonStr(req));
        }
    }

    /**
     * 踢人出群具体逻辑
     *
     * @param groupId  群id
     * @param appId    appId
     * @param memberId 成员id
     */
    @Override
    public void removeGroupMember(String groupId, Integer appId, String memberId) {
        // 判断被踢人是否合法
        ImUserDataEntity singleUserInfo = userService.getSingleUserInfo(memberId, appId);
        if (ObjectUtil.isNull(singleUserInfo)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 获取被踢人的信息
        GetRoleInGroupResp roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);

        // 踢人
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(new Date());
        imGroupMemberEntity.setGroupMemberId(roleInGroupOne.getGroupMemberId());
        imGroupMemberMapper.updateById(imGroupMemberEntity);
    }

    /**
     * 退出群聊
     */
    @Override
    public void exitGroup(ExitGroupReq req) {
        // 安全检查
        imGroupService.getGroup(req.getGroupId(), req.getAppId());
        getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

        ImGroupMemberEntity update = new ImGroupMemberEntity();
        update.setRole(GroupMemberRoleEnum.LEAVE.getCode());

        LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupMemberEntity::getAppId, req.getAppId());
        lqw.eq(ImGroupMemberEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupMemberEntity::getMemberId, req.getOperator());
        imGroupMemberMapper.update(update, lqw);
    }

    /**
     * 修改群成员信息
     */
    @Override
    public void updateGroupMember(UpdateGroupMemberReq req) {
        boolean isAdmin = false;

        // 安全检查该群成员
        ImGroupEntity group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        // 安全检查该群组
        if (group.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new BusinessException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = ObjectUtil.equal(req.getOperator(), req.getMemberId());
        if (!isAdmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (CharSequenceUtil.isBlank(req.getAlias()) && !isMeOperate) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }

            // 如果要修改权限相关的则走下面的逻辑
            if (ObjectUtil.isNotNull(req.getRole())) {
                // 私有群不能设置管理员
                if (ObjectUtil.equal(group.getGroupType(), GroupTypeEnum.PRIVATE.getCode()) && ObjectUtil.isNotNull(req.getRole())
                        && (ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.MANAGER.getCode()) ||
                        ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.OWNER.getCode()))) {
                    throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
                }

                //获取被操作人的是否在群内
                getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                //获取操作人权限
                GetRoleInGroupResp operateRoleInGroupOne = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                Integer roleInfo = operateRoleInGroupOne.getRole();
                boolean isOwner = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());
                boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode());

                //不是管理员不能修改权限
                if (ObjectUtil.isNotNull(req.getRole()) && !isOwner && !isManager) {
                    throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //管理员只有群主能够设置
                if (ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.MANAGER.getCode()) && !isOwner) {
                    throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }
        }
        ImGroupMemberEntity update = new ImGroupMemberEntity();

        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }

        // 不能直接修改为群主
        if (ObjectUtil.notEqual(req.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
            update.setRole(req.getRole());
        }

        LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupMemberEntity::getAppId, req.getAppId());
        lqw.eq(ImGroupMemberEntity::getMemberId, req.getMemberId());
        lqw.eq(ImGroupMemberEntity::getGroupId, req.getGroupId());
        imGroupMemberMapper.update(update, lqw);

        // TCP通知
        UpdateGroupMemberPack pack = new UpdateGroupMemberPack();
        BeanUtils.copyProperties(req, pack);
        groupMessageHelper.producer(req.getOperator(), GroupEventCommand.UPDATED_MEMBER,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
    }

    /**
     * 禁言群成员
     */
    @Override
    public void speak(SpeakMemberReq req) {
        // 判断群组是否合法
        imGroupService.getGroup(req.getGroupId(), req.getAppId());

        boolean isAdmin = false;
        boolean isOwner;
        boolean isManager;
        GetRoleInGroupResp memberRole = null;
        if (!isAdmin) {
            //获取操作人的权限 是管理员or群主or群成员
            GetRoleInGroupResp role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            Integer roleInfo = role.getRole();

            isOwner = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());
            isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode());

            if (!isOwner && !isManager) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            memberRole = getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            //被操作人是群主只能app管理员操作
            if (ObjectUtil.equal(memberRole.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
            }

            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && ObjectUtil.notEqual(memberRole.getRole(), GroupMemberRoleEnum.ORDINARY.getCode())) {
                throw new BusinessException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        if (ObjectUtil.isNull(memberRole)) {
            //获取被操作的权限
            memberRole = getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
        }

        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }

        int i = imGroupMemberMapper.updateById(imGroupMemberEntity);
        if (i == 1) {
            GroupMemberSpeakPack pack = new GroupMemberSpeakPack();
            BeanUtils.copyProperties(req, pack);
            groupMessageHelper.producer(req.getOperator(), GroupEventCommand.SPEAK_GROUP_MEMBER, pack,
                    new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }
    }

    /**
     * 获取指定群组的所有的成员Id
     *
     * @param groupId 群id
     * @param appId   appId
     */
    @Override
    public List<String> getGroupMemberId(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupMemberId(appId, groupId);
    }

    /**
     * 获取群组中的管理员具体信息
     *
     * @param groupId 群id
     * @param appId   appId
     */
    @Override
    public List<GroupMemberDto> getGroupManager(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupManager(groupId, appId);
    }

    /**
     * 增量同步用户加入的群组
     *
     * @param operator
     * @param appId
     */
    @Override
    public List<String> syncMemberJoinedGroup(String operator, Integer appId) {
        return imGroupMemberMapper.syncJoinedGroupId(appId, operator, GroupMemberRoleEnum.LEAVE.getCode());
    }
}
