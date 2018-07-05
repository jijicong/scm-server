package org.trc.service.warehouse;

import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.WarehouseTypeEnum;
import org.trc.form.warehouse.ScmInventoryQueryResponse;

import java.util.List;

public interface IWarehouseExtService {

    /**
     * 获取所有仓库
     * @return
     */
    List<WarehouseInfo> getWarehouseInfo();

    /**
     * 获取商品库存
     * @param skuCodes
     * @return
     */
    List<ScmInventoryQueryResponse> getWarehouseInventory(List<String> skuCodes,String inventoryType);

    /**
     * 获取商品库存
     * @param warehouseInfoList
     * @param warehouseItemInfoList
     * @param inventoryType
     * @return
     */
    List<ScmInventoryQueryResponse> getWarehouseInventory(List<WarehouseInfo> warehouseInfoList, List<WarehouseItemInfo> warehouseItemInfoList, String inventoryType);

    /**
     * 查询商品对应的仓储信息
     * @param skuCodes sku编码列表
     * @param warehouseInfoIds 相关仓库ID
     * @return
     */
    List<WarehouseItemInfo> getWarehouseItemInfo(List<String> skuCodes, List<String> warehouseInfoIds);
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

    /**
     * 根据sku获取仓库绑定的商品信息
     * @param skuCodes
     * @return
     */
    List<WarehouseItemInfo> getWarehouseItemInfos(List<String> skuCodes);

}
