package org.trc.service.warehouse;

import org.trc.enums.WarehouseTypeEnum;
import org.trc.form.warehouse.ScmInventoryQueryResponse;

import java.util.List;

public interface IWarehouseExtService {

    /**
     * 获取商品库存
     * @param skuCodes
     * @return
     */
    List<ScmInventoryQueryResponse> getWarehouseInventory(List<String> skuCodes);

    /**
     * 获取仓库类型
     * @param warehouseCode
     * @return
     */
    WarehouseTypeEnum getWarehouseType(String warehouseCode);

    /**
     * 获取仓库编码
     * @param warehouseCode
     * @return
     */
    String getWmsWarehouseCode(String warehouseCode);

}
