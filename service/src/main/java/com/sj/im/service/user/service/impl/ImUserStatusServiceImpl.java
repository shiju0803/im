/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.user.UserCustomStatusChangeNotifyPack;
import com.sj.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.command.Command;
import com.sj.im.common.enums.command.UserEventCommand;
import com.sj.im.common.model.ClientInfo;
import com.sj.im.common.model.UserSession;
import com.sj.im.service.friendship.service.ImFriendShipService;
import com.sj.im.service.helper.MessageHelper;
import com.sj.im.service.helper.UserSessionHelper;
import com.sj.im.service.user.service.ImUserStatusService;
import com.sj.im.service.user.web.req.PullFriendOnlineStatusReq;
import com.sj.im.service.user.web.req.PullUserOnlineStatusReq;
import com.sj.im.service.user.web.req.SetUserCustomerStatusReq;
import com.sj.im.service.user.web.req.SubscribeUserOnlineStatusReq;
import com.sj.im.service.user.web.resp.UserOnlineStatusResp;
import com.sj.im.service.user.web.resp.UserStatusChangeNotifyContent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户在线状态相关接口实现类
 */
@Service
public class ImUserStatusServiceImpl implements ImUserStatusService {

    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private MessageHelper messageHelper;
    @Resource
    private ImFriendShipService imFriendShipService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理用户在线状态通知
     *
     * @param content 用户在线状态变更通知内容
     */
    @Override
    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content) {
        List<UserSession> userSessionList = userSessionHelper.getUserSessionList(content.getAppId(), content.getUserId());
        UserStatusChangeNotifyPack notifyPack = BeanUtil.toBean(content, UserStatusChangeNotifyPack.class);
        notifyPack.setClient(userSessionList);
        // 发送给自己的同步端
        syncSender(notifyPack, content.getUserId(), content, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC);
        // 通知好友和订阅了自己的人
        dispatchSender(notifyPack, content.getUserId(), content.getAppId(), UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY);
    }

    private void syncSender(Object pack, String userId, ClientInfo clientInfo, Command command) {
        messageHelper.sendToUserExceptClient(userId, command, pack, clientInfo);
    }

    private void dispatchSender(Object pack, String userId, Integer appId, Command command) {
        // 获取所有好友id
        List<String> friendIds = imFriendShipService.getAllFriendId(userId, appId);
        for (String friendId : friendIds) {
            messageHelper.sendToUser(friendId, command, pack, appId);
        }

        // 通知临时订阅了自己的人
        String userKey = appId + ":" + RedisConstants.SUBSCRIBE + ":" + userId;
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(userKey);
        for (Object key : keys) {
            String filed = String.valueOf(key);
            long expire = Long.parseLong(String.valueOf(stringRedisTemplate.opsForHash().get(userKey, filed)));
            if (expire > 0 && expire > System.currentTimeMillis()) {
                messageHelper.sendToUser(filed, command, pack, appId);
            } else {
                stringRedisTemplate.opsForHash().delete(userKey, filed);
            }
        }
    }

    /**
     * 订阅用户在线状态
     *
     * @param req 订阅用户在线状态请求
     */
    @Override
    public void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req) {
        // 计算订阅过期时间
        long subExpireTime = 0L;
        if (ObjectUtil.isNotNull(req) && req.getSubTime() > 0) {
            subExpireTime = System.currentTimeMillis() + req.getSubTime();
        }
        // 遍历订阅的用户ID列表，并将订阅信息存储到Redis中
        for (String beSubUserId : req.getSubUserId()) {
            String userKey = req.getAppId() + ":" + RedisConstants.SUBSCRIBE + ":" + beSubUserId;
            stringRedisTemplate.opsForHash().put(userKey, req.getOperator(), Long.toString(subExpireTime));
        }
    }

    /**
     * 设置用户客服状态
     *
     * @param req 设置用户客服状态请求
     */
    @Override
    public void setUserCustomerStatus(SetUserCustomerStatusReq req) {
        String key = req.getAppId() + ":" + RedisConstants.USER_CUSTOMER_STATUS + ":" + req.getUserId();
        UserCustomStatusChangeNotifyPack notifyPack = BeanUtil.toBean(req, UserCustomStatusChangeNotifyPack.class);
        // 在Redis中设置用户客户端状态信息
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(notifyPack));

        // 发送用户状态变更通知给其他端
        syncSender(notifyPack, req.getUserId(), new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()), UserEventCommand.USER_ONLINE_STATUS_SET_CHANGE_NOTIFY_SYNC);

        // 分发用户状态变更通知给好友
        dispatchSender(notifyPack, req.getUserId(), req.getAppId(), UserEventCommand.USER_ONLINE_STATUS_SET_CHANGE_NOTIFY);
    }

    /**
     * 查询好友在线状态
     *
     * @param req 查询好友在线状态请求
     * @return 好友在线状态映射
     */
    @Override
    public Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req) {
        List<String> friendId = imFriendShipService.getAllFriendId(req.getOperator(), req.getAppId());
        return getUserOnlineStatus(friendId, req.getAppId());
    }

    /**
     * 查询用户在线状态
     *
     * @param req 查询用户在线状态请求
     * @return 用户在线状态映射
     */
    @Override
    public Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req) {
        return getUserOnlineStatus(req.getUserList(), req.getAppId());
    }

    private Map<String, UserOnlineStatusResp> getUserOnlineStatus(List<String> userIds, Integer appId) {
        Map<String, UserOnlineStatusResp> respMap = new HashMap<>(userIds.size());
        for (String userId : userIds) {
            UserOnlineStatusResp resp = new UserOnlineStatusResp();
            List<UserSession> userSessionList = userSessionHelper.getUserSessionList(appId, userId);
            resp.setSession(userSessionList);
            String key = appId + ":" + RedisConstants.USER_CUSTOMER_STATUS + ":" + userId;
            String s = stringRedisTemplate.opsForValue().get(key);
            if (CharSequenceUtil.isNotBlank(s)) {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                resp.setCustomText(jsonObject.getStr("customText"));
                resp.setCustomStatus(jsonObject.getInt("customStatus"));
            }
            respMap.put(userId, resp);
        }
        return respMap;
    }
}
