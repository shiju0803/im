/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.message.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.github.jeffreyning.mybatisplus.base.MppSqlInjector;

import java.util.List;

/**
 * 自定义的 SQL 注入器，扩展自默认的 SQL 注入器（DefaultSqlInjector）。
 * 由于com.github.jeffreyning.mybatisplus-plus中bean重复了，这里改为继承MppSqlInjector，然后进行覆盖
 */
public class EasySqlInjector extends MppSqlInjector {

    /**
     * 重写父类的 getMethodList 方法，用于获取映射器类中定义的方法列表。
     *
     * @param mapperClass 映射器类
     * @return 方法列表
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 获取父类的方法列表
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);

        // 添加自定义的 InsertBatchSomeColumn 方法
        methodList.add(new InsertBatchSomeColumn());

        // 返回更新后的方法列表
        return methodList;
    }
}