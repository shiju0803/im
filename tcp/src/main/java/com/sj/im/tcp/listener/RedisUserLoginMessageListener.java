/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.pack.MessagePack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.ClientType;
import com.sj.im.common.enums.DeviceMultiLoginEnum;
import com.sj.im.common.enums.command.SystemCommand;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.tcp.redis.RedisManager;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户登录Redis信息监听类
 * 多端同步模式：
 * 1 单端登录（手机/电脑/web）,一端在线：踢掉除了本client+imei的设备
 * 2 双端登录（允许手机/电脑的一台设备 + web在线）：踢掉除了本client+imei的非web端设备
 * 3 三端登录（允许手机和电脑单设备 + web 同时在线）：踢掉非本client+imei的同端设备
 * 4 全端登录（允许所有端多设备登录）：不踢任何设备
 */
@Slf4j
public class RedisUserLoginMessageListener {

    private final Integer loginModel;

    public RedisUserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin() {
        RTopic topic = RedisManager.getRedissonClient().getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        topic.addListener(String.class, (charSequence, msg) -> {
            log.info("收到用户上线通知：" + msg);
            UserClientDto dto = JSONUtil.toBean(msg, UserClientDto.class);
            List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.getAll(dto.getAppId(), dto.getUserId());

            String loginClientImei = dto.getClientType() + ":" + dto.getImei();
            for (NioSocketChannel channel : nioSocketChannels) {
                Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(TcpConstants.CLIENT_TYPE)).get();
                String imei = (String) channel.attr(AttributeKey.valueOf(TcpConstants.IMEI)).get();
                String targetClientImei = clientType + ":" + imei;
                // 单端登录（手机/电脑/web）,一端在线：踢掉除了本client+imei的设备
                if (ObjectUtil.equal(loginModel, DeviceMultiLoginEnum.ONE.getLoginMode())) {
                    if (ObjectUtil.notEqual(loginClientImei, targetClientImei)) {
                        // 告诉客户端 其他端登录
                        mutualLogin(channel);
                    }
                } else {
                    // web端直接跳过不处理
                    if (ObjectUtil.equal(dto.getClientType(), ClientType.WEB.getCode()) || ObjectUtil.equal(clientType, ClientType.WEB.getCode())) {
                        continue;
                    }

                    // 双端登录（允许手机/电脑的一台设备 + web在线）：踢掉除了本client+imei的非web端设备
                    if (ObjectUtil.equal(loginModel, DeviceMultiLoginEnum.TWO.getLoginMode()) && (ObjectUtil.notEqual(loginClientImei, targetClientImei))) {
                        // 踢掉除了本client+imei的非web端设备
                        mutualLogin(channel);
                    }

                    // 三端登录（允许手机和电脑单设备 + web 同时在线）：踢掉非本client+imei的同端设备
                    if (ObjectUtil.equal(loginModel, DeviceMultiLoginEnum.THREE.getLoginMode())) {
                        // 是否都是手机端
                        boolean isSameClient = (ObjectUtil.equal(clientType, ClientType.IOS.getCode()) || ObjectUtil.equal(clientType, ClientType.ANDROID.getCode()))
                                && (ObjectUtil.equal(dto.getClientType(), ClientType.IOS.getCode()) || ObjectUtil.equal(dto.getClientType(), ClientType.ANDROID.getCode()));

                        // 是否都是电脑端
                        if ((ObjectUtil.equal(clientType, ClientType.MAC.getCode()) || ObjectUtil.equal(clientType, ClientType.WINDOWS.getCode()))
                                && (ObjectUtil.equal(dto.getClientType(), ClientType.MAC.getCode()) || ObjectUtil.equal(dto.getClientType(), ClientType.WINDOWS.getCode()))) {
                            isSameClient = true;
                        }

                        if (isSameClient && ObjectUtil.notEqual(loginClientImei, targetClientImei)) {
                            // 踢掉非本client+imei的同端设备
                            mutualLogin(channel);
                        }
                    }
                }
            }
        });
    }

    /**
     * 通知下线
     */
    private void mutualLogin(NioSocketChannel channel) {
        MessagePack<Object> pack = new MessagePack<>();
        pack.setToId((String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get());
        pack.setUserId((String) channel.attr(AttributeKey.valueOf(TcpConstants.USERID)).get());
        pack.setCommand(SystemCommand.MUTUAL_LOGIN.getCommand());
        channel.writeAndFlush(pack);
    }
}
