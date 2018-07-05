package org.trc.form.warehouseInfo;

import java.io.Serializable;

/**
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfoResult implements Serializable {
    //主键
    private Long id;

    private String code;

    private String remark;

    //仓库名称
    private String warehouseName;

    //仓库类型
    private String warehouseTypeCode;

    //奇门仓库编码
    private String wmsWarehouseCode;

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

    private String isValid;

    private String createOperator;

    private String operationalNature;

    private String operationalType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWarehouseTypeCode() {
        return warehouseTypeCode;
    }

    public void setWarehouseTypeCode(String warehouseTypeCode) {
        this.warehouseTypeCode = warehouseTypeCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWmsWarehouseCode() {
        return wmsWarehouseCode;
    }

    public void setWmsWarehouseCode(String wmsWarehouseCode) {
        this.wmsWarehouseCode = wmsWarehouseCode;
    }

    public String getOperationalNature() {
        return operationalNature;
    }

    public void setOperationalNature(String operationalNature) {
        this.operationalNature = operationalNature;
    }

    public String getOperationalType() {
        return operationalType;
    }

    public void setOperationalType(String operationalType) {
        this.operationalType = operationalType;
    }
}
