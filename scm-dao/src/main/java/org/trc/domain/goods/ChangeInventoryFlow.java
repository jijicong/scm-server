package org.trc.domain.goods;

import java.util.Date;

public class ChangeInventoryFlow {

    private Long id;

    private String channelCode;

    private String requestCode;

    private String orderType;

    private String orderCode;

    private Long skuStockId;

    private String skuCode;

    private Long availableInventoryChange;

    private Long frozenInventoryChange;

    private Long realInventoryChange;

    private Long defectiveInventoryChange;

    private Long originalAvailableInventory;

    private Long originalFrozenInventory;

    private Long originalRealInventory;

    private Long originalDefectiveInventory;

    private Long newestAvailableInventory;

    private Long newestFrozenInventory;

    private Long newestRealInventory;

    private Long newestDefectiveInventory;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode == null ? null : requestCode.trim();
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public Long getSkuStockId() {
        return skuStockId;
    }

    public void setSkuStockId(Long skuStockId) {
        this.skuStockId = skuStockId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Long getAvailableInventoryChange() {
        return availableInventoryChange;
    }

    public void setAvailableInventoryChange(Long availableInventoryChange) {
        this.availableInventoryChange = availableInventoryChange;
    }

    public Long getFrozenInventoryChange() {
        return frozenInventoryChange;
    }

    public void setFrozenInventoryChange(Long frozenInventoryChange) {
        this.frozenInventoryChange = frozenInventoryChange;
    }

    public Long getRealInventoryChange() {
        return realInventoryChange;
    }

    public void setRealInventoryChange(Long realInventoryChange) {
        this.realInventoryChange = realInventoryChange;
    }

    public Long getDefectiveInventoryChange() {
        return defectiveInventoryChange;
    }

    public void setDefectiveInventoryChange(Long defectiveInventoryChange) {
        this.defectiveInventoryChange = defectiveInventoryChange;
    }

    public Long getOriginalAvailableInventory() {
        return originalAvailableInventory;
    }

    public void setOriginalAvailableInventory(Long originalAvailableInventory) {
        this.originalAvailableInventory = originalAvailableInventory;
    }

    public Long getOriginalFrozenInventory() {
        return originalFrozenInventory;
    }

    public void setOriginalFrozenInventory(Long originalFrozenInventory) {
        this.originalFrozenInventory = originalFrozenInventory;
    }

    public Long getOriginalRealInventory() {
        return originalRealInventory;
    }

    public void setOriginalRealInventory(Long originalRealInventory) {
        this.originalRealInventory = originalRealInventory;
    }

    public Long getOriginalDefectiveInventory() {
        return originalDefectiveInventory;
    }

    public void setOriginalDefectiveInventory(Long originalDefectiveInventory) {
        this.originalDefectiveInventory = originalDefectiveInventory;
    }

    public Long getNewestAvailableInventory() {
        return newestAvailableInventory;
    }

    public void setNewestAvailableInventory(Long newestAvailableInventory) {
        this.newestAvailableInventory = newestAvailableInventory;
    }

    public Long getNewestFrozenInventory() {
        return newestFrozenInventory;
    }

    public void setNewestFrozenInventory(Long newestFrozenInventory) {
        this.newestFrozenInventory = newestFrozenInventory;
    }

    public Long getNewestRealInventory() {
        return newestRealInventory;
    }

    public void setNewestRealInventory(Long newestRealInventory) {
        this.newestRealInventory = newestRealInventory;
    }

    public Long getNewestDefectiveInventory() {
        return newestDefectiveInventory;
    }

    public void setNewestDefectiveInventory(Long newestDefectiveInventory) {
        this.newestDefectiveInventory = newestDefectiveInventory;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}