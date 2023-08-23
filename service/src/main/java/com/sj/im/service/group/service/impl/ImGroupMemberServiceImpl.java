/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sj.im.common.enums.*;
import com.sj.im.common.exception.ApplicationException;
import com.sj.im.service.group.dao.ImGroupEntity;
import com.sj.im.service.group.dao.ImGroupMemberEntity;
import com.sj.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.model.resp.AddMemberResp;
import com.sj.im.service.group.model.resp.GetRoleInGroupResp;
import com.sj.im.service.group.service.ImGroupMemberService;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private ImUserService userService;
    @Resource
    private ImGroupMemberMapper imGroupMemberMapper;

    /**
     * 导入群组成员
     */
    @Override
    public ResponseVO<List<AddMemberResp>> importGroupMember(ImportGroupMemberReq req) {
        // 检验一下该群组是否存在
        ResponseVO<ImGroupEntity> group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        List<AddMemberResp> resp = new ArrayList<>();
        for (GroupMemberDto memberId : req.getMembers()) {
            ResponseVO<String> responseVO;
            try {
                responseVO = imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                log.error("群成员添加失败, memberId:{}", memberId.getMemberId(), e);
                responseVO = ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                addMemberResp.setResult(0);
            } else if (ObjectUtil.equal(responseVO.getCode(), GroupErrorCode.USER_JOIN_GROUP_ERROR.getCode())) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            resp.add(addMemberResp);
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 导入群组成员具体逻辑
     *
     * @param groupId 群组id
     * @param appId   appId
     * @param dto     群成员参数
     */
    @Override
    @Transactional
    public ResponseVO<String> addGroupMember(String groupId, Integer appId, GroupMemberDto dto) {
        // 判断该成员是否合法
        ResponseVO<ImUserDataEntity> singleUserInfo = userService.getSingleUserInfo(dto.getMemberId(), appId);
        if (!singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        // 判断群是否已经有群主了
        if (ObjectUtil.equal(dto.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
            LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ImGroupMemberEntity::getGroupId, groupId);
            lqw.eq(ImGroupMemberEntity::getAppId, appId);
            lqw.eq(ImGroupMemberEntity::getRole, GroupMemberRoleEnum.OWNER.getCode());
            Long count = imGroupMemberMapper.selectCount(lqw);
            if (count > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
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
            BeanUtils.copyProperties(dto, imGroupMemberEntity);
            imGroupMemberEntity.setGroupId(groupId);
            imGroupMemberEntity.setAppId(appId);
            imGroupMemberEntity.setJoinTime(now);
            int insert = imGroupMemberMapper.insert(imGroupMemberEntity);
            if (insert != 1) {
                return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
            return ResponseVO.successResponse();
        } else if (ObjectUtil.equal(GroupMemberRoleEnum.LEAVE.getCode(), imGroupMemberEntity.getRole())) {
            // 如果不为空且Role是3离开的话就是再次进入群
            imGroupMemberEntity = new ImGroupMemberEntity();
            BeanUtils.copyProperties(dto, imGroupMemberEntity);
            imGroupMemberEntity.setJoinTime(now);
            int insert = imGroupMemberMapper.insert(imGroupMemberEntity);
            if (insert != 1) {
                return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
    }

    /**
     * 获得群组中某成员的成员类型
     *
     * @param groupId  群id
     * @param memberId 成员id
     * @param appId    appId
     */
    @Override
    public ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();

        LambdaQueryWrapper<ImGroupMemberEntity> queryOwner = new LambdaQueryWrapper<>();
        queryOwner.eq(ImGroupMemberEntity::getGroupId, groupId);
        queryOwner.eq(ImGroupMemberEntity::getAppId, appId);
        queryOwner.eq(ImGroupMemberEntity::getMemberId, memberId);

        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(queryOwner);
        if (ObjectUtil.isNull(imGroupMemberEntity) || ObjectUtil.equal(imGroupMemberEntity.getRole(), GroupMemberRoleEnum.LEAVE.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());

        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取群组中的所有成员信息
     *
     * @param groupId 群id
     * @param appId   appId
     */
    @Override
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDto> groupMember = imGroupMemberMapper.getGroupMember(appId, groupId);
        return ResponseVO.successResponse(groupMember);
    }

    /**
     * 获取到指定用户加入的所有群的id
     */
    @Override
    public ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req) {
        return ResponseVO.successResponse(imGroupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
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
    public ResponseVO<String> transferGroupMember(String owner, String groupId, Integer appId) {
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

        return ResponseVO.successResponse("群主转让成功");
    }

    /**
     * 拉人入群
     */
    @Override
    public ResponseVO<Object> addMember(AddGroupMemberReq req) {
        boolean isAdmin = false;
        // 先判断群组是否存在
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        List<GroupMemberDto> memberDtos = req.getMembers();

        List<AddMemberResp> resp = new ArrayList<>();

        ImGroupEntity group = groupResp.getData();

        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
        }

        // 成功的
        List<String> successId = new ArrayList<>();
        for (GroupMemberDto memberId : memberDtos) {
            ResponseVO<String> responseVO;
            try {
                // 添加组成员
                responseVO = imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                log.error("添加组成员失败, memberId:{}", memberId.getMemberId(), e);
                responseVO = ResponseVO.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                successId.add(memberId.getMemberId());
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
                addMemberResp.setResultMessage(responseVO.getMsg());
            } else {
                addMemberResp.setResult(1);
                addMemberResp.setResultMessage(responseVO.getMsg());
            }
            resp.add(addMemberResp);
        }

        //TODO TCP通知

        //TODO 回调

        return ResponseVO.successResponse(resp);
    }

    /**
     * 踢人出群
     */
    @Override
    public ResponseVO<String> removeMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;

        // 判断群是否存在
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        ImGroupEntity group = groupResp.getData();
        if (!isAdmin) {
            if (ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), group.getGroupType())) {
                //获取操作人的权限 是管理员or群主or群成员
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!role.isOk()) {
                    return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                }

                GetRoleInGroupResp data = role.getData();
                Integer roleInfo = data.getRole();

                boolean isOwner = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());
                boolean isManager = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.MANAGER.getCode());

                if (!isOwner && !isManager) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //私有群必须是群主才能踢人
                if (!isOwner && ObjectUtil.equal(group.getGroupType(), GroupTypeEnum.PRIVATE.getCode())) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                //公开群管理员和群主可踢人，但管理员只能踢普通群成员
                if (ObjectUtil.equal(GroupTypeEnum.PUBLIC.getCode(), group.getGroupType())) {
                    //获取被踢人的权限
                    ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                    if (!roleInGroupOne.isOk()) {
                        return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                    }
                    GetRoleInGroupResp memberRole = roleInGroupOne.getData();
                    if (ObjectUtil.equal(memberRole.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
                        return ResponseVO.errorResponse(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    //是管理员并且被踢人不是群成员，无法操作
                    if (isManager && ObjectUtil.notEqual(memberRole.getRole(), GroupMemberRoleEnum.ORDINARY.getCode())) {
                        return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }
            } else {
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!role.isOk()) {
                    return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                }

                GetRoleInGroupResp data = role.getData();
                Integer roleInfo = data.getRole();

                boolean isOwner = ObjectUtil.equal(roleInfo, GroupMemberRoleEnum.OWNER.getCode());

                if (!isOwner && ObjectUtil.equal(GroupTypeEnum.PRIVATE.getCode(), group.getGroupType())) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }
        }
        ResponseVO<String> responseVO = imGroupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());
        if (responseVO.isOk()) {
            //TODO TCP通知

            //TODO 回调
        }
        return responseVO;
    }

    /**
     * 踢人出群具体逻辑
     *
     * @param groupId  群id
     * @param appId    appId
     * @param memberId 成员id
     */
    @Override
    public ResponseVO<String> removeGroupMember(String groupId, Integer appId, String memberId) {
        // 判断被踢人是否合法
        ResponseVO<ImUserDataEntity> singleUserInfo = userService.getSingleUserInfo(memberId, appId);
        if (!singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 获取被踢人的信息
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        GetRoleInGroupResp data = roleInGroupOne.getData();

        // 踢人
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(new Date());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        imGroupMemberMapper.updateById(imGroupMemberEntity);

        return ResponseVO.successResponse();
    }

    /**
     * 退出群聊
     */
    @Override
    public ResponseVO<String> exitGroup(ExitGroupReq req) {
        // 安全检查
        ResponseVO<ImGroupEntity> group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }

        ImGroupMemberEntity update = new ImGroupMemberEntity();
        update.setRole(GroupMemberRoleEnum.LEAVE.getCode());

        LambdaQueryWrapper<ImGroupMemberEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ImGroupMemberEntity::getAppId, req.getAppId());
        lqw.eq(ImGroupMemberEntity::getGroupId, req.getGroupId());
        lqw.eq(ImGroupMemberEntity::getMemberId, req.getOperator());
        imGroupMemberMapper.update(update, lqw);

        return ResponseVO.successResponse();
    }

    /**
     * 修改群成员信息
     */
    @Override
    public ResponseVO<String> updateGroupMember(UpdateGroupMemberReq req) {
        boolean isAdmin = false;

        // 安全检查该群成员
        ResponseVO<ImGroupEntity> group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        // 安全检查该群组
        ImGroupEntity groupData = group.getData();
        if (groupData.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = ObjectUtil.equal(req.getOperator(), req.getMemberId());

        if (!isAdmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (CharSequenceUtil.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }

            // 如果要修改权限相关的则走下面的逻辑
            if (ObjectUtil.isNotNull(req.getRole())) {
                // 私有群不能设置管理员
                if (ObjectUtil.equal(groupData.getGroupType(), GroupTypeEnum.PRIVATE.getCode()) && ObjectUtil.isNotNull(req.getRole()) && (ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.MANAGER.getCode()) || ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.OWNER.getCode()))) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
                }

                //获取被操作人的是否在群内
                ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if (!roleInGroupOne.isOk()) {
                    return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                }

                //获取操作人权限
                ResponseVO<GetRoleInGroupResp> operateRoleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!operateRoleInGroupOne.isOk()) {
                    return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
                }

                GetRoleInGroupResp data = operateRoleInGroupOne.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

                //不是管理员不能修改权限
                if (ObjectUtil.isNotNull(req.getRole()) && !isOwner && !isManager) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //管理员只有群主能够设置
                if (ObjectUtil.equal(req.getRole(), GroupMemberRoleEnum.MANAGER.getCode()) && !isOwner) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
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

        //TODO TCP通知

        return ResponseVO.successResponse();
    }

    /**
     * 禁言群成员
     *
     * @param req
     */
    @Override
    public ResponseVO<String> speak(SpeakMemberReq req) {
        // 判断群组是否合法
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        boolean isAdmin = false;
        boolean isOwner = false;
        boolean isManager = false;
        GetRoleInGroupResp memberRole = null;

        if (!isAdmin) {
            //获取操作人的权限 是管理员or群主or群成员
            ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            if (!isOwner && !isManager) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }

            memberRole = roleInGroupOne.getData();
            //被操作人是群主只能app管理员操作
            if (ObjectUtil.equal(memberRole.getRole(), GroupMemberRoleEnum.OWNER.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_APP_MANAGER_ROLE);
            }

            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && ObjectUtil.notEqual(memberRole.getRole(), GroupMemberRoleEnum.ORDINARY.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        if (ObjectUtil.isNull(memberRole)) {
            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
            }
            memberRole = roleInGroupOne.getData();
        }

        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());

        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(DateUtil.offsetMinute(new Date(), req.getSpeakDate()));
        }

        int i = imGroupMemberMapper.updateById(imGroupMemberEntity);

        return ResponseVO.successResponse();
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
    public ResponseVO<Collection<String>> syncMemberJoinedGroup(String operator, Integer appId) {
        return ResponseVO.successResponse(imGroupMemberMapper.syncJoinedGroupId(appId, operator, GroupMemberRoleEnum.LEAVE.getCode()));
    }
}
