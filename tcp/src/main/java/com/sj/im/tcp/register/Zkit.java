/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.register;

import com.sj.im.common.constant.TcpConstants;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author ShiJu
 * @version 1.0
 * @description: TODO
 */
public class Zkit {

    private ZkClient zkClient;

    public Zkit(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public void createRootNode() {
        boolean tcpExists = zkClient.exists(TcpConstants.IM_CORE_ZK_ROOT_TCP);
        if (!tcpExists) {
            zkClient.createPersistent(TcpConstants.IM_CORE_ZK_ROOT_TCP, true);
        }

        boolean webExists = zkClient.exists(TcpConstants.IM_CORE_ZK_ROOT_WEB);
        if (!webExists) {
            zkClient.createPersistent(TcpConstants.IM_CORE_ZK_ROOT_WEB, true);
        }
    }

    // ip + port
    public void createNode(String path) {
        if (!zkClient.exists(path)) {
            zkClient.createEphemeral(path);
        }
    }
}
