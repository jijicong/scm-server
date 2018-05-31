package org.trc.domain.warehouseNotice;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 入库通知单信息
 * Created by sone on 2017/7/10.
 */
@Table(name = "warehouse_notice")
public class WarehouseNotice implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'入库通知单编号',
    @Column(name ="warehouse_notice_code")
    @NotEmpty
    @Length(max = 64, message = "入库通知的编码字母和数字不能超过64个,汉字不能超过32个")
    private String warehouseNoticeCode;
    //'采购单编号',
    @Column(name ="purchase_order_code")
    @NotEmpty
    @Length(max = 32, message = "采购订单的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    //采购订单id
    @Transient
    private Long purhcaseOrderId;

    //'采购合同编号',
    /*@NotEmpty*/
    @Column(name ="contract_code")
    @Length(max = 32, message = "采购合同的编码字母和数字不能超过32个,汉字不能超过16个")
    private String contractCode;
    @Transient //采购组名称
    private String purchaseGroupName;
    //'归属采购组编号',
    /*@NotEmpty*/
    @Column(name ="purchase_group_code")
    @Length(max = 32, message = "采购组的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseGroupCode;
    //'所在仓库id',
    @Column(name ="warehouse_id")
    private Long warehouseId;
    //'仓库编号',
    @Column(name ="warehouse_code")
    @NotEmpty
    @Length(max = 32, message = "仓库的编码字母和数字不能超过32个,汉字不能超过16个")
    private String warehouseCode;
    //'状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
    @Transient
    private String warehouseName;
    @Column(name ="status")
    @NotEmpty
    @Length(max = 2, message = "状态字母和数字不能超过2个")
    private String status;
    //'供应商id',
    @Column(name ="supplier_id")
    private Long supplierId;
    //'供应商编号',
    @Column(name ="supplier_code")
    @Length(max = 32, message = "供应商编码字母和数字不能超过32个,汉字不能超过16个")
    private String supplierCode;
    //供应商名称
    @Transient
    private String supplierName;
    //'采购类型编号',
    @Column(name ="purchase_type")
    @NotEmpty
    @Length(max = 32, message = "采购类型字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseType;
    //货主ID
    @Column(name ="channel_code")
    private String channelCode;
    //仓库信息主键
    @Column(name ="warehouse_info_id")
    private Long warehouseInfoId;
    @Transient
    private String purchaseTypeName;
    //'归属采购人编号',
    @Column(name ="purchase_person_id")
    private String purchasePersonId;
    @Transient //归属采购人名称
    private String purchasePersonName;
    //'提运单号',
    @Column(name ="take_goods_no")
    private String takeGoodsNo;
    // '要求到货日期,格式:yyyy-mm-dd',
    @Column(name ="requried_receive_date")
    private String requriedReceiveDate;
    //'截止到货日期,格式:yyyy-mm-dd',
    @Column(name ="end_receive_date")
    private String endReceiveDate;
    //'备注',
    @Column(name ="remark")
    private String remark;
    //'创建人',
    @Column(name ="create_operator")
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
    @Column(name ="entry_order_id")
    private String entryOrderId;
    //货主编码
    @Column(name ="owner_code")
    private String ownerCode;
    //奇门仓库编码
    @Column(name ="qimen_warehouse_code")
    private String qimenWarehouseCode;
    //发件人
    @Column(name ="sender")
    private String sender;
    //收货人手机
    @Column(name ="receiver_number")
    private String receiverNumber;
    //收货人
    @Column(name ="receiver")
    private String receiver;
    //发件人所在省
    @Column(name ="sender_province")
    private String senderProvince;
    //发件人所在城市
    @Column(name ="sender_city")
    private String senderCity;
    //发件人手机
    @Column(name ="sender_number")
    private String senderNumber;
    //发件方详细地址
    @Column(name ="sender_address")
    private String senderAddress;
    //收件方省份
    @Column(name ="receiver_province")
    private String receiverProvince;
    //收件方地址
    @Column(name ="receiver_address")
    private String receiverAddress;
    //收件方城市
    @Column(name ="receiver_city")
    private String receiverCity;
    // 仓库接收失败原因warehouseNotice
    @Column(name ="failure_cause")
    private String failureCause;
    //异常原因
    @Column(name ="exception_cause")
    private String exceptionCause;
    //完成状态：0-未完成，1-已完成 (仓库反馈收货完成的、作废的、已取消的都是已完成)
    @Column(name ="finish_status")
    private String finishStatus;

    public String getFinishStatus() {
		return finishStatus;
	}

	public void setFinishStatus(String finishStatus) {
		this.finishStatus = finishStatus;
	}

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
