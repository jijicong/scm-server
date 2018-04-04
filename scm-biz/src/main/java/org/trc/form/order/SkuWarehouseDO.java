package org.trc.form.order;

public class SkuWarehouseDO {

    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 商品数量
     */
    private Long itemNum;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Long getItemNum() {
        return itemNum;
    }

    public void setItemNum(Long itemNum) {
        this.itemNum = itemNum;
    }
}
