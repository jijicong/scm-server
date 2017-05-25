package org.trc.domain.purchase;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购订单信息
 * Created by sone on 2017/5/25.
 */
public class PurchaseOrder extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("purchaseOrderCode")
    @Length(max = 32, message = "采购单编号字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    @FormParam("channelId")
    private Long channelId;//TODO
    @FormParam("channelCode")
    private String channelCode;//TODO
    @FormParam("supplierId")
    private Long supplierId;//TODO
    @FormParam("supplierCode")
    @NotEmpty
    private String supplierCode;
    @FormParam("contractId") //TODO
    private Long contractId ;
    @FormParam("contractCode") //TODO "采购合同编号',
    private String contractCode;
    @FormParam("purchaseType")
    @NotEmpty
    private String purchaseType;
    @FormParam("payType")
    @NotEmpty
    private String payType;
    @FormParam("paymentProportion")
    private BigDecimal paymentProportion; //decimal(4,3)
    @FormParam("purchaseGroupCode")
    @NotEmpty
    private String purchaseGroupCode;//'归属采购组编号'
    @FormParam("warehouseId")
    @NotEmpty
    private String warehouseId;
    @FormParam("currencyType")
    @NotEmpty
    private String currencyType;
    @FormParam("purchasePersonId")
    @NotEmpty
    private String purchasePersonId;
    @FormParam("receiveAddress")
    @NotEmpty
    private String receiveAddress;
    @FormParam("warehouseCode")
    @NotEmpty
    private String warehouseCode;
    @FormParam("transportFeeDestId")
    @NotEmpty
    private String transportFeeDestId;
    @FormParam("takeGoodsNo")
    @NotEmpty
    private String takeGoodsNo; //提运单号
    @FormParam("requriedReceiveDate")
    @NotEmpty
    private Date requriedReceiveDate;
    @FormParam("endReceiveDate")
    @NotEmpty
    private Date endReceiveDate;
    @FormParam("handlerPriority")
    @NotEmpty
    private Integer handlerPriority;//处理优先级
    @FormParam("status")
    private String status;//状态:0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-全部收货,5-收货异常,6-冻结,7-作废',
    @FormParam("enterWarehouseNotice")
    private String enterWarehouseNotice;//'入库通知:0-待通知,1-已通知',
    @FormParam("enterWarehouseNotice")
    private String virtualEnterWarehouse;// '虚拟入库:0-待入库,1-已入库',
    @FormParam("remark")
    @Length(max = 3072, message = "采购单编号字母和数字不能超过3072个,汉字不能超过3072个")
    private String remark;
    @FormParam("totalFee")
    @NotEmpty
    private Long totalFee;//'采购总金额,单位/分',
    @FormParam("abnormalRemark")
    @Length(max = 1024, message = "采购单编号字母和数字不能超过1024个,汉字不能超过512个")
    private String abnormalRemark;//入库异常说明

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPaymentProportion() {
        return paymentProportion;
    }

    public void setPaymentProportion(BigDecimal paymentProportion) {
        this.paymentProportion = paymentProportion;
    }

    public String getPurchaseGroupCode() {
        return purchaseGroupCode;
    }

    public void setPurchaseGroupCode(String purchaseGroupCode) {
        this.purchaseGroupCode = purchaseGroupCode;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getPurchasePersonId() {
        return purchasePersonId;
    }

    public void setPurchasePersonId(String purchasePersonId) {
        this.purchasePersonId = purchasePersonId;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getTransportFeeDestId() {
        return transportFeeDestId;
    }

    public void setTransportFeeDestId(String transportFeeDestId) {
        this.transportFeeDestId = transportFeeDestId;
    }

    public String getTakeGoodsNo() {
        return takeGoodsNo;
    }

    public void setTakeGoodsNo(String takeGoodsNo) {
        this.takeGoodsNo = takeGoodsNo;
    }

    public Date getRequriedReceiveDate() {
        return requriedReceiveDate;
    }

    public void setRequriedReceiveDate(Date requriedReceiveDate) {
        this.requriedReceiveDate = requriedReceiveDate;
    }

    public Date getEndReceiveDate() {
        return endReceiveDate;
    }

    public void setEndReceiveDate(Date endReceiveDate) {
        this.endReceiveDate = endReceiveDate;
    }

    public Integer getHandlerPriority() {
        return handlerPriority;
    }

    public void setHandlerPriority(Integer handlerPriority) {
        this.handlerPriority = handlerPriority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnterWarehouseNotice() {
        return enterWarehouseNotice;
    }

    public void setEnterWarehouseNotice(String enterWarehouseNotice) {
        this.enterWarehouseNotice = enterWarehouseNotice;
    }

    public String getVirtualEnterWarehouse() {
        return virtualEnterWarehouse;
    }

    public void setVirtualEnterWarehouse(String virtualEnterWarehouse) {
        this.virtualEnterWarehouse = virtualEnterWarehouse;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public String getAbnormalRemark() {
        return abnormalRemark;
    }

    public void setAbnormalRemark(String abnormalRemark) {
        this.abnormalRemark = abnormalRemark;
    }
}
