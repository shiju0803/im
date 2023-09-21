/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.service.impl;

import cn.hutool.json.JSONUtil;
import com.sj.app.config.AppConfig;
import com.sj.app.entry.AppUserEntity;
import com.sj.app.service.ImService;
import com.sj.app.web.req.ImportUserReq;
import com.sj.app.web.resp.ImportUserResp;
import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.util.HttpRequestUtil;
import com.sj.im.common.util.SigAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Service
public class ImServiceImpl implements ImService {
    private static final Object lock = new Object();
    private volatile static Map<String, Object> parameter = null;
    @Resource
    private HttpRequestUtil httpRequestUtil;
    @Resource
    private AppConfig appConfig;

    /**
     * 导入IM用户的方法
     *
     * @param users 用户信息
     */
    @Override
    public ResponseVO<ImportUserResp> importUser(List<AppUserEntity> users) {
        ImportUserReq req = new ImportUserReq();
        List<ImportUserReq.UserData> userData = new ArrayList<>();
        for (AppUserEntity user : users) {
            ImportUserReq.UserData data = new ImportUserReq.UserData();
            data.setUserId(user.getUserId());
            data.setPassword(user.getPassword());
            data.setUserType(1);
            userData.add(data);
        }

        String uri = "/user/importUser";
        try {
            req.setUserData(userData);
            req.setAppId(appConfig.getAppId());
            return httpRequestUtil.doPost(getUrl(uri), ResponseVO.class, getParameter(), JSONUtil.toJsonStr(req), null);
        } catch (Exception e) {
            log.error("请求IM服务失败:", e);
        }
        return ResponseVO.errorResponse();
    }

    private String getUrl(String uri) {
        return appConfig.getImUrl() + "/" + appConfig.getImVersion() + uri;
    }

    private Map<String, Object> getParameter() {
        if (parameter == null) {
            synchronized (lock) {
                if (parameter == null) {
                    SigAPI sigAPI = new SigAPI(appConfig.getAppId(), appConfig.getPrivateKey());
                    Map<String, Object> temp = new ConcurrentHashMap<>();
                    temp.put("appId", appConfig.getAppId());
                    temp.put("userSign", sigAPI.genUserSign(appConfig.getAdminId(), 30 * 60 * 1000L));
                    temp.put("identifier", appConfig.getAdminId());
                    parameter = temp;
                }
            }
        }
        return parameter;
    }
}
