/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.friendship.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("校验好友关系接口响应类")
public class CheckFriendShipResp {
    @ApiModelProperty(value = "用户id")
    private String fromId;

    @ApiModelProperty(value = "好友id")
    private String toId;


    //校验状态，根据双向校验和单向校验有不同的status
    //单向校验：1 from添加了to，不确定to是否添加了from CheckResult_single_Type_AWithB；
    //         0  from没有添加to，也不确定to有没有添加from CheckResult_single_Type_NoRelation
    //双向校验 1 from添加了to，to也添加了from CheckResult_Type_BothWay
    //        2 from添加了t0，to没有添加from CheckResult_Both_Type_AWithB
    //        3 from没有添加to，to添加了from， CheckResult_Both_Type_BWithA
    //        4 双方都没有添加 CheckResult_Both_Type_NoRelation

    //单向校验黑名单：1 from没有拉黑to，不确定to是否拉黑了from CheckResult_singe_Type_AWithB；
    //              0 from拉黑to，不确定to是佛拉黑from CheckResult_singe_Type_NoRelation
    //双向校验黑名单 1 from没有拉黑to，to也没有拉黑from CheckResult_Type_BothWay
    //        2 from没有拉黑to，to拉黑from CheckResult_Both_Type_AWithB
    //        3 from拉黑了to，to没有拉黑from CheckResult_Both_Type_BWithA
    //        4 双方都拉黑 CheckResult_Both_Type_NoRelation
    @ApiModelProperty(value = "好友关系状态")
    private Integer status;
}
