/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sj.im.codec.pack.user.UserModifyPack;
import com.sj.im.common.constant.CallbackCommandConstants;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.SeqConstants;
import com.sj.im.common.enums.DelFlagEnum;
import com.sj.im.common.enums.command.UserEventCommand;
import com.sj.im.common.enums.exception.UserErrorCode;
import com.sj.im.common.exception.BusinessException;
import com.sj.im.common.route.RouteHandle;
import com.sj.im.common.route.RouteInfo;
import com.sj.im.common.util.RouteInfoParseUtil;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.group.service.ImGroupService;
import com.sj.im.service.helper.CallbackHelper;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.helper.ZookeeperHelper;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.mapper.ImUserDataMapper;
import com.sj.im.service.user.service.ImUserService;
import com.sj.im.service.user.web.req.*;
import com.sj.im.service.user.web.resp.GetUserInfoResp;
import com.sj.im.service.user.web.resp.ImportUserResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户相关接口实现类
 */
@Service
@Slf4j
public class ImUserServiceImpl extends ServiceImpl<ImUserDataMapper, ImUserDataEntity> implements ImUserService {
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackHelper callBackHelper;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ZookeeperHelper zookeeperHelper;
    @Resource
    private RouteHandle routeHandle;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ImGroupService imGroupService;

    // 导入用户
    @Override
    public ImportUserResp importUser(ImportUserReq req) {
        // 导入用户数量的限制
        if (req.getUserData().size() > 100) {
            throw new BusinessException(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        // 这个要返回给客户，导入成功的和导入失败的
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        // 遍历然后填充成功和失败列表
        for (ImUserDataEntity user : req.getUserData()) {
            try {
                user.setAppId(req.getAppId());
                user.setCreateTime(new Date());
                boolean insert = save(user);
                if (insert) {
                    successId.add(user.getUserId());
                } else {
                    errorId.add(user.getUserId());
                }
            } catch (Exception e) {
                log.error("导入用户失败", e);
                errorId.add(user.getUserId());
            }
        }

        // 定义返回的类
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return resp;
    }

    @Override
    public GetUserInfoResp getUserInfo(GetUserInfoReq req) {
        // 查询用户的条件
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getAppId, req.getAppId());
        qw.in(ImUserDataEntity::getUserId, req.getUserIds());
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        // 查询出符合条件的用户
        List<ImUserDataEntity> imUserDataEntities = list(qw);

        // 过滤出查询失败的用户一并返回
        Map<String, ImUserDataEntity> map = imUserDataEntities.stream().collect(Collectors.toMap(ImUserDataEntity::getUserId, entity -> entity));
        // 通过Map的containsKey来判断没有在查询成功中的用户，将他们添加到查询失败的用户中去
        List<String> failUser = new ArrayList<>();
        for (String userId : req.getUserIds()) {
            if (!map.containsKey(userId)) {
                failUser.add(userId);
            }
        }
        // 包装Resp
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(imUserDataEntities);
        resp.setFailUser(failUser);
        return resp;
    }

    /**
     * 获取单个用户信息
     *
     * @param userId 用户id
     * @param appId  appId
     * @return ImUserDataEntity 用户信息
     */
    @Override
    public ImUserDataEntity getSingleUserInfo(String userId, Integer appId) {
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getUserId, userId);
        qw.eq(ImUserDataEntity::getAppId, appId);
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        return getOne(qw);
    }

    /**
     * 批量删除用户信息
     *
     * @param req 要删除的用户id
     * @return 操作结果
     */
    @Override
    public ImportUserResp deleteUser(DeleteUserReq req) {
        // 这里的删除都是逻辑删除，也就是把那个DelFlag变成1就成了删除
        // ，不用delete语句，使用的是update语句，所以下面这个就是为了update使用的
        ImUserDataEntity data = new ImUserDataEntity();
        data.setDelFlag(DelFlagEnum.DELETE.getCode());

        // 删除成功和失败的情况，返回给客户端
        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId : req.getUserIds()) {
            // 构建一个update的条件
            LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(ImUserDataEntity::getAppId, req.getAppId());
            qw.eq(ImUserDataEntity::getUserId, userId);
            qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
            // try catch用的很好，思维缜密
            try {
                boolean update = update(data, qw);
                // 分堆
                if (update) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);
            }
        }
        // 包装一下Resp
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);

        return resp;
    }

    /**
     * 修改用户信息
     *
     * @param req 修改的用户信息
     * @return 操作结果
     */
    @Override
    @Transactional
    public void modifyUserInfo(ModifyUserInfoReq req) {
        // 先查询要修改的用户是否合法
        LambdaQueryWrapper<ImUserDataEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ImUserDataEntity::getAppId, req.getAppId());
        qw.eq(ImUserDataEntity::getUserId, req.getUserId());
        qw.eq(ImUserDataEntity::getDelFlag, DelFlagEnum.NORMAL.getCode());
        // 查询用户数据
        ImUserDataEntity data = getOne(qw);
        // 如果用户不存在，则抛出业务异常
        if (ObjectUtil.isNull(data)) {
            throw new BusinessException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        // 构建更新数据对象
        ImUserDataEntity update = BeanUtil.toBean(req, ImUserDataEntity.class);
        // 这里不需要修改AppId和userId，如果要修改这两个的话，还不如新建一个用户
        update.setAppId(null);
        update.setUserId(null);
        // 执行更新操作
        boolean update1 = update(update, qw);
        // 如果更新成功，则发送用户修改事件和回调请求
        if (update1) {
            // TCP通知其他端 构建用户修改事件对象
            UserModifyPack pack = BeanUtil.toBean(req, UserModifyPack.class);
            // 发送用户修改事件
            messageHelper.sendToUser(req.getUserId(), req.getClientType(), req.getImei(),
                    UserEventCommand.USER_MODIFY, pack, req.getAppId());

            // 如果开启了修改用户信息后回调功能，则发送回调请求
            if (appConfig.isModifyUserAfterCallback()) {
                callBackHelper.callback(req.getAppId(), CallbackCommandConstants.MODIFY_USER_AFTER, JSONUtil.toJsonStr(req));
            }
            return;
        }
        // 如果更新失败，则抛出业务异常
        throw new BusinessException(UserErrorCode.MODIFY_USER_ERROR);
    }

    /**
     * 用户登录
     *
     * @param req 登录参数
     * @return 操作结果
     */
    @Override
    public RouteInfo login(LoginReq req) {
        // 登录验证
        boolean isOk = true;
        // 登录验证成功
        if (isOk) {
            // 获取所有可用的服务器节点 判断客户端类型，获取相应类型的服务器节点
            List<String> allNode = zookeeperHelper.getAllNodeByClientType(req.getClientType());

            // 根据用户ID进行服务器路由选择
            String address = routeHandle.routeServer(allNode, req.getUserId());
            // 返回解析出的服务器路由信息
            return RouteInfoParseUtil.parse(address);
        } else {
            throw new BusinessException(500, "登录失败");
        }
    }

    /**
     * 获取用户序列
     *
     * @param req 用户信息
     * @return 用户最大的序列号
     */
    @Override
    public Map<Object, Object> getUserSequence(GetUserSequenceReq req) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + RedisConstants.SEQ_PREFIX + ":" + req.getUserId());
        Long groupSeq = imGroupService.getUserGroupMaxSeq(req.getUserId(), req.getAppId());
        map.put(SeqConstants.GROUP_SEQ, groupSeq);
        return map;
    }
}
