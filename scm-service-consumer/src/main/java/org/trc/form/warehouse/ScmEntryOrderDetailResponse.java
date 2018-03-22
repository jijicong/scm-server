package org.trc.form.warehouse;

import java.util.Date;
import java.util.List;

public class ScmEntryOrderDetailResponse {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 入库单号
     */
    private String entryOrderCode;

    /**
     * 仓储系统入库单ID
     */
    private String entryOrderId;

    /**
     * 入库单类型
     */
    private String ntryOrderType;

    /**
     * 入库单状态
     */
    private String status;

    /**
     * 采购入库单入库状态
     */
    private String storageStatus;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 入库单生成时间
     */
    private Date createTime;

    /**
     * 供应商编号
     */
    private String supplierNo;

    /**
     * 物流开放平台采购单号
     */
    private String poOrderNo;

    /**
     * 采购入库单创建人
     */
    private String createUser;

    /**
     * 入库单商品明细
     */
    private List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

    public String getNtryOrderType() {
        return ntryOrderType;
    }

    public void setNtryOrderType(String ntryOrderType) {
        this.ntryOrderType = ntryOrderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStorageStatus() {
        return storageStatus;
    }

    public void setStorageStatus(String storageStatus) {
        this.storageStatus = storageStatus;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getPoOrderNo() {
        return poOrderNo;
    }

    public void setPoOrderNo(String poOrderNo) {
        this.poOrderNo = poOrderNo;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public List<ScmEntryOrderDetailResponseItem> getScmEntryOrderDetailResponseItemList() {
        return scmEntryOrderDetailResponseItemList;
    }

    public void setScmEntryOrderDetailResponseItemList(List<ScmEntryOrderDetailResponseItem> scmEntryOrderDetailResponseItemList) {
        this.scmEntryOrderDetailResponseItemList = scmEntryOrderDetailResponseItemList;
    }
}
