package org.trc.mapper.warehouseInfo;

import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyz on 2017/11/15.
 */
public interface IWarehouseInfoMapper extends BaseMapper<WarehouseInfo> {

    int batchUpdate(Map<String, Object> map);

}
