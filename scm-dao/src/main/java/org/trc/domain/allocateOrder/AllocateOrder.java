package org.trc.domain.allocateOrder;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.ws.rs.FormParam;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

@Table(name = "allocate_order")
public class AllocateOrder extends AllocateOrderBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 741617431263633041L;

	/**
     * 调拨单编号
     */
    @Id
    @Column(name = "allocate_order_code")
    @FormParam("allocateOrderCode")
    private String allocateOrderCode;

    /**
     * 调拨入库单编号
     */
    @Column(name = "allocate_in_order_code")
    @FormParam("allocateInOrderCode")
    private String allocateInOrderCode;

    /**
     * 调拨出库单编号
     */
    @Column(name = "allocate_out_order_code")
    @FormParam("allocateOutOrderCode")
    private String allocateOutOrderCode;

    /**
     * 0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-通知仓库,5-作废
     */
    @Column(name = "order_status")
    @FormParam("orderStatus")
    private String orderStatus;

    /**
     * 出入库状态
            0-初始,1-等待出入库,2-出库完成,3-出库异常,4-入库完成,5-入库异常
     */
    @Column(name = "in_out_status")
    private String inOutStatus;

    /**
     * 收货人
     */
    @NotBlank(message = "收货人不能为空")
    @FormParam("receiver")
    private String receiver;

    /**
     * 收货人所在省
     */
    @Column(name = "reciver_province")
    @NotBlank(message = "收货人所在省不能为空")
    @FormParam("reciverProvince")
    private String reciverProvince;

    /**
     * 收货人所在城市
     */
    @Column(name = "reciver_city")
    @NotBlank(message = "收货人所在城市不能为空")
    @FormParam("reciverCity")
    private String reciverCity;

    /**
     * 收货详细地址
     */
    @Column(name = "receive_address")
    @FormParam("receiveAddress")
    private String receiveAddress;

    /**
     * 收货人手机
     */
    @Column(name = "receiver_mobile")
    @NotBlank(message = "收货人手机不能为空")
    @FormParam("receiverMobile")
    private String receiverMobile;

    /**
     * 发件人
     */
    @NotBlank(message = "发件人不能为空")
    @FormParam("sender")
    private String sender;

    /**
     * 发件人所在省
     */
    @Column(name = "sender_province")
    @NotBlank(message = "发件人所在省不能为空")
    @FormParam("senderProvince")
    private String senderProvince;

    /**
     * 发件人所在城市
     */
    @Column(name = "sender_city")
    @NotBlank(message = "发件人所在城市不能为空")
    @FormParam("senderCity")
    private String senderCity;

    /**
     * 发件人手机
     */
    @Column(name = "sender_mobile")
    @NotBlank(message = "发件人手机不能为空")
    @FormParam("senderMobile")
    private String senderMobile;

    /**
     * 发件方详细地址
     */
    @Column(name = "sender_address")
    @FormParam("senderAddress")
    private String senderAddress;

    @FormParam("memo")
    private String memo;

    /**
     * 审核意见
     */
    @FormParam("auditOpinion")
    @Column(name = "audit_opinion")
    private String auditOpinion;
    
    /**
     * 提交审核人
     */
    @Column(name = "submit_operator")
    private String submitOperator;
    
    /**
     * 提交审核时间
     */
    @Column(name = "submit_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date submitTime;
    
    /**
     * 是否删除:0-否,1-是
     */
    @Column(name = "is_deleted")
    private String isDeleted;

    /**
     * 创建人
     */
    @Column(name = "create_operator")
    private String createOperator;

    /**
     * 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "create_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    /**
     * 最后更新时间,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "update_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
    
    /**
     * 出库通知单的状态
     */
    @Transient
    private String outOrderStatus;
    
    /**
     * 创建人姓名
     */
    @Transient
    private String createOperatorName;
    
    /**
     * 调拨单商品明细列表
     */
    @Transient
    private List<AllocateSkuDetail> skuDetailList;

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
     * 获取调拨入库单编号
     *
     * @return allocate_in_order_code - 调拨入库单编号
     */
    public String getAllocateInOrderCode() {
        return allocateInOrderCode;
    }

    /**
     * 设置调拨入库单编号
     *
     * @param allocateInOrderCode 调拨入库单编号
     */
    public void setAllocateInOrderCode(String allocateInOrderCode) {
        this.allocateInOrderCode = allocateInOrderCode;
    }

    /**
     * 获取调拨出库单编号
     *
     * @return allocate_out_order_code - 调拨出库单编号
     */
    public String getAllocateOutOrderCode() {
        return allocateOutOrderCode;
    }

    /**
     * 设置调拨出库单编号
     *
     * @param allocateOutOrderCode 调拨出库单编号
     */
    public void setAllocateOutOrderCode(String allocateOutOrderCode) {
        this.allocateOutOrderCode = allocateOutOrderCode;
    }

    /**
     * 获取0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-通知仓库,5-作废
     *
     * @return order_status - 0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-通知仓库,5-作废
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-通知仓库,5-作废
     *
     * @param orderStatus 0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-通知仓库,5-作废
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取出入库状态
            0-初始,1-等待出入库,2-出库完成,3-出库异常,4-入库完成,5-入库异常
     *
     * @return in_out_status - 出入库状态
            0-初始,1-等待出入库,2-出库完成,3-出库异常,4-入库完成,5-入库异常
     */
    public String getInOutStatus() {
        return inOutStatus;
    }

    /**
     * 设置出入库状态
            0-初始,1-等待出入库,2-出库完成,3-出库异常,4-入库完成,5-入库异常
     *
     * @param inOutStatus 出入库状态
            0-初始,1-等待出入库,2-出库完成,3-出库异常,4-入库完成,5-入库异常
     */
    public void setInOutStatus(String inOutStatus) {
        this.inOutStatus = inOutStatus;
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
     * 获取收货人所在省
     *
     * @return reciver_province - 收货人所在省
     */
    public String getReciverProvince() {
        return reciverProvince;
    }

    /**
     * 设置收货人所在省
     *
     * @param reciverProvince 收货人所在省
     */
    public void setReciverProvince(String reciverProvince) {
        this.reciverProvince = reciverProvince;
    }

    /**
     * 获取收货人所在城市
     *
     * @return reciver_city - 收货人所在城市
     */
    public String getReciverCity() {
        return reciverCity;
    }

    /**
     * 设置收货人所在城市
     *
     * @param reciverCity 收货人所在城市
     */
    public void setReciverCity(String reciverCity) {
        this.reciverCity = reciverCity;
    }

    /**
     * 获取收货详细地址
     *
     * @return receive_address - 收货详细地址
     */
    public String getReceiveAddress() {
        return receiveAddress;
    }

    /**
     * 设置收货详细地址
     *
     * @param receiveAddress 收货详细地址
     */
    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
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
     * 获取发件人所在省
     *
     * @return sender_province - 发件人所在省
     */
    public String getSenderProvince() {
        return senderProvince;
    }

    /**
     * 设置发件人所在省
     *
     * @param senderProvince 发件人所在省
     */
    public void setSenderProvince(String senderProvince) {
        this.senderProvince = senderProvince;
    }

    /**
     * 获取发件人所在城市
     *
     * @return sender_city - 发件人所在城市
     */
    public String getSenderCity() {
        return senderCity;
    }

    /**
     * 设置发件人所在城市
     *
     * @param senderCity 发件人所在城市
     */
    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
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
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }




	public String getOutOrderStatus() {
		return outOrderStatus;
	}

	public void setOutOrderStatus(String outOrderStatus) {
		this.outOrderStatus = outOrderStatus;
	}

	public String getCreateOperatorName() {
		return createOperatorName;
	}

	public void setCreateOperatorName(String createOperatorName) {
		this.createOperatorName = createOperatorName;
	}

	public List<AllocateSkuDetail> getSkuDetailList() {
		return skuDetailList;
	}

	public void setSkuDetailList(List<AllocateSkuDetail> skuDetailList) {
		this.skuDetailList = skuDetailList;
	}

	public String getAuditOpinion() {
		return auditOpinion;
	}

	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
	}

	public String getSubmitOperator() {
		return submitOperator;
	}

	public void setSubmitOperator(String submitOperator) {
		this.submitOperator = submitOperator;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
    
}