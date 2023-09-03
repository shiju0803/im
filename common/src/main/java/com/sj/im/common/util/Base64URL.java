/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.util;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;

import java.nio.charset.StandardCharsets;

/**
 * @author ShiJu
 * @version 1.0
 * @description: URL base64加解密工具类
 */
public class Base64URL {

    private Base64URL() {}

    // 将输入的字节数组进行Base64编码，并将字符'+', '/', 和 '=' 替换为 '*', '-', 和 '_'
    public static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = Base64Encoder.encode(input).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return base64;
    }

    // 将输入的字节数组进行Base64编码，但不替换字符'+', '/', 和 '='
    public static byte[] base64EncodeUrlNotReplace(byte[] input) {
        byte[] base64 = Base64Encoder.encode(input).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return base64;
    }

    // 将输入的字节数组进行Base64解码，但不替换字符'+', '/', 和 '='
    public static byte[] base64DecodeUrlNotReplace(byte[] input) {
        byte[] base64 = input.clone();
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '*':
                    base64[i] = '+';
                    break;
                case '-':
                    base64[i] = '/';
                    break;
                case '_':
                    base64[i] = '=';
                    break;
                default:
                    break;
            }
        return Base64Decoder.decode(base64);
    }

    // 将输入的字节数组进行Base64解码，并将字符'*', '-', 和 '_' 替换为 '+', '/', 和 '='
    public static byte[] base64DecodeUrl(byte[] input) {
        byte[] base64 = input.clone();
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '*':
                    base64[i] = '+';
                    break;
                case '-':
                    base64[i] = '/';
                    break;
                case '_':
                    base64[i] = '=';
                    break;
                default:
                    break;
            }
        return Base64Decoder.decode(base64);
    }
}