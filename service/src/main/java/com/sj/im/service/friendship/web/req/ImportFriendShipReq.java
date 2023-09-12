/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.req;

import com.sj.im.common.enums.FriendShipStatusEnum;
import com.sj.im.common.model.RequestBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("导入好友关系请求")
public class ImportFriendShipReq extends RequestBase {
    @NotNull(message = "fromId不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private String fromId;

    @ApiModelProperty(value = "导入的好友数据")
    private List<ImportFriendDto> friendItem;

    @Data
    @ApiModel("导入好友数据实体")
    public static class ImportFriendDto {
        @NotNull(message = "toId不能为空")
        @ApiModelProperty(value = "导入的好友id", required = true)
        private String toId;

        @ApiModelProperty(value = "好友备注")
        private String remark;

        @ApiModelProperty(value = "导入来源")
        private String addSource;

        @ApiModelProperty(value = "状态", example = "0未导入 1正常 2删除")
        private Integer status = FriendShipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

        @ApiModelProperty(value = "拉黑状态", example = "1正常 2拉黑")
        private Integer black = FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode();
    }
}
