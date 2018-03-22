package org.trc.form.warehouse;

import java.util.List;

/**
 * 仓库商品同步参数
 */
public class ScmItemSyncRequest extends ScmWarehouseRequestBase{

    /**
     * 操作类型：add-新增|update-更新
     */
    private String actionType;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 仓库商品信息
     */
    private List<ScmWarehouseItem> warehouseItemList;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public List<ScmWarehouseItem> getWarehouseItemList() {
        return warehouseItemList;
    }

    public void setWarehouseItemList(List<ScmWarehouseItem> warehouseItemList) {
        this.warehouseItemList = warehouseItemList;
    }


}
