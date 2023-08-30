/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service;

import com.sj.im.common.ResponseVO;
import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.model.req.GetGroupInfoReq;
import com.sj.im.service.group.dao.ImGroupEntity;
import com.sj.im.service.group.model.req.*;
import com.sj.im.service.group.model.resp.GetGroupResp;

import java.util.List;

public interface ImGroupService {
    /**
     * 导入群
     */
    ResponseVO<String> importGroup(ImportGroupReq req);

    /**
     * 新建群组
     */
    ResponseVO<String> createGroup(CreateGroupReq req);

    /**
     * 修改群信息
     */
    ResponseVO<String> updateBaseGroupInfo(UpdateGroupReq req);

    /**
     * 获取群的具体信息
     */
    ResponseVO<GetGroupResp> getGroupInfo(GetGroupInfoReq req);

    /**
     * 获取群信息
     */
    ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId);

    /**
     * 获取用户加入的群组
     */
    ResponseVO<List<ImGroupEntity>> getJoinedGroup(GetJoinedGroupReq req);

    /**
     * 解散群组
     */
    ResponseVO<String> destroyGroup(DestroyGroupReq req);

    /**
     * 转让群主
     */
    ResponseVO<String> transferGroup(TransferGroupReq req);

    /**
     * 群组禁言
     */
    ResponseVO<String> muteGroup(MuteGroupReq req);

    /**
     * 增量同步群组成员列表
     */
    ResponseVO<SyncResp<ImGroupEntity>> syncJoinedGroupList(SyncReq req);

    /**
     * 动态获取群组中最大的seq
     */
    Long getUserGroupMaxSeq(String userId, Integer appId);
}
