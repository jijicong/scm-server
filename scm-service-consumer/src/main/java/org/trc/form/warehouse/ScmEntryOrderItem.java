package org.trc.form.warehouse;

public class ScmEntryOrderItem {

    /**
     * 货主编码
     */
    private String ownerCode;

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
     * 商品数量
     * @return
     */
    private Long planQty;

    /**
     * 库存类型
     * @return
     */
    private String inventoryType;

    /**
     * 商品状态
     * @return
     */
    private String goodsStatus;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

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

    public Long getPlanQty() {
        return planQty;
    }

    public void setPlanQty(Long planQty) {
        this.planQty = planQty;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(String goodsStatus) {
        this.goodsStatus = goodsStatus;
    }
}
