/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.helper;

import cn.hutool.core.util.ObjectUtil;
import com.github.zkclient.ZkClient;
import com.sj.im.common.ClientType;
import com.sj.im.common.constant.TcpConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Zookeeper工具类
 */
@Component
public class ZookeeperHelper {
    @Resource
    private ZkClient zkClient;

    /**
     * 根据连接类型获取Zookeeper服务信息
     */
    public List<String> getAllNodeByClientType(Integer clientType) {
        List<String> nodes;
        if (ObjectUtil.equal(clientType, ClientType.WEB.getCode())) {
            nodes = zkClient.getChildren(TcpConstants.IM_CORE_ZK_ROOT + TcpConstants.IM_CORE_ZK_ROOT_WEB);
        } else {
            nodes = zkClient.getChildren(TcpConstants.IM_CORE_ZK_ROOT + TcpConstants.IM_CORE_ZK_ROOT_TCP);
        }
        return nodes;
    }
}
