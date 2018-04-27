package org.trc.mapper.warehouseInfo;

import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzcyn on 2017/11/16.
 */
public interface IWarehouseItemInfoMapper extends BaseMapper<WarehouseItemInfo> {

    int selectWarehouseItemInfoCount(Map<String, Object> map);

    List<WarehouseItemInfo> selectWarehouseItemInfo(Map<String, Object> map);
}
