package com.sj;

import cn.hutool.core.io.resource.ResourceUtil;
import com.sj.im.codec.config.BootstrapConfig;
import com.sj.im.tcp.listener.MqMessageListener;
import com.sj.im.tcp.redis.RedisManager;
import com.sj.im.tcp.register.RegistryZk;
import com.sj.im.tcp.register.Zkit;
import com.sj.im.tcp.server.LimServer;
import com.sj.im.tcp.server.LimWebSocketServer;
import com.sj.im.tcp.util.MqFactory;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ShiJu
 * @version 1.0
 * @description: TCP网关启动类
 */
@Slf4j
public class ImTcpServerStarter {
    public static void main(String[] args) {
        if(args.length > 0){
            start(args[0]);
        }
    }

    private static void start(String path) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = ResourceUtil.getStreamSafe(path);
            BootstrapConfig config = yaml.loadAs(inputStream, BootstrapConfig.class);

            new LimServer(config.getLim()).start();
            new LimWebSocketServer(config.getLim()).start();

            // 初始化redis
            RedisManager.init(config);
            // 初始化rabbitmq
            MqFactory.init(config.getLim().getRabbitmq());
            MqMessageListener.init();
            // 初始化Zookeeper
            registryZK(config);
        } catch (Exception e) {
            log.error("服务初始化失败：{}", e.getMessage(), e);
            System.exit(500);
        }
    }

    private static void registryZK(BootstrapConfig config) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getLim().getZkConfig().getZkAddr(),
                config.getLim().getZkConfig().getZkConnectTimeOut());
        Zkit zkit = new Zkit(zkClient);
        RegistryZk registryZk = new RegistryZk(zkit, hostAddress, config.getLim());
        new Thread(registryZk).start();
    }
}