package org.trc.form.warehouse;

public class ScmEntryOrderDetailResponseItem {

    /**
     * 库存类型
     * @return
     */
    private String inventoryType;

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
     * 应收商品数量
     * @return
     */
    private Long planQty;

    /**
     * 实收数量
     * @return
     */
    private Long actualQty;

    /**
     * 残损数量
     * @return
     */
    private Long damagedQty;

    /**
     * 缺货数量
     * @return
     */
    private Long shortQty;

    /**
     * 空盒数量
     * @return
     */
    private Long emptyQty;

    /**
     * 其他差异数量
     * @return
     */
    private Long otherQty;

    /**
     * 过期数量
     * @return
     */
    private Long expiredQty;

    /**
     * 商品状态
     */
    private String goodsStatus;

    /**
     * 差异备注
     */
    private String remark;

    /**
     * 商品生产日期
     */
    private String productDate;

    /**
     * 商品过期日期
     */
    private String expireDate;

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
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

    public Long getActualQty() {
        return actualQty;
    }

    public void setActualQty(Long actualQty) {
        this.actualQty = actualQty;
    }

    public Long getDamagedQty() {
        return damagedQty;
    }

    public void setDamagedQty(Long damagedQty) {
        this.damagedQty = damagedQty;
    }

    public Long getShortQty() {
        return shortQty;
    }

    public void setShortQty(Long shortQty) {
        this.shortQty = shortQty;
    }

    public Long getEmptyQty() {
        return emptyQty;
    }

    public void setEmptyQty(Long emptyQty) {
        this.emptyQty = emptyQty;
    }

    public Long getOtherQty() {
        return otherQty;
    }

    public void setOtherQty(Long otherQty) {
        this.otherQty = otherQty;
    }

    public Long getExpiredQty() {
        return expiredQty;
    }

    public void setExpiredQty(Long expiredQty) {
        this.expiredQty = expiredQty;
    }

    public String getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(String goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
