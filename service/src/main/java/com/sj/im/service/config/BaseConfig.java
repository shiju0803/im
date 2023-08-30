/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.config;

import cn.hutool.core.util.ObjectUtil;
import com.sj.im.common.config.AppConfig;
import com.sj.im.common.enums.ImUrlRouteWayEnum;
import com.sj.im.common.enums.RouteHashMethodEnum;
import com.sj.im.common.route.RouteHandle;
import com.sj.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
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
 * @description: 配置类
 */
@Slf4j
@Configuration
public class BaseConfig {
    @Resource
    private AppConfig appConfig;

    /**
     * ZK配置类
     */
    @Bean
    public ZkClient buildZkClient() {
        return new ZkClient(appConfig.getZkAddr(), appConfig.getZkConnectTimeOut());
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
            // 从配置文件中拿到指定hash算法的代表值
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            // 具体hash算法的类的路径
            String hashWay = Objects.requireNonNull(RouteHashMethodEnum.getHandler(consistentHashWay)).getClazz();
            // 通过反射拿到hash算法
            AbstractConsistentHash consistentHash = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
            setHash.invoke(routeHandle, consistentHash);
        }
        return routeHandle;
    }
}
