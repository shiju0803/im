/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.enums.exception.BaseExceptionEnum;
import com.sj.im.service.config.AppConfig;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.exception.GateWayErrorCode;
import com.sj.im.common.util.SigAPI;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 操作人校验器
 */
@Slf4j
@Component
public class IdentityCheck {
    @Resource
    private ImUserService imUserService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public BaseExceptionEnum checkUserSign(String identifier, String appId, String userSign) {
        // 从redis中取出密钥，判断时效
        String key = appId + ":" + RedisConstants.USER_SIGN + ":" + identifier + ":" + userSign;
        String cacheUserSig = stringRedisTemplate.opsForValue().get(key);
        if (CharSequenceUtil.isNotBlank(cacheUserSig) && Long.parseLong(cacheUserSig) > System.currentTimeMillis() / 1000) {
            return BaseErrorCode.SUCCESS;
        }

        // 获取密钥
        String secretKey = appConfig.getSecretKey();

        // 根据appId + 密钥 创建sigApi
        SigAPI sigAPI = new SigAPI(Long.parseLong(appId), secretKey);

        // 调用sigApi对userSig解密
        JSONObject jsonObject = sigAPI.decodeUserSign(userSign);

        // 取出解密后的 appId 和 操作人 和 过期时间做匹配，不通过则提示错误
        Long expireSec = 0L;
        Long expireTime = 0L;
        String decoderAppId = "";
        String decoderIdentifiter = "";

        try {
            decoderAppId = jsonObject.getStr("TLS.appId");
            decoderIdentifiter = jsonObject.getStr("TLS.identifier");
            expireSec = jsonObject.getLong("TLS.expire");
            expireTime = jsonObject.getLong("TLS.expireTime");
        } catch (Exception e) {
            log.error("checkUserSig-error: {}", e.getMessage());
        }

        if (ObjectUtil.notEqual(appId, decoderAppId)) {
            return GateWayErrorCode.USER_SIGN_IS_ERROR;
        }

        if (ObjectUtil.notEqual(identifier, decoderIdentifiter)) {
            return GateWayErrorCode.USER_SIGN_OPERATE_NOT_MATE;
        }

        if (ObjectUtil.equal(expireSec, 0L)) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        if (expireTime < System.currentTimeMillis() / 1000) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        // 把userSign存储到redis中去
        // appid + "xxx" + "userId" + sign
        Long etime = expireTime - System.currentTimeMillis() / 1000;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(expireTime), etime, TimeUnit.SECONDS);
        return BaseErrorCode.SUCCESS;
    }
}
