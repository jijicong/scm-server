package org.trc.service.jingdong;

import org.trc.domain.config.TableMappingDO;
import org.trc.service.IBaseService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
public interface ITableMappingService extends IBaseService<TableMappingDO, Long> {
    String selectByCode(String code);
}
