/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.common.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.sj.im.common.config.GlobalHttpClientConfig;
import com.sj.im.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Http请求工具类
 */
@Slf4j
@Component
public class HttpRequestUtil {
    @Resource
    private GlobalHttpClientConfig httpClientConfig;
    @Resource
    private RequestConfig requestConfig;

    private HttpRequestUtil() {
    }

    /**
     * 发起GET请求，返回响应体的字符串形式
     *
     * @param url     请求的URL
     * @param params  请求参数
     * @param charset 字符集
     * @return 响应体的字符串形式
     */
    public String doGet(String url, Map<String, Object> params, String charset) throws Exception {
        return doGet(url, params, null, charset);
    }

    /**
     * 发起GET请求，返回响应体的字符串形式
     *
     * @param url     请求的URL
     * @param params  请求参数
     * @param header  请求头
     * @param charset 字符集
     * @return 响应体的字符串形式
     */
    public String doGet(String url, Map<String, Object> params, Map<String, Object> header, String charset) throws Exception {
        if (CharSequenceUtil.isBlank(charset)) {
            charset = CharsetUtil.UTF_8;
        }
        URIBuilder uriBuilder = new URIBuilder(url);
        // 判断是否有参数
        if (ObjectUtil.isNotNull(params)) {
            // 遍历map,拼接请求参数
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        // 声明 http get 请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setConfig(requestConfig);

        if (ObjectUtil.isNotNull(header)) {
            // 遍历map,拼接header参数
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }

        String result = CharSequenceUtil.EMPTY;
        try {
            // 发起请求
            CloseableHttpResponse response = httpClientConfig.getCloseableHttpClient().execute(httpGet);
            // 判断状态码是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 返回响应体的内容
                result = EntityUtils.toString(response.getEntity(), charset);
            }

        } catch (IOException e) {
            log.error("get请求发送失败,url:{}, errMsg:{}", url, e.getMessage());
            throw new BusinessException(e);
        }
        return result;
    }

    /**
     * 发起GET请求，返回响应体的字符串形式
     *
     * @param url    请求的URL
     * @param params 请求参数
     * @return 响应体的字符串形式
     */
    public String doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, null);
    }

    /**
     * 发起GET请求，返回响应体的字符串形式
     *
     * @param url 请求的URL
     * @return 响应体的字符串形式
     */
    public String doGet(String url) throws Exception {
        return doGet(url, null, null);
    }

    /**
     * 发起POST请求，返回响应体的字符串形式
     *
     * @param url      请求的URL
     * @param params   请求参数
     * @param jsonBody 请求体（JSON格式）
     * @param charset  字符集
     * @return 响应体的字符串形式
     */
    public String doPost(String url, Map<String, Object> params, String jsonBody, String charset) throws Exception {
        return doPost(url, params, null, jsonBody, charset);
    }

    /**
     * 发起POST请求，返回响应体的字符串形式
     *
     * @param url      请求的URL
     * @param params   请求参数
     * @param header   请求头
     * @param jsonBody 请求体（JSON格式）
     * @param charset  字符集
     * @return 响应体的字符串形式
     */
    public String doPost(String url, Map<String, Object> params, Map<String, Object> header, String jsonBody, String charset) throws Exception {

        if (StringUtils.isEmpty(charset)) {
            charset = CharsetUtil.UTF_8;
        }
        URIBuilder uriBuilder = new URIBuilder(url);
        // 判断是否有参数
        if (ObjectUtil.isNotNull(params)) {
            // 遍历map,拼接请求参数
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 声明httpPost请求
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        // 加入配置信息
        httpPost.setConfig(requestConfig);

        // 判断map是否为空，不为空则进行遍历，封装from表单对象
        if (CharSequenceUtil.isNotBlank(jsonBody)) {
            StringEntity s = new StringEntity(jsonBody, charset);
            s.setContentEncoding(charset);
            s.setContentType("application/json");

            // 把json body放到post里
            httpPost.setEntity(s);
        }

        if (ObjectUtil.isNotNull(header)) {
            // 遍历map,拼接header参数
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }

        String result = CharSequenceUtil.EMPTY;
        CloseableHttpResponse response;
        try {
            // 发起请求
            response = httpClientConfig.getCloseableHttpClient().execute(httpPost);
            // 判断状态码是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 返回响应体的内容
                result = EntityUtils.toString(response.getEntity(), charset);
            }

        } catch (IOException e) {
            log.error("post请求发送失败,url:{}, errMsg:{}", url, e.getMessage());
            throw new BusinessException(e);
        }
        return result;
    }

    /**
     * 发起POST请求，返回响应体的字符串形式
     *
     * @param url 请求的URL
     * @return 响应体的字符串形式
     */
    public String doPost(String url) throws Exception {
        return doPost(url, null, null, null);
    }

    /**
     * 发起GET请求，返回响应体的对象形式
     *
     * @param url     请求的URL
     * @param tClass  响应体的类型
     * @param map     请求参数
     * @param charSet 字符集
     * @param <T>     响应体的类型
     * @return 响应体的对象形式
     */
    public <T> T doGet(String url, Class<T> tClass, Map<String, Object> map, String charSet) throws Exception {
        String result = doGet(url, map, charSet);
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, tClass);
        }
        return null;
    }

    /**
     * 发起GET请求，返回响应体的对象形式
     *
     * @param url     请求的URL
     * @param tClass  响应体的类型
     * @param map     请求参数
     * @param header  请求头
     * @param charSet 字符集
     * @param <T>     响应体的类型
     * @return 响应体的对象形式
     */
    public <T> T doGet(String url, Class<T> tClass, Map<String, Object> map, Map<String, Object> header, String charSet) throws Exception {
        String result = doGet(url, map, header, charSet);
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, tClass);
        }
        return null;
    }

    /**
     * 发起POST请求，返回响应体的对象形式
     *
     * @param url      请求的URL
     * @param tClass   响应体的类型
     * @param map      请求参数
     * @param jsonBody 请求体（JSON格式）
     * @param charSet  字符集
     * @param <T>      响应体的类型
     * @return 响应体的对象形式
     */
    public <T> T doPost(String url, Class<T> tClass, Map<String, Object> map, String jsonBody, String charSet) throws Exception {
        String result = doPost(url, map, jsonBody, charSet);
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, tClass);
        }
        return null;
    }

    /**
     * 发起POST请求，返回响应体的对象形式
     *
     * @param url      请求的URL
     * @param tClass   响应体的类型
     * @param map      请求参数
     * @param header   请求头
     * @param jsonBody 请求体（JSON格式）
     * @param charSet  字符集
     * @param <T>      响应体的类型
     * @return 响应体的对象形式
     */
    public <T> T doPost(String url, Class<T> tClass, Map<String, Object> map, Map<String, Object> header, String jsonBody, String charSet) throws Exception {
        String result = doPost(url, map, header, jsonBody, charSet);
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, tClass);
        }
        return null;
    }

    /**
     * 发起POST请求，返回响应体的字符串形式
     *
     * @param url      请求的URL
     * @param map      请求参数
     * @param jsonBody 请求体（JSON格式）
     * @param charSet  字符集
     * @return 响应体的字符串形式
     */
    public String doPostString(String url, Map<String, Object> map, String jsonBody, String charSet) throws Exception {
        return doPost(url, map, jsonBody, charSet);
    }

    /**
     * 发起POST请求，返回响应体的字符串形式
     *
     * @param url      请求的URL
     * @param map      请求参数
     * @param header   请求头
     * @param jsonBody 请求体（JSON格式）
     * @param charSet  字符集
     * @return 响应体的字符串形式
     */
    public String doPostString(String url, Map<String, Object> map, Map<String, Object> header, String jsonBody, String charSet) throws Exception {
        return doPost(url, map, header, jsonBody, charSet);
    }
}
