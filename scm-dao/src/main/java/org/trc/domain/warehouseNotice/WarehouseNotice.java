package org.trc.domain.warehouseNotice;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * 入库通知单信息
 * Created by sone on 2017/7/10.
 */
public class WarehouseNotice {

    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'入库通知单编号',
    @FormParam("warehouseNoticeCode")
    @NotEmpty
    @Length(max = 64, message = "入库通知的编码字母和数字不能超过64个,汉字不能超过32个")
    private String warehouseNoticeCode;
    //'采购单编号',
    @FormParam("purchaseOrderCode")
    @NotEmpty
    @Length(max = 32, message = "采购订单的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    //采购订单id
    @Transient
    private Long purhcaseOrderId;
    //'采购合同编号',
    @FormParam("contractCode")
    /*@NotEmpty*/
    @Length(max = 32, message = "采购合同的编码字母和数字不能超过32个,汉字不能超过16个")
    private String contractCode;
    @Transient //采购组名称
    private String purchaseGroupName;
    //'归属采购组编号',
    @FormParam("purchaseGroupCode")
    /*@NotEmpty*/
    @Length(max = 32, message = "采购组的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseGroupCode;
    //'所在仓库id',
    @FormParam("warehouseId")
    private Long warehouseId;
    //'仓库编号',
    @FormParam("warehouseCode")
    @NotEmpty
    @Length(max = 32, message = "仓库的编码字母和数字不能超过32个,汉字不能超过16个")
    private String warehouseCode;
    //'状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
    @Transient
    private String warehouseName;
    @FormParam("status")
    @NotEmpty
    @Length(max = 2, message = "状态字母和数字不能超过2个")
    private String status;
    //'供应商id',
    @FormParam("supplierId")
    private Long supplierId;
    //'供应商编号',
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编码字母和数字不能超过32个,汉字不能超过16个")
    private String supplierCode;
    //供应商名称
    @Transient
    private String supplierName;
    //'采购类型编号',
    @FormParam("purchaseType")
    @NotEmpty
    @Length(max = 32, message = "采购类型字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseType;
    //货主ID
    @FormParam("channelCode")
    private String channelCode;
    //仓库信息主键
    @FormParam("warehouseInfoId")
    private Long warehouseInfoId;
    @Transient
    private String purchaseTypeName;
    //'归属采购人编号',
    private String purchasePersonId;
    @Transient //归属采购人名称
    private String purchasePersonName;
    //'提运单号',
    @FormParam("takeGoodsNo")
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
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
    
    /**
     * scm2.0新增字段 
     **/
    //仓储系统入库单编码,入库单申请时返回
    private String entryOrderId;
    //货主编码
    private String ownerCode;
    //奇门仓库编码
    private String qimenWarehouseCode;
    //发件人
    private String sender;
    //收货人手机
    private String receiverNumber;
    //收货人
    private String receiver;
    //发件人所在省
    private String senderProvince;
    //发件人所在城市
    private String senderCity;
    //发件人手机
    private String senderNumber;
    //发件方详细地址
    private String senderAddress;
    //收件方省份
    private String receiverProvince;
    //收件方地址
    private String receiverAddress;
    //收件方城市
    private String receiverCity;
    // 仓库接收失败原因
    private String failureCause;
    //异常原因
    private String exceptionCause;

    public String getExceptionCause() {
		return exceptionCause;
	}

	public void setExceptionCause(String exceptionCause) {
		this.exceptionCause = exceptionCause;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public String getEntryOrderId() {
		return entryOrderId;
	}

	public void setEntryOrderId(String entryOrderId) {
		this.entryOrderId = entryOrderId;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public String getQimenWarehouseCode() {
		return qimenWarehouseCode;
	}

	public void setQimenWarehouseCode(String qimenWarehouseCode) {
		this.qimenWarehouseCode = qimenWarehouseCode;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiverNumber() {
		return receiverNumber;
	}

	public void setReceiverNumber(String receiverNumber) {
		this.receiverNumber = receiverNumber;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
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

	public String getReceiverProvince() {
		return receiverProvince;
	}

	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverCity() {
		return receiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}

	public String getPurchaseGroupName() {
        return purchaseGroupName;
    }

    public void setPurchaseGroupName(String purchaseGroupName) {
        this.purchaseGroupName = purchaseGroupName;
    }

    public String getPurchasePersonName() {
        return purchasePersonName;
    }

    public void setPurchasePersonName(String purchasePersonName) {
        this.purchasePersonName = purchasePersonName;
    }

    public String getPurchaseTypeName() {
        return purchaseTypeName;
    }

    public void setPurchaseTypeName(String purchaseTypeName) {
        this.purchaseTypeName = purchaseTypeName;
    }

    public Long getPurhcaseOrderId() {
        return purhcaseOrderId;
    }

    public void setPurhcaseOrderId(Long purhcaseOrderId) {
        this.purhcaseOrderId = purhcaseOrderId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Long getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(Long warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }
}
