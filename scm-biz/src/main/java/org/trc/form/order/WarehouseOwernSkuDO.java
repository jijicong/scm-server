package org.trc.form.order;

import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;

import java.util.List;

public class WarehouseOwernSkuDO {

    private WarehouseInfo warehouseInfo;

    private List<WarehouseItemInfo> warehouseItemInfoList;

    public WarehouseInfo getWarehouseInfo() {
        return warehouseInfo;
    }

    public void setWarehouseInfo(WarehouseInfo warehouseInfo) {
        this.warehouseInfo = warehouseInfo;
    }

    public List<WarehouseItemInfo> getWarehouseItemInfoList() {
        return warehouseItemInfoList;
    }

    public void setWarehouseItemInfoList(List<WarehouseItemInfo> warehouseItemInfoList) {
        this.warehouseItemInfoList = warehouseItemInfoList;
    }
}
