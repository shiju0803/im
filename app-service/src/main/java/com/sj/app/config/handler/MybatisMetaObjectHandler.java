/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * MybatisPlus配置类（修复createTime、updateTime不自动更新）
 *
 * @author ShiJu
 * @version 1.0
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        insertOrUpdateFill(metaObject, true);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        insertOrUpdateFill(metaObject, false);
    }

    private void insertOrUpdateFill(MetaObject metaObject, boolean isInsert) {
        if (isInsert) {
            if (Objects.isNull(getFieldValByName("createTime", metaObject))) {
                this.setFieldValByName("createTime", new Date(), metaObject);
            }
        } else {
            if (Objects.isNull(getFieldValByName("updateTime", metaObject))) {
                this.setFieldValByName("updateTime", new Date(), metaObject);
            }
        }
    }
}