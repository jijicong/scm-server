package org.trc.mapper.config;

import org.trc.domain.config.TableMappingDO;
import org.trc.util.BaseMapper;

/**
 * Created by hzwyz on 2017/5/24 0024.
 */
public interface ITableMappingMapper extends BaseMapper<TableMappingDO> {
    String selectByCode(String code);
}
