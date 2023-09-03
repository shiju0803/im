/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sj.im.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用于生成和解密IM服务中必须使用的UserSig鉴权票据的类
 */
@Slf4j
public class SigAPI {
    private final long appId;
    private final String key;

    public SigAPI(long appId, String key) {
        this.appId = appId;
        this.key = key;
    }


    public static void main(String[] args) {
        SigAPI asd = new SigAPI(10000, "ShiJu");
        String sign = asd.genUserSign("sj001", 1000000);
//        Thread.sleep(2000L);
        JSONObject jsonObject = decodeUserSign(sign);
        System.out.println("sign:" + sign);
        System.out.println("decoder:" + jsonObject.toString());
    }

    /**
     * 解密UserSig的方法
     *
     * @param userSign 待解密的UserSig
     * @return 解密后的JSON对象
     */
    public static JSONObject decodeUserSign(String userSign) {
        JSONObject sigDoc = new JSONObject(true);
        try {
            byte[] decodeUrlByte = Base64URL.base64DecodeUrlNotReplace(userSign.getBytes());
            byte[] decompressByte = decompress(decodeUrlByte);
            String decodeText = new String(decompressByte, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(decodeText)) {
                sigDoc = JSONUtil.parseObj(decodeText);
            }
        } catch (Exception ex) {
            log.error("解密异常: {}", ex.getMessage());
            throw new BusinessException(500, "签名解析失败");
        }
        return sigDoc;
    }

    /**
     * 解压缩字节数组的方法
     *
     * @param data 待解压缩的字节数组
     * @return 解压缩后的字节数组
     */
    public static byte[] decompress(byte[] data) {
        byte[] output;
        Inflater decompressor = new Inflater();
        decompressor.reset();
        decompressor.setInput(data);

        try (ByteArrayOutputStream o = new ByteArrayOutputStream(data.length)) {
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int i = decompressor.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            log.error("解压缩异常: {}", e.getMessage());
        }
        decompressor.end();
        return output;
    }


    /**
     * 生成UserSign的方法
     *
     * @param userid 用户ID
     * @param expire UserSig的过期时间，单位毫秒
     * @return 生成的UserSig
     */
    public String genUserSign(String userid, long expire) {
        return genUserSign(userid, expire, null);
    }

    /**
     * 生成UserSig的方法
     *
     * @param userid  用户ID
     * @param expire  UserSig的过期时间，单位为秒
     * @param userBuf 用户自定义数据
     * @return 生成的UserSig
     */
    private String genUserSign(String userid, long expire, byte[] userBuf) {
        long expireTime = System.currentTimeMillis() / 1000 + expire;
        JSONObject sigDoc = JSONUtil.createObj();
        sigDoc.putOnce("TLS.identifier", userid);
        sigDoc.putOnce("TLS.appId", appId);
        sigDoc.putOnce("TLS.expire", expire);
        sigDoc.putOnce("TLS.expireTime", expireTime);

        String base64UserBuf = null;
        if (null != userBuf) {
            base64UserBuf = Base64Encoder.encode(userBuf).replaceAll("\\s*", "");
            sigDoc.putOnce("TLS.userBuf", base64UserBuf);
        }
        String sig = hmacSHA256(userid, expireTime, expire, base64UserBuf);
        if (CharSequenceUtil.isBlank(sig)) {
            return CharSequenceUtil.EMPTY;
        }
        sigDoc.putOnce("TLS.sig", sig);
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    /**
     * 生成UserSig的方法
     *
     * @param userid  用户ID
     * @param expire  UserSig的过期时间，单位为秒
     * @param time    UserSig的生成时间，单位为秒
     * @param userBuf 用户自定义数据
     * @return 生成的UserSig
     */
    public String genUserSign(String userid, long expire, long time, byte[] userBuf) {
        JSONObject sigDoc = new JSONObject();
        sigDoc.putOnce("TLS.identifier", userid);
        sigDoc.putOnce("TLS.appId", appId);
        sigDoc.putOnce("TLS.expire", expire);
        sigDoc.putOnce("TLS.expireTime", time);

        String base64UserBuf = null;
        if (null != userBuf) {
            base64UserBuf = Base64Encoder.encode(userBuf).replaceAll("\\s*", "");
            sigDoc.putOnce("TLS.userBuf", base64UserBuf);
        }
        String sig = hmacSHA256(userid, time, expire, base64UserBuf);
        if (CharSequenceUtil.isBlank(sig)) {
            return CharSequenceUtil.EMPTY;
        }
        sigDoc.putOnce("TLS.sig", sig);
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    /**
     * HMAC-SHA256加密方法
     *
     * @param identifier    用户ID
     * @param currTime      当前时间，单位为秒
     * @param expire        UserSig的过期时间，单位为秒
     * @param base64UserBuf 用户自定义数据的Base64编码
     * @return 加密后的字符串
     */
    private String hmacSHA256(String identifier, long currTime, long expire, String base64UserBuf) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.appId:" + appId + "\n"
                + "TLS.expireTime:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64UserBuf) {
            contentToBeSigned += "TLS.userBuf:" + base64UserBuf + "\n";
        }
        try {
            byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return Base64Encoder.encode(byteSig).replaceAll("\\s*", "");
        } catch (Exception e) {
            return CharSequenceUtil.EMPTY;
        }
    }
}