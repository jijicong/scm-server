package org.trc.domain.allocateOrder;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Table(name = "allocate_in_order")
public class AllocateInOrder extends AllocateOutInOrderBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = -929818832773422166L;

	/**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 调拨出库单号
     */
    @Length(max = 32, message = "调拨出库单号不得超过32个字符")
    @Column(name = "allocate_in_order_code")
    private String allocateInOrderCode;

    /**
     * 调拨单编号
     */
    @Length(max = 32, message = "调拨单编号不得超过32个字符")
    @Column(name = "allocate_order_code")
    private String allocateOrderCode;

    /**
     * 供应商名称
     */
    @Length(max = 256, message = "供应商名称不得超过256个字符")
    @Column(name = "supplier_name")
    private String supplierName;

    /**
     * 供应商编码
     */
    @Length(max = 32, message = "供应商编码不得超过32个字符")
    @Column(name = "supplier_code")
    private String supplierCode;

    /**
     * 0-待完成出库,1-出库完成,2-出库异常,3-入库仓接收成功,4-入库仓接收失败,5-入库完成,6-入库异常,7-已取消
     */
    @Length(max = 2, message = "调拨入库单状态不得超过2个字符")
    private String status;

    /**
     * 预约到货日期,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "pre_recive_date")
    private Date preReciveDate;

    /**
     * 收货人
     */
    @Length(max = 64, message = "收货人名称不得超过64个字符")
    private String receiver;

    /**
     * 收货人手机
     */
    @Length(max = 16, message = "收货人手机不得超过16个字符")
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @Length(max = 256, message = "收货地址不得超过256个字符")
    @Column(name = "receiver_address")
    private String receiverAddress;

    /**
     * 收货人手机
     */
    @Length(max = 16, message = "收货人手机不得超过16个字符")
    @Column(name = "receiver_mobile")
    private String receiverMobile;

    /**
     * 发件人
     */
    @Length(max = 64, message = "发件人不得超过64个字符")
    private String sender;

    /**
     * 发件人手机
     */
    @Length(max = 16, message = "发件人手机不得超过16个字符")
    @Column(name = "sender_phone")
    private String senderPhone;

    /**
     * 发件人手机
     */
    @Length(max = 16, message = "发件人手机不得超过32个字符")
    @Column(name = "sender_mobile")
    private String senderMobile;

    /**
     * 发件方详细地址
     */
    @Length(max = 32, message = "品牌名称不得超过32个字符")
    @Column(name = "sender_address")
    private String senderAddress;

    /**
     * 发货单号
     */
    @Length(max = 128, message = "发货单号不得超过128个字符")
    @Column(name = "delivery_number")
    private String deliveryNumber;

    /**
     * 入库备注
     */
    @Length(max = 2048, message = "入库备注不得超过2048个字符")
    @Column(name = "in_memo")
    private String inMemo;

    /**
     * 备注
     */
    @Length(max = 2048, message = "备注不得超过2048个字符")
    private String memo;

    /**
     * 是否有效:0-无效,1-有效
     */
    @Length(max = 2, message = "是否有效不得超过32个字符")
    @Column(name = "is_valid")
    private String isValid;

    /**
     * sku明细
     */
    @Transient
    private List<AllocateSkuDetail> skuDetailList;
    
    /**
     * 失败原因
     */
    @Column(name = "failed_cause")
    private String failedCause;
    
    /**
     * 调拨入库单序号-京东仓库重新发货用
     */
    @Column(name = "in_order_seq")
    private Integer inOrderSeq;
    
    /**
     * 仓库入库单号
     */
    @Column(name = "wms_allocate_in_order_code")
    private String wmsAllocateInOrderCode;

	public Integer getInOrderSeq() {
		return inOrderSeq;
	}

	public void setInOrderSeq(Integer inOrderSeq) {
		this.inOrderSeq = inOrderSeq;
	}

	public String getWmsAllocateInOrderCode() {
		return wmsAllocateInOrderCode;
	}

	public void setWmsAllocateInOrderCode(String wmsAllocateInOrderCode) {
		this.wmsAllocateInOrderCode = wmsAllocateInOrderCode;
	}

	public String getFailedCause() {
		return failedCause;
	}

	public void setFailedCause(String failedCause) {
		this.failedCause = failedCause;
	}


    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取调拨出库单号
     *
     * @return allocate_in_order_code - 调拨出库单号
     */
    public String getAllocateInOrderCode() {
        return allocateInOrderCode;
    }

    /**
     * 设置调拨出库单号
     *
     * @param allocateInOrderCode 调拨出库单号
     */
    public void setAllocateInOrderCode(String allocateInOrderCode) {
        this.allocateInOrderCode = allocateInOrderCode;
    }

    /**
     * 获取调拨单编号
     *
     * @return allocate_order_code - 调拨单编号
     */
    public String getAllocateOrderCode() {
        return allocateOrderCode;
    }

    /**
     * 设置调拨单编号
     *
     * @param allocateOrderCode 调拨单编号
     */
    public void setAllocateOrderCode(String allocateOrderCode) {
        this.allocateOrderCode = allocateOrderCode;
    }

    /**
     * 获取供应商名称
     *
     * @return supplier_name - 供应商名称
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 设置供应商名称
     *
     * @param supplierName 供应商名称
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * 获取供应商编码
     *
     * @return supplier_code - 供应商编码
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * 设置供应商编码
     *
     * @param supplierCode 供应商编码
     */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * 获取0-待完成出库,1-出库完成,2-出库异常,3-入库仓接收成功,4-入库仓接收失败,5-入库完成,6-入库异常,7-已取消
     *
     * @return status - 0-待完成出库,1-出库完成,2-出库异常,3-入库仓接收成功,4-入库仓接收失败,5-入库完成,6-入库异常,7-已取消
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置0-待完成出库,1-出库完成,2-出库异常,3-入库仓接收成功,4-入库仓接收失败,5-入库完成,6-入库异常,7-已取消
     *
     * @param status 0-待完成出库,1-出库完成,2-出库异常,3-入库仓接收成功,4-入库仓接收失败,5-入库完成,6-入库异常,7-已取消
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取预约到货日期,格式yyyy-mm-dd hh:mi:ss
     *
     * @return pre_recive_date - 预约到货日期,格式yyyy-mm-dd hh:mi:ss
     */
    public Date getPreReciveDate() {
        return preReciveDate;
    }

    /**
     * 设置预约到货日期,格式yyyy-mm-dd hh:mi:ss
     *
     * @param preReciveDate 预约到货日期,格式yyyy-mm-dd hh:mi:ss
     */
    public void setPreReciveDate(Date preReciveDate) {
        this.preReciveDate = preReciveDate;
    }

    /**
     * 获取收货人
     *
     * @return receiver - 收货人
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * 设置收货人
     *
     * @param receiver 收货人
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取收货人手机
     *
     * @return receiver_phone - 收货人手机
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * 设置收货人手机
     *
     * @param receiverPhone 收货人手机
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	/**
     * 获取收货人手机
     *
     * @return receiver_mobile - 收货人手机
     */
    public String getReceiverMobile() {
        return receiverMobile;
    }

    /**
     * 设置收货人手机
     *
     * @param receiverMobile 收货人手机
     */
    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    /**
     * 获取发件人
     *
     * @return sender - 发件人
     */
    public String getSender() {
        return sender;
    }

    /**
     * 设置发件人
     *
     * @param sender 发件人
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * 获取发件人手机
     *
     * @return sender_phone - 发件人手机
     */
    public String getSenderPhone() {
        return senderPhone;
    }

    /**
     * 设置发件人手机
     *
     * @param senderPhone 发件人手机
     */
    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    /**
     * 获取发件人手机
     *
     * @return sender_mobile - 发件人手机
     */
    public String getSenderMobile() {
        return senderMobile;
    }

    /**
     * 设置发件人手机
     *
     * @param senderMobile 发件人手机
     */
    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    /**
     * 获取发件方详细地址
     *
     * @return sender_address - 发件方详细地址
     */
    public String getSenderAddress() {
        return senderAddress;
    }

    /**
     * 设置发件方详细地址
     *
     * @param senderAddress 发件方详细地址
     */
    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    /**
     * 获取发货单号
     *
     * @return delivery_number - 发货单号
     */
    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    /**
     * 设置发货单号
     *
     * @param deliveryNumber 发货单号
     */
    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    /**
     * 获取入库备注
     *
     * @return in_memo - 入库备注
     */
    public String getInMemo() {
        return inMemo;
    }

    /**
     * 设置入库备注
     *
     * @param inMemo 入库备注
     */
    public void setInMemo(String inMemo) {
        this.inMemo = inMemo;
    }

    /**
     * 获取备注
     *
     * @return memo - 备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置备注
     *
     * @param memo 备注
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取是否有效:0-无效,1-有效
     *
     * @return is_valid - 是否有效:0-无效,1-有效
     */
    public String getIsValid() {
        return isValid;
    }

    /**
     * 设置是否有效:0-无效,1-有效
     *
     * @param isValid 是否有效:0-无效,1-有效
     */
    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public List<AllocateSkuDetail> getSkuDetailList() {
        return skuDetailList;
    }

    public void setSkuDetailList(List<AllocateSkuDetail> skuDetailList) {
        this.skuDetailList = skuDetailList;
    }
}