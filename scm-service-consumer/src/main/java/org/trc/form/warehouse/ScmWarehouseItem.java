package org.trc.form.warehouse;

public class ScmWarehouseItem {

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 仓储系统商品编码(仓库商品ID，
     首次发送时不用填写，后续发送必须填写)
     */
    private String itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 商品类型
     */
    private String itemType;

    /**
     * 商品货号
     */
    private String goodsCode;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 商品规格
     */
    private String skuProperty;

    /**
     * 保质期天数
     */
    private Integer saveDays;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getSaveDays() {
        return saveDays;
    }

    public void setSaveDays(Integer saveDays) {
        this.saveDays = saveDays;
    }

    public String getSkuProperty() {
        return skuProperty;
    }

    public void setSkuProperty(String skuProperty) {
        this.skuProperty = skuProperty;
    }
}
