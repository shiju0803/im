/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.sj.im.common.constant.RedisConstants;
import com.sj.im.common.enums.ImUserTypeEnum;
import com.sj.im.common.enums.exception.BaseErrorCode;
import com.sj.im.common.enums.exception.BaseExceptionEnum;
import com.sj.im.common.enums.exception.GateWayErrorCode;
import com.sj.im.common.util.SigAPI;
import com.sj.im.service.config.AppConfig;
import com.sj.im.service.user.entry.ImUserDataEntity;
import com.sj.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 操作人校验器
 *
 * @author ShiJu
 * @version 1.0
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

    /**
     * 检查用户鉴权票据
     *
     * @param identifier 用户ID
     * @param appId      应用ID
     * @param userSign   鉴权票据
     * @return 检查结果
     */
    public BaseExceptionEnum checkUserSign(String identifier, String appId, String userSign) {
        // 从redis中取出密钥，判断时效
        String key = appId + ":" + RedisConstants.USER_SIGN + ":" + identifier + ":" + userSign;
        String cacheUserSig = stringRedisTemplate.opsForValue().get(key);
        if (CharSequenceUtil.isNotBlank(cacheUserSig)
                && Long.parseLong(cacheUserSig) > System.currentTimeMillis() / 1000) {
            return BaseErrorCode.SUCCESS;
        }

        // 获取密钥
        String secretKey = appConfig.getSecretKey();

        // 根据appId + 密钥 创建sigApi
        SigAPI sigAPI = new SigAPI(Long.parseLong(appId), secretKey);

        // 调用sigApi对userSig解密
        JSONObject jsonObject = sigAPI.decodeUserSign(userSign);

        // 取出解密后的 appId 和 操作人 和 过期时间做匹配，不通过则提示错误
        long expireSec = 0L;
        long expireTime = 0L;
        long time = 0L;
        String decoderAppId = "";
        String decoderIdentifiter = "";

        try {
            decoderAppId = jsonObject.getStr("TLS.appId");
            decoderIdentifiter = jsonObject.getStr("TLS.identifier");
            time = jsonObject.getLong("TLS.expireTime");
            expireSec = jsonObject.getLong("TLS.expire");
            expireTime = time + expireSec;
        } catch (Exception e) {
            log.error("checkUserSig-error: {}", e.getMessage());
        }

        if (ObjectUtil.notEqual(identifier, decoderIdentifiter)) {
            //操作人不匹配，返回错误
            return GateWayErrorCode.USER_SIGN_OPERATE_NOT_MATE;
        }

        if (ObjectUtil.notEqual(appId, decoderAppId)) {
            //appid不匹配，返回错误
            return GateWayErrorCode.USER_SIGN_IS_ERROR;
        }

        if (ObjectUtil.equal(expireSec, 0L)) {
            //过期时间为0，返回错误
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        if (expireTime < System.currentTimeMillis() / 1000) {
            //鉴权票据已过期，返回错误
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        //appid + "xxx" + userId + sign
        String genSign = sigAPI.genUserSign(identifier, expireSec, time, null);
        if (ObjectUtil.equal(genSign.toLowerCase(), userSign.toLowerCase())) {
            //生成的鉴权票据与传入的鉴权票据匹配，缓存鉴权票据并设置用户为管理员身份，并返回成功
            long etime = expireTime - System.currentTimeMillis() / 1000;
            stringRedisTemplate.opsForValue().set(key, Long.toString(expireTime), etime, TimeUnit.SECONDS);
            this.setIsAdmin(identifier, Integer.valueOf(appId));
            return BaseErrorCode.SUCCESS;
        }
        //鉴权票据不匹配，返回错误
        return GateWayErrorCode.USER_SIGN_IS_ERROR;
    }

    /**
     * 根据appid,identifier判断是否App管理员,并设置到RequestHolder
     *
     * @param identifier 用户ID
     * @param appId      应用ID
     */
    public void setIsAdmin(String identifier, Integer appId) {
        //去DB或Redis中查找用户信息，判断用户是否为管理员，并设置到RequestHolder中
        ImUserDataEntity singleUserInfo = imUserService.getSingleUserInfo(identifier, appId);
        if (ObjectUtil.isNotNull(singleUserInfo)) {
            RequestHolder.set(ObjectUtil.equal(singleUserInfo.getUserType(), ImUserTypeEnum.APP_ADMIN.getCode()));
        } else {
            RequestHolder.set(false);
        }
    }
}
