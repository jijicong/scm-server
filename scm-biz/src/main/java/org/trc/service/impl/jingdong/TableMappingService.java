package org.trc.service.impl.jingdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.config.TableMappingDO;
import org.trc.mapper.config.ITableMappingMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.jingdong.ITableMappingService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
@Service("tableMappingService")
public class TableMappingService extends BaseService<TableMappingDO, Long> implements ITableMappingService {
    @Autowired
    ITableMappingMapper iTableMappingMapper;
    @Override
    public String selectByCode(String code) {
        return iTableMappingMapper.selectByCode(code);
    }
}
