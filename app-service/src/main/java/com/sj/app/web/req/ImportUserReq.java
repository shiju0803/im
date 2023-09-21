/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.web.req;

import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("导入用户接口入参")
public class ImportUserReq extends RequestBase {

    @ApiModelProperty(value = "用户信息集合", required = true)
    private List<UserData> userData;

    @Data
    public static class UserData {
        /**
         * 用户id
         */
        private String userId;

        /**
         * 用户类型 1普通用户 2客服 3 机器人
         */
        private Integer userType;

        /**
         * appId
         */
        private Integer appId;

        /**
         * 密码
         */
        private String password;
    }
}
