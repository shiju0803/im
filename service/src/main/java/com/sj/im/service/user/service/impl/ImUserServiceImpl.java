/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.user.service.impl;

import com.sj.im.common.enums.ResponseVO;
import com.sj.im.service.user.dao.ImUserDataEntity;
import com.sj.im.service.user.dao.mapper.ImUserDataMapper;
import com.sj.im.service.user.model.req.ImportUserReq;
import com.sj.im.service.user.model.resp.ImportUserResp;
import com.sj.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 用户相关接口实现类
 * @author ShiJu
 * @version 1.0
 */
@Service
public class ImUserServiceImpl implements ImUserService {
    @Resource
    ImUserDataMapper imUserDataMapper;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
        if (req.getUserData().size() > 100) {
            //TODO 返回数量过多
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImUserDataEntity user : req.getUserData()) {
            try {
                user.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(user);
                if (insert == 1) {
                    successId.add(user.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(user.getUserId());
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }
}
