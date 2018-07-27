package org.trc.domain.purchase;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.MoneySerializer;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.math.BigDecimal;

/**
 * 采购订单信息
 * Created by sone on 2017/5/25.
 */
public class PurchaseOrder extends BaseDO{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -4743778003296714026L;
	@PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("purchaseOrderCode")
    @Length(max = 32, message = "采购单编号字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    @FormParam("channelId")
    private Long channelId;//

    @FormParam("channelCode")
    private String channelCode;//
    @FormParam("supplierId")
    private Long supplierId;//
    @FormParam("supplierCode")
    private String supplierCode;
    @Transient
    private String supplierName;
    @FormParam("contractId") //
    private Long contractId ;
    @FormParam("contractCode") //TODO "采购合同编号',
    private String contractCode;
    @FormParam("purchaseType")
    private String purchaseType;
    @Transient
    private String purchaseTypeName;
    @FormParam("payType")
    private String payType;
    @Transient
    private String payTypeName;
    @FormParam("paymentProportion")
    private BigDecimal paymentProportion; //decimal(4,3)
    @FormParam("purchaseGroupCode")
     private String purchaseGroupCode;//'归属采购组编号'
     @Transient
     private String purchaseGroupName;
     @FormParam("warehouseId")
     private Long warehouseId;
     @FormParam("currencyType")
     private String currencyType;
        @Transient
     private String currencyTypeName;
     @FormParam("purchasePersonId")
     private String purchasePersonId;
     @Transient
     private String purchasePerson;
     @FormParam("receiveAddress")
     private String receiveAddress;
     @FormParam("warehouseCode")
     private String warehouseCode;
     @FormParam("transportFeeDestId")
     private String transportFeeDestId;
    @Transient
     private String transportFeeDestIdName;
     @FormParam("takeGoodsNo")
     private String takeGoodsNo; //提运单号
     @FormParam("requriedReceiveDate")
     @Length(max = 20, message = "开始日期长度不能超过20个")
     private String requriedReceiveDate;
     @FormParam("endReceiveDate")
     @Length(max = 20, message = "截止日期长度不能超过20个")
     private String endReceiveDate;
     @FormParam("handlerPriority")
     private String handlerPriority;//处理优先级
     @Transient
     private String handlerPriorityName;
     @FormParam("status")
     private String status;//状态:0-暂存,1-审核驳回,2-审核通过,3-提交审核,4-全部收货,5-收货异常,6-冻结,7-作废,8-入库通知
     @FormParam("enterWarehouseNotice")
     private String enterWarehouseNotice;//'入库通知:0-待通知,1-已通知',
     @FormParam("enterWarehouseNotice")
     private String virtualEnterWarehouse;// '虚拟入库:0-待入库,1-已入库',
     @FormParam("remark")
     @Length(max = 3072, message = "采购单编号字母和数字不能超过3072个,汉字不能超过3072个")
     private String remark;
     @FormParam("totalFeeD")
     @Transient
     private BigDecimal totalFeeD;//'采购总金额,单位/分',
     @FormParam("totalFee")
     //@JsonSerialize(using = MoneySerializer.class)
     private BigDecimal totalFee;//'采购总金额,单位/分',
     @FormParam("abnormalRemark")
     @Length(max = 1024, message = "采购单编号字母和数字不能超过1024个,汉字不能超过512个")
     private String abnormalRemark;//入库异常说明*/
    @FormParam("receiver")
    private String receiver;//收货人
    @FormParam("warehouseInfoId")
    private Long warehouseInfoId;//仓库信息主键
    @FormParam("warehouseName")
    private String warehouseName;//仓库名称
    @FormParam("receiverNumber")
    private String receiverNumber;//收货人手机
    @FormParam("sender")
    @Length(max = 50, message = "发件人字母和数字不能超过50个")
    private String sender;//发件人
    @FormParam("senderProvince")
    private String senderProvince;//发件所在省
    @FormParam("senderCity")
    private String senderCity;//发件所在城市
    @FormParam("senderNumber")
    private String senderNumber;//发件人手机
    @FormParam("senderAddress")
    @Length(max = 100, message = "发件方详细地址字母和数字不能超过100个")
    private String senderAddress;//发件方详细地址

    /**
     * v2.5 入库状态:0-等待入库,1-全部入库,2-部分入库,3-入库异常,其他情况为null
     */
    @FormParam("warehouseNoticeStatus")
    private String warehouseNoticeStatus;

    @Transient
    private String senderProvinceName;

    @Transient
    private String senderCityName;

    @Transient
    private String noticeStatus;
    
    //审核意见  20180720 add
    @Transient
    private String auditOpinion;

    public String getAuditOpinion() {
		return auditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}

	public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public String getHandlerPriorityName() {
        return handlerPriorityName;
    }

    public void setHandlerPriorityName(String handlerPriorityName) {
        this.handlerPriorityName = handlerPriorityName;
    }

    public String getTransportFeeDestIdName() {
        return transportFeeDestIdName;
    }

    public void setTransportFeeDestIdName(String transportFeeDestIdName) {
        this.transportFeeDestIdName = transportFeeDestIdName;
    }

    public BigDecimal getTotalFeeD() {
        return totalFeeD;
    }

    public void setTotalFeeD(BigDecimal totalFeeD) {
        this.totalFeeD = totalFeeD;
    }

    public String getCurrencyTypeName() {
        return currencyTypeName;
    }

    public void setCurrencyTypeName(String currencyTypeName) {
        this.currencyTypeName = currencyTypeName;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getPurchaseTypeName() {
        return purchaseTypeName;
    }

    public void setPurchaseTypeName(String purchaseTypeName) {
        this.purchaseTypeName = purchaseTypeName;
    }

    public String getPurchaseGroupCode() {
        return purchaseGroupCode;
    }

    public void setPurchaseGroupCode(String purchaseGroupCode) {
        this.purchaseGroupCode = purchaseGroupCode;
    }

    public String getPurchaseGroupName() {
        return purchaseGroupName;
    }

    public void setPurchaseGroupName(String purchaseGroupName) {
        this.purchaseGroupName = purchaseGroupName;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
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

    public String getPurchasePerson() {
        return purchasePerson;
    }

    public void setPurchasePerson(String purchasePerson) {
        this.purchasePerson = purchasePerson;
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

    public String getHandlerPriority() {
        return handlerPriority;
    }

    public void setHandlerPriority(String handlerPriority) {
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

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }


    public String getAbnormalRemark() {
        return abnormalRemark;
    }

    public void setAbnormalRemark(String abnormalRemark) {
        this.abnormalRemark = abnormalRemark;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Long getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(Long warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderProvinceName() {
        return senderProvinceName;
    }

    public void setSenderProvinceName(String senderProvinceName) {
        this.senderProvinceName = senderProvinceName;
    }

    public String getSenderCityName() {
        return senderCityName;
    }

    public void setSenderCityName(String senderCityName) {
        this.senderCityName = senderCityName;
    }

    public String getWarehouseNoticeStatus() {
        return warehouseNoticeStatus;
    }

    public void setWarehouseNoticeStatus(String warehouseNoticeStatus) {
        this.warehouseNoticeStatus = warehouseNoticeStatus;
    }
}
