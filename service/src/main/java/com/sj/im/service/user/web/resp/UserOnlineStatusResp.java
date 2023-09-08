package com.sj.im.service.user.web.resp;

import com.sj.im.common.model.UserSession;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: 用户在线状态响应类
 */
@Data
@ApiModel("用户在线状态响应类")
public class UserOnlineStatusResp {

    @ApiModelProperty(value = "用户会话集合")
    private List<UserSession> session;

    @ApiModelProperty(value = "用户客户端id")
    private String customText;

    @ApiModelProperty(value = "用户客户端在线状态")
    private Integer customStatus;
}