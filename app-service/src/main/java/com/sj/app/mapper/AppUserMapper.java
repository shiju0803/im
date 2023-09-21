/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sj.app.entry.AppUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUserEntity> {}
