/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.web.callback;

import com.sj.im.service.group.web.resp.AddMemberResp;
import lombok.Data;

import java.util.List;

@Data
public class AddMemberAfterCallback {

    private String groupId;

    private Integer groupType;

    private String operator;

    private List<AddMemberResp> memberId;
}