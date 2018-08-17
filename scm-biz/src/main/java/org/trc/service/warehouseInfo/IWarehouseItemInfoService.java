package org.trc.service.warehouseInfo;

import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzcyn on 2017/11/16.
 * @author hzcyn
 */
public interface IWarehouseItemInfoService extends IBaseService<WarehouseItemInfo, Long> {

    int batchUpdate(Map<String, Object> map);

    int selectWarehouseItemInfoCount(Map<String, Object> map);

    List<WarehouseItemInfo> selectWarehouseItemInfo(Map<String, Object> map);

	List<WarehouseItemInfo> selectInfoListBySkuCodeAndWarehouseCode(List<String> skuCodeList, String warehouseCode);
}
