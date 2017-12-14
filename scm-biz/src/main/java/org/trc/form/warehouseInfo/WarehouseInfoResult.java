package org.trc.form.warehouseInfo;

/**
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfoResult {
    //主键
    private Long id;

    //仓库订单表主键
    private String warehouseId;
    //仓库名称
    private String warehouseName;

    //仓库类型
    private String type;

    //奇门仓库编码
    private String qimenWarehouseCode;

    //sku数量
    private Integer skuNum;

    //货主ID
    private String ownerId;

    //仓库货主ID
    private String warehouseOwnerId;

    //货主名称
    private String ownerName;

    //货主仓库状态 0--待通知 1--通知成功 2--通知失败
    private String ownerWarehouseState;

    //创建时间
    private String createTime;

    //更新时间
    private String updateTime;

    //是否删除 0--未删除 1--已删除
    private Integer isDelete;

    private Integer isNoticeSuccess;

    private String isNoticeWarehouseItems;

    //备注
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQimenWarehouseCode() {
        return qimenWarehouseCode;
    }

    public void setQimenWarehouseCode(String qimenWarehouseCode) {
        this.qimenWarehouseCode = qimenWarehouseCode;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getWarehouseOwnerId() {
        return warehouseOwnerId;
    }

    public void setWarehouseOwnerId(String warehouseOwnerId) {
        this.warehouseOwnerId = warehouseOwnerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerWarehouseState() {
        return ownerWarehouseState;
    }

    public void setOwnerWarehouseState(String ownerWarehouseState) {
        this.ownerWarehouseState = ownerWarehouseState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsNoticeSuccess() {
        return isNoticeSuccess;
    }

    public void setIsNoticeSuccess(Integer isNoticeSuccess) {
        this.isNoticeSuccess = isNoticeSuccess;
    }

    public String getIsNoticeWarehouseItems() {
        return isNoticeWarehouseItems;
    }

    public void setIsNoticeWarehouseItems(String isNoticeWarehouseItems) {
        this.isNoticeWarehouseItems = isNoticeWarehouseItems;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
