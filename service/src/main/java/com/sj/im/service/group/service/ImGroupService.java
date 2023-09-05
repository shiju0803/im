/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.group.service;

import com.sj.im.common.model.SyncReq;
import com.sj.im.common.model.SyncResp;
import com.sj.im.service.friendship.web.req.GetGroupInfoReq;
import com.sj.im.service.group.entry.ImGroupEntity;
import com.sj.im.service.group.web.req.*;
import com.sj.im.service.group.web.resp.GetGroupResp;
import com.sj.im.service.group.web.resp.GetJoinedGroupResp;

public interface ImGroupService {
    /**
     * 导入群组
     *
     * @param req 导入群组请求参数
     */
    void importGroup(ImportGroupReq req);

    /**
     * 创建群组
     *
     * @param req 创建群组请求参数
     */
    void createGroup(CreateGroupReq req);

    /**
     * 更新群组基本信息
     *
     * @param req 更新群组请求参数
     */
    void updateBaseGroupInfo(UpdateGroupReq req);

    /**
     * 获取用户加入的群组列表
     *
     * @param req 获取用户加入的群组列表请求参数
     * @return 获取用户加入的群组列表响应参数
     */
    GetJoinedGroupResp getJoinedGroup(GetJoinedGroupReq req);

    /**
     * 解散群组
     *
     * @param req 解散群组请求参数
     */
    void destroyGroup(DestroyGroupReq req);

    /**
     * 转让群组
     *
     * @param req 转让群组请求参数
     */
    void transferGroup(TransferGroupReq req);

    /**
     * 获取群组信息
     *
     * @param groupId 群组 ID
     * @param appId   应用 ID
     * @return 群组实体
     */
    ImGroupEntity getGroup(String groupId, Integer appId);


    /**
     * 获取群组信息
     *
     * @param req 获取群组信息请求参数
     * @return 获取群组信息响应参数
     */
    GetGroupResp getGroupInfo(GetGroupInfoReq req);

    /**
     * 禁言/解禁群组
     *
     * @param req 禁言/解禁群组请求参数
     */
    void muteGroup(MuteGroupReq req);

    /**
     * 同步用户加入的群组列表
     *
     * @param req 同步请求参数
     * @return 同步响应参数
     */
    SyncResp<ImGroupEntity> syncJoinedGroupList(SyncReq req);

    /**
     * 动态获取群组中最大的seq
     */
    Long getUserGroupMaxSeq(String userId, Integer appId);
}
