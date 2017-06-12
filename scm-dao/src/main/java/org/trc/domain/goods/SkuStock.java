package org.trc.domain.goods;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class SkuStock extends ScmDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("spuCode")
    @NotEmpty
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @FormParam("skuCode")
    @NotEmpty
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    @FormParam("supplierId")
    private Long supplierId;
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编号长度不能超过32个")
    private String supplierCode;
    @FormParam("warehouseId")
    @NotEmpty
    private Long warehouseId;
    @FormParam("warehouseCode")
    @NotEmpty
    @Length(max = 32, message = "仓库编号长度不能超过32个")
    private String warehouseCode;
    @FormParam("warehouseItemId")
    @NotEmpty
    @Length(max = 32, message = "仓库对应itemId长度不能超过32个")
    private String warehouseItemId;
    @FormParam("availableInventory")
    @NotEmpty
    private Long availableInventory;
    @FormParam("frozenInventory")
    @NotEmpty
    private Long frozenInventory;
    @FormParam("realInventory")
    @NotEmpty
    private Long realInventory;
    @FormParam("defectiveInventory")
    @NotEmpty
    private Long defectiveInventory;

    @FormParam("isValid")
    @NotEmpty
    private String isValid;


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

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}