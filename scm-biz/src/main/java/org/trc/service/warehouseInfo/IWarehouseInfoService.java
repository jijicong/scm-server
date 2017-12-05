package org.trc.service.warehouseInfo;

import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyz on 2017/11/15.
 */
public interface IWarehouseInfoService extends IBaseService<WarehouseInfo,Long> {
    List<WarehouseInfo> selectWarehouseInfo(Map<String, String> map);
}
