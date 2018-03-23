package org.trc.form.warehouse;

import java.util.Date;
import java.util.List;

public class ScmEntryOrderCreateRequest  extends ScmWarehouseRequestBase{

    /**
     * 入库单号
     */
    private String entryOrderCode;

    /**
     * 采购单号
     */
    private String purchaseOrderCode;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 业务类型
     */
    private String orderType;

    /**
     * 运单号
     */
    private String expressCode;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

    /**
     * 预期到货时间
     */
    private Date expectStartTime;

    /**
     * 最迟预期到货时间
     */
    private Date expectEndTime;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 发件人姓名
     */
    private String senderName;

    /**
     * 发件人移动电话
     */
    private String senderMobile;

    /**
     * 发件人省份
     */
    private String senderProvince;

    /**
     * 发件人城市
     */
    private String senderCity;

    /**
     * 发件人详细地址
     */
    private String senderDetailAddress;

    /**
     * 收件人姓名
     */
    private String reciverName;

    /**
     * 收件人移动电话
     */
    private String reciverMobile;

    /**
     * 收件人省份
     */
    private String reciverProvince;

    /**
     * 收件人城市
     */
    private String reciverCity;

    /**
     * 收件人详细地址
     */
    private String reciverDetailAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 单据类型
     */
    private String billType;

    /**
     * 采购单类型
     */
    private String poType;

    /**
     * 提货单号
     */
    private String billOfLading;

    /**
     * 入库单商品
     */
    private List<ScmEntryOrderItem> entryOrderItemList;

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public Date getExpectStartTime() {
        return expectStartTime;
    }

    public void setExpectStartTime(Date expectStartTime) {
        this.expectStartTime = expectStartTime;
    }

    public Date getExpectEndTime() {
        return expectEndTime;
    }

    public void setExpectEndTime(Date expectEndTime) {
        this.expectEndTime = expectEndTime;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getSenderProvince() {
        return senderProvince;
    }

    public void setSenderProvince(String senderProvince) {
        this.senderProvince = senderProvince;
    }

    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }

    public String getSenderDetailAddress() {
        return senderDetailAddress;
    }

    public void setSenderDetailAddress(String senderDetailAddress) {
        this.senderDetailAddress = senderDetailAddress;
    }

    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public String getReciverMobile() {
        return reciverMobile;
    }

    public void setReciverMobile(String reciverMobile) {
        this.reciverMobile = reciverMobile;
    }

    public String getReciverProvince() {
        return reciverProvince;
    }

    public void setReciverProvince(String reciverProvince) {
        this.reciverProvince = reciverProvince;
    }

    public String getReciverCity() {
        return reciverCity;
    }

    public void setReciverCity(String reciverCity) {
        this.reciverCity = reciverCity;
    }

    public String getReciverDetailAddress() {
        return reciverDetailAddress;
    }

    public void setReciverDetailAddress(String reciverDetailAddress) {
        this.reciverDetailAddress = reciverDetailAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }

    public String getBillOfLading() {
        return billOfLading;
    }

    public void setBillOfLading(String billOfLading) {
        this.billOfLading = billOfLading;
    }

    public List<ScmEntryOrderItem> getEntryOrderItemList() {
        return entryOrderItemList;
    }

    public void setEntryOrderItemList(List<ScmEntryOrderItem> entryOrderItemList) {
        this.entryOrderItemList = entryOrderItemList;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }
}
