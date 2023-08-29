/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.register;

import com.sj.im.codec.config.BootstrapConfig;
import com.sj.im.common.constant.TcpConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ShiJu
 * @version 1.0
 * @description: TODO
 */
@Slf4j
public class RegistryZk implements Runnable {

    private final Zkit zkit;

    private final String ip;

    private final BootstrapConfig.TcpConfig tcpConfig;

    public RegistryZk(Zkit zkit, String ip, BootstrapConfig.TcpConfig tcpConfig) {
        this.zkit = zkit;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    @Override
    public void run() {
        zkit.createRootNode();
        String tcpPath = TcpConstants.IM_CORE_ZK_ROOT_TCP + "/" + ip + ":" + tcpConfig.getTcpPort();
        zkit.createNode(tcpPath);
        log.info("Registry zookeeper tcpPath success, msg = [{}]", tcpPath);

        String webPath = TcpConstants.IM_CORE_ZK_ROOT_WEB + "/" + ip + ":" + tcpConfig.getWebSocketPort();
        zkit.createNode(webPath);
        log.info("Registry zookeeper webPath success, msg = [{}]", webPath);
    }
}
