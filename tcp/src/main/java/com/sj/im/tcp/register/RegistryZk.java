/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.register;

import com.sj.im.common.constant.TcpConstants;
import com.sj.im.tcp.config.TcpConfig;
import com.sj.im.tcp.util.ZkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;

/**
 * 服务注册到zookeeper类
 *
 * @author ShiJu
 * @version 1.0
 */
@Slf4j
@Component
@DependsOn({"limServer", "limWebSocketServer"})
public class RegistryZk {
    @Resource
    private ZkUtil zkUtil;
    @Resource
    private TcpConfig tcpConfig;

    @PostConstruct
    public void init() throws Exception {
        // 创建Zookeeper中的根节点
        createRootNode();
        // 获取本地主机地址
        String ip = InetAddress.getLocalHost().getHostAddress();
        // 创建服务器TCP配置的节点
        String tcpPath = TcpConstants.IM_CORE_ZK_ROOT + TcpConstants.IM_CORE_ZK_ROOT_TCP + "/" + ip + ":"
                + tcpConfig.getTcpPort();
        zkUtil.createTmpNode(tcpPath);
        log.info("注册Zookeeper TCP路径成功，msg=[{}]", tcpPath);

        // 创建服务器WebSocket配置的节点
        String webPath = TcpConstants.IM_CORE_ZK_ROOT + TcpConstants.IM_CORE_ZK_ROOT_WEB + "/" + ip + ":"
                + tcpConfig.getWebSocketPort();
        zkUtil.createTmpNode(webPath);
        log.info("注册Zookeeper WebSocket路径成功，msg=[{}]", webPath);
    }

    /**
     * 创建 ZooKeeper 的根节点，例如 "im-coreRoot/tcp"
     */
    public void createRootNode() {
        String rootPath = TcpConstants.IM_CORE_ZK_ROOT;
        zkUtil.createNode(rootPath);
        log.info("注册Zookeeper 根路径成功, msg = [{}]", rootPath);

        String tcpPath = rootPath + TcpConstants.IM_CORE_ZK_ROOT_TCP;
        zkUtil.createNode(tcpPath);
        log.info("注册Zookeeper tcp路径成功, msg = [{}]", tcpPath);

        String webPath = rootPath + TcpConstants.IM_CORE_ZK_ROOT_WEB;
        zkUtil.createNode(webPath);
        log.info("注册Zookeeper webSocket路径成功, msg = [{}]", webPath);
    }
}
