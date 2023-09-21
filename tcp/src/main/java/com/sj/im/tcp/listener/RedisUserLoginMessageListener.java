/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sj.im.codec.proto.MessagePack;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.constant.TcpConstants;
import com.sj.im.common.enums.ClientType;
import com.sj.im.common.enums.DeviceMultiLoginEnum;
import com.sj.im.common.enums.command.SystemCommand;
import com.sj.im.common.model.UserClientDto;
import com.sj.im.tcp.config.TcpConfig;
import com.sj.im.tcp.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * 监听redis消息，处理用户登录的多端同步功能
 *
 * @author ShiJu
 * @version 1.0
 * <p>
 * 实现逻辑：
 * 1. 单端登录：如果用户只在一个端口登录，踢掉除本clinetType + imei外的其他设备。
 * 2. 双端登录：允许pc/mobile 其中一端登录 + web端，踢掉除了本clinetType + imei以外的web端设备。
 * 3. 三端登录：允许手机 + pc + web，踢掉同端的其他imei除了web。
 * 4. 不做任何处理。
 * <p>
 * 多端同步实现：
 * 当用户登录成功后，将用户信息封装成UserClientDto对象，通过Redisson的RTopic发布到Redis的UserLoginChannel频道中，
 * 然后通过订阅该频道的其他节点将用户登录信息同步到其他节点上。
 */
@Slf4j
public class RedisUserLoginMessageListener {

    private final RedissonClient redissonClient;
    private final TcpConfig nettyConfig;

    public RedisUserLoginMessageListener(RedissonClient redissonClient, TcpConfig nettyConfig) {
        this.redissonClient = redissonClient;
        this.nettyConfig = nettyConfig;
    }

    /**
     * 监听用户登录消息
     */
    public void listenerUserLogin() {
        // 获取 RedissonClient 中指定主题的 RTopic 实例
        RTopic topic = redissonClient.getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        // 给 RTopic 实例添加监听器
        topic.addListener(String.class, (charSequence, msg) -> {
            // 实现 onMessage 方法
            log.info("收到用户上线通知：" + msg);
            // 解析收到的用户上线通知消息
            UserClientDto dto = JSONUtil.toBean(msg, UserClientDto.class);
            // 获取用户的所有 NioSocketChannel
            List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.getAll(dto.getAppId(), dto.getUserId());

            String loginClientImei = dto.getClientType() + ":" + dto.getImei();
            Integer loginModel = nettyConfig.getLoginModel();
            // 遍历用户的每个 NioSocketChannel
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
                    if (ObjectUtil.equal(dto.getClientType(), ClientType.WEB.getCode()) || ObjectUtil.equal(clientType,
                                                                                                            ClientType.WEB.getCode())) {
                        continue;
                    }

                    // 双端登录（允许手机/电脑的一台设备 + web在线）：踢掉除了本client+imei的非web端设备
                    if (ObjectUtil.equal(loginModel, DeviceMultiLoginEnum.TWO.getLoginMode()) && (ObjectUtil.notEqual(
                            loginClientImei, targetClientImei))) {
                        // 踢掉除了本client+imei的非web端设备
                        mutualLogin(channel);
                    }

                    // 三端登录（允许手机和电脑单设备 + web 同时在线）：踢掉非本client+imei的同端设备
                    if (ObjectUtil.equal(loginModel, DeviceMultiLoginEnum.THREE.getLoginMode())) {
                        // 是否都是手机端
                        boolean isSameClient =
                                (ObjectUtil.equal(clientType, ClientType.IOS.getCode()) || ObjectUtil.equal(clientType,
                                                                                                            ClientType.ANDROID.getCode()))
                                        && (ObjectUtil.equal(dto.getClientType(), ClientType.IOS.getCode())
                                        || ObjectUtil.equal(dto.getClientType(), ClientType.ANDROID.getCode()));

                        // 是否都是电脑端
                        if ((ObjectUtil.equal(clientType, ClientType.MAC.getCode()) || ObjectUtil.equal(clientType,
                                                                                                        ClientType.WINDOWS.getCode()))
                                && (ObjectUtil.equal(dto.getClientType(), ClientType.MAC.getCode()) || ObjectUtil.equal(
                                dto.getClientType(), ClientType.WINDOWS.getCode()))) {
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
