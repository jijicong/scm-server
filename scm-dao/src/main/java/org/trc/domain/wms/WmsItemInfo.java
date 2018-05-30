package org.trc.domain.wms;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by hzcyn on 2018/5/30.
 */
public class WmsItemInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String warehouseCode;

    private String skuCode;

    private String skuName;

    private String ownerId;

    private String outSkuCode;

    private String barCode;

    private String specNatureInfo;

    private String spuCode;

    private long categoryId;

    private long brandId;

    private long realInventory;

    private long realDefectiveInventory;

    private long lockInventory;

    private long lockDefectiveInventory;

    private long lockAllocateInventory;

    private long lockAllocateDefectiveInventory;

    private long onWayInventory;

    private long defectiveOnWayInventory;

    private long frozenInventory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOutSkuCode() {
        return outSkuCode;
    }

    public void setOutSkuCode(String outSkuCode) {
        this.outSkuCode = outSkuCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    public long getRealInventory() {
        return realInventory;
    }

    public void setRealInventory(long realInventory) {
        this.realInventory = realInventory;
    }

    public long getRealDefectiveInventory() {
        return realDefectiveInventory;
    }

    public void setRealDefectiveInventory(long realDefectiveInventory) {
        this.realDefectiveInventory = realDefectiveInventory;
    }

    public long getLockInventory() {
        return lockInventory;
    }

    public void setLockInventory(long lockInventory) {
        this.lockInventory = lockInventory;
    }

    public long getLockDefectiveInventory() {
        return lockDefectiveInventory;
    }

    public void setLockDefectiveInventory(long lockDefectiveInventory) {
        this.lockDefectiveInventory = lockDefectiveInventory;
    }

    public long getLockAllocateInventory() {
        return lockAllocateInventory;
    }

    public void setLockAllocateInventory(long lockAllocateInventory) {
        this.lockAllocateInventory = lockAllocateInventory;
    }

    public long getLockAllocateDefectiveInventory() {
        return lockAllocateDefectiveInventory;
    }

    public void setLockAllocateDefectiveInventory(long lockAllocateDefectiveInventory) {
        this.lockAllocateDefectiveInventory = lockAllocateDefectiveInventory;
    }

    public long getOnWayInventory() {
        return onWayInventory;
    }

    public void setOnWayInventory(long onWayInventory) {
        this.onWayInventory = onWayInventory;
    }

    public long getDefectiveOnWayInventory() {
        return defectiveOnWayInventory;
    }

    public void setDefectiveOnWayInventory(long defectiveOnWayInventory) {
        this.defectiveOnWayInventory = defectiveOnWayInventory;
    }

    public long getFrozenInventory() {
        return frozenInventory;
    }

    public void setFrozenInventory(long frozenInventory) {
        this.frozenInventory = frozenInventory;
    }

}
