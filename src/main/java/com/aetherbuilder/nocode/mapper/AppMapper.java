package com.aetherbuilder.nocode.mapper;

import com.aetherbuilder.nocode.model.entity.App;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用Mapper接口
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {
}
