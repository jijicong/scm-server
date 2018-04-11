package org.trc.domain.goods;

import java.io.Serializable;

public class RequestSkuStock implements Serializable {
    /**
     * 可用正品总库存
     */
    private Long availableInventory = 0L;
    /**
     * 仓库锁定正品总库存
     */
    private Long warehouseLockInventory = 0L;
    /**
     * 临期锁定正品总库存
     */
    private Long adventLockInventory = 0L;

    /**
     * 盘点锁定正品总库存
     */
    private Long checkLockInventory = 0L;
    /**
     * 残品总库存
     */
    private Long defectiveInventory = 0L;

    /**
     * 仓库名称
     */
    private String warehouseName;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Long getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(Long availableInventory) {
        this.availableInventory = availableInventory;
    }

    public Long getWarehouseLockInventory() {
        return warehouseLockInventory;
    }

    public void setWarehouseLockInventory(Long warehouseLockInventory) {
        this.warehouseLockInventory = warehouseLockInventory;
    }

    public Long getAdventLockInventory() {
        return adventLockInventory;
    }

    public void setAdventLockInventory(Long adventLockInventory) {
        this.adventLockInventory = adventLockInventory;
    }

    public Long getCheckLockInventory() {
        return checkLockInventory;
    }

    public void setCheckLockInventory(Long checkLockInventory) {
        this.checkLockInventory = checkLockInventory;
    }

    public Long getDefectiveInventory() {
        return defectiveInventory;
    }

    public void setDefectiveInventory(Long defectiveInventory) {
        this.defectiveInventory = defectiveInventory;
    }
}
