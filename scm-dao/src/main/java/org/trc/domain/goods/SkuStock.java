package org.trc.domain.goods;

import org.trc.domain.util.ScmDO;

public class SkuStock extends ScmDO {

    private Long id;

    private String spuCode;

    private String skuCode;

    private Long supplierId;

    private String supplierCode;

    private Long warehouseId;

    private String warehouseCode;

    private String warehouseItemId;

    private Long availableInventory;

    private Long frozenInventory;

    private Long realInventory;

    private Long defectiveInventory;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode == null ? null : warehouseCode.trim();
    }

    public String getWarehouseItemId() {
        return warehouseItemId;
    }

    public void setWarehouseItemId(String warehouseItemId) {
        this.warehouseItemId = warehouseItemId == null ? null : warehouseItemId.trim();
    }

    public Long getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(Long availableInventory) {
        this.availableInventory = availableInventory;
    }

    public Long getFrozenInventory() {
        return frozenInventory;
    }

    public void setFrozenInventory(Long frozenInventory) {
        this.frozenInventory = frozenInventory;
    }

    public Long getRealInventory() {
        return realInventory;
    }

    public void setRealInventory(Long realInventory) {
        this.realInventory = realInventory;
    }

    public Long getDefectiveInventory() {
        return defectiveInventory;
    }

    public void setDefectiveInventory(Long defectiveInventory) {
        this.defectiveInventory = defectiveInventory;
    }

}