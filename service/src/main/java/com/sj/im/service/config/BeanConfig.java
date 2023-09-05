/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.config;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.enums.ImUrlRouteWayEnum;
import com.sj.im.common.enums.RouteHashMethodEnum;
import com.sj.im.common.route.RouteHandle;
import com.sj.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.sj.im.service.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Bean 配置类，用于定义应用程序中的 Bean
 */
@Slf4j
@Configuration
public class BeanConfig {
    @Resource
    private AppConfig appConfig;

    /**
     * 创建一个ZkClient对象的Bean
     *
     * @return ZkClient实例
     */
    @Bean
    public ZkClient buildZkClient() {
        // 使用配置中的ZooKeeper地址和连接超时时间创建一个新的ZkClient实例
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
    }

    /**
     * 通过 @Bean 注解，将 SnowflakeIdWorker 实例化并交由 Spring 管理
     * 参数 0 表示机器 ID，可以根据实际情况进行配置
     *
     * @return SnowflakeIdWorker实例
     */
    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq() {
        return new SnowflakeIdWorker(0);
    }

    /**
     * 定义 EasySqlInjector Bean。
     *
     * @return EasySqlInjector 实例
     */
    @Bean
    public EasySqlInjector easySqlInjector() {
        return new EasySqlInjector();
    }

    /**
     * 负载均衡策略
     */
    @Bean
    public RouteHandle routeHandle() throws Exception {
        // 获取配置文件中使用的哪个路由策略
        Integer imRouteWay = appConfig.getImRouteWay();
        // 通过配置文件中的路由策略的代表值去Enum获取到具体路径的类
        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);
        // 使用的路由策略的具体的路径
        String routeWay = Objects.requireNonNull(handler).getClazz();
        // 通过反射拿到路由策略的类
        RouteHandle routeHandle = (RouteHandle) Class.forName(routeWay).newInstance();

        // 如果是hash策略的话，还要搞一个具体的hash算法
        if (ObjectUtil.equal(handler, ImUrlRouteWayEnum.HASH)) {
            // 通过反射拿到ConsistentHashHandle中的方法
            Method setHash = Class.forName(routeWay).getMethod("setHash", AbstractConsistentHash.class);
            // 获取配置中设置的一致性哈希算法
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            // 获取一致性哈希算法对应的类名
            String hashWay = Objects.requireNonNull(RouteHashMethodEnum.getHandler(consistentHashWay)).getClazz();
            // 使用反射创建一致性哈希算法对应的实例
            AbstractConsistentHash consistentHash = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
            // 将一致性哈希算法实例设置到路由实例中
            setHash.invoke(routeHandle, consistentHash);
        }
        return routeHandle;
    }
}
