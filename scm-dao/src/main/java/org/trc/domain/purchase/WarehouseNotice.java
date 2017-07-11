package org.trc.domain.purchase;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 入库通知单信息
 * Created by sone on 2017/7/10.
 */
public class WarehouseNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'入库通知单编号',
    private String warehouseNoticeCode;
    //'采购单编号',
    private String purchaseOrderCode;
    //'采购合同编号',
    private String contractCode;
    //'归属采购组编号',
    private String purchaseGroupCode;
    //'所在仓库id',
    private Long warehouseId;
    //'仓库编号',
    private String warehouseCode;
    //'状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
    private String state;
    //'供应商id',
    private Long supplierId;
    //'供应商编号',
    private String supplierCode;
    //'采购类型编号',
    private String purchaseType;
    //'归属采购人编号',
    private String purchasePersonId;
    //'提运单号',
    private String takeGoodsNo;
    // '要求到货日期,格式:yyyy-mm-dd',
    private String requriedReceiveDate;
    //'截止到货日期,格式:yyyy-mm-dd',
    private String endReceiveDate;
    //'备注',
    private String remark;
    //'创建人',
    private String createOperator;
    //'创建时间,格式yyyy-mm-dd hh:mi:ss',
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseNoticeCode() {
        return warehouseNoticeCode;
    }

    public void setWarehouseNoticeCode(String warehouseNoticeCode) {
        this.warehouseNoticeCode = warehouseNoticeCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getPurchaseGroupCode() {
        return purchaseGroupCode;
    }

    public void setPurchaseGroupCode(String purchaseGroupCode) {
        this.purchaseGroupCode = purchaseGroupCode;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getPurchasePersonId() {
        return purchasePersonId;
    }

    public void setPurchasePersonId(String purchasePersonId) {
        this.purchasePersonId = purchasePersonId;
    }

    public String getTakeGoodsNo() {
        return takeGoodsNo;
    }

    public void setTakeGoodsNo(String takeGoodsNo) {
        this.takeGoodsNo = takeGoodsNo;
    }

    public String getRequriedReceiveDate() {
        return requriedReceiveDate;
    }

    public void setRequriedReceiveDate(String requriedReceiveDate) {
        this.requriedReceiveDate = requriedReceiveDate;
    }

    public String getEndReceiveDate() {
        return endReceiveDate;
    }

    public void setEndReceiveDate(String endReceiveDate) {
        this.endReceiveDate = endReceiveDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
