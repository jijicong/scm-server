package org.trc.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by george on 2017/3/31.
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
