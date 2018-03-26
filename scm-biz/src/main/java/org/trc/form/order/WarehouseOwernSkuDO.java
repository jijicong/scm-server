package org.trc.form.order;

import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;

import java.util.List;

public class WarehouseOwernSkuDO {

    /**
     * 仓库类型:
     */
    private String warehouseType;

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

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }
}
