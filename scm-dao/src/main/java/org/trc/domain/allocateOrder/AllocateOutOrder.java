package org.trc.domain.allocateOrder;

import java.util.Date;
import javax.persistence.*;

@Table(name = "allocate_out_order")
public class AllocateOutOrder {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 调拨出库单编号
     */
    @Column(name = "allocate_out_order_code")
    private String allocateOutOrderCode;

    /**
     * 调拨单编号
     */
    @Column(name = "allocate_order_code")
    private String allocateOrderCode;

    /**
     * 调入仓库编码
     */
    @Column(name = "in_warehouse_code")
    private String inWarehouseCode;

    /**
     * 调入仓库名称
     */
    @Column(name = "in_warehouse_name")
    private String inWarehouseName;

    /**
     * 调出仓库编码
     */
    @Column(name = "out_warehouse_code")
    private String outWarehouseCode;

    /**
     * 调出仓库名称
     */
    @Column(name = "out_warehouse_name")
    private String outWarehouseName;

    /**
     * 出库状态0-待通知出库,1-出库仓库接收成功,2-出库仓库接收失败,3-出库完成,4-出库异常,5-已取消
     */
    private String status;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人手机
     */
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 发件人所在省
     */
    @Column(name = "reciver_province")
    private String reciverProvince;

    /**
     * 发件人所在城市
     */
    @Column(name = "reciver_city")
    private String reciverCity;

    /**
     * 收货地址
     */
    @Column(name = "receive_address")
    private String receiveAddress;

    /**
     * 发件人
     */
    private String sender;

    /**
     * 发件人所在省
     */
    @Column(name = "sender_province")
    private String senderProvince;

    /**
     * 发件人所在城市
     */
    @Column(name = "sender_city")
    private String senderCity;

    /**
     * 发件人手机
     */
    @Column(name = "sender_phone")
    private String senderPhone;

    /**
     * 发件方详细地址
     */
    @Column(name = "sender_address")
    private String senderAddress;

    /**
     * 备注
     */
    private String memo;

    /**
     * 是否删除:0-否,1-是
     */
    @Column(name = "is_deleted")
    private String isDeleted;

    /**
     * 出库单创建人
     */
    @Column(name = "create_operator_name")
    private String createOperatorName;

    /**
     * 创建人
     */
    @Column(name = "create_operator")
    private String createOperator;

    /**
     * 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 最后更新时间,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 是否有效:0-无效,1-有效
     */
    @Column(name = "is_valid")
    private String isValid;

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
     * 获取调入仓库编码
     *
     * @return in_warehouse_code - 调入仓库编码
     */
    public String getInWarehouseCode() {
        return inWarehouseCode;
    }

    /**
     * 设置调入仓库编码
     *
     * @param inWarehouseCode 调入仓库编码
     */
    public void setInWarehouseCode(String inWarehouseCode) {
        this.inWarehouseCode = inWarehouseCode;
    }

    /**
     * 获取调入仓库名称
     *
     * @return in_warehouse_name - 调入仓库名称
     */
    public String getInWarehouseName() {
        return inWarehouseName;
    }

    /**
     * 设置调入仓库名称
     *
     * @param inWarehouseName 调入仓库名称
     */
    public void setInWarehouseName(String inWarehouseName) {
        this.inWarehouseName = inWarehouseName;
    }

    /**
     * 获取调出仓库编码
     *
     * @return out_warehouse_code - 调出仓库编码
     */
    public String getOutWarehouseCode() {
        return outWarehouseCode;
    }

    /**
     * 设置调出仓库编码
     *
     * @param outWarehouseCode 调出仓库编码
     */
    public void setOutWarehouseCode(String outWarehouseCode) {
        this.outWarehouseCode = outWarehouseCode;
    }

    /**
     * 获取调出仓库名称
     *
     * @return out_warehouse_name - 调出仓库名称
     */
    public String getOutWarehouseName() {
        return outWarehouseName;
    }

    /**
     * 设置调出仓库名称
     *
     * @param outWarehouseName 调出仓库名称
     */
    public void setOutWarehouseName(String outWarehouseName) {
        this.outWarehouseName = outWarehouseName;
    }

    /**
     * 获取出库状态0-待通知出库,1-出库仓库接收成功,2-出库仓库接收失败,3-出库完成,4-出库异常,5-已取消
     *
     * @return status - 出库状态0-待通知出库,1-出库仓库接收成功,2-出库仓库接收失败,3-出库完成,4-出库异常,5-已取消
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置出库状态0-待通知出库,1-出库仓库接收成功,2-出库仓库接收失败,3-出库完成,4-出库异常,5-已取消
     *
     * @param status 出库状态0-待通知出库,1-出库仓库接收成功,2-出库仓库接收失败,3-出库完成,4-出库异常,5-已取消
     */
    public void setStatus(String status) {
        this.status = status;
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

    /**
     * 获取发件人所在省
     *
     * @return reciver_province - 发件人所在省
     */
    public String getReciverProvince() {
        return reciverProvince;
    }

    /**
     * 设置发件人所在省
     *
     * @param reciverProvince 发件人所在省
     */
    public void setReciverProvince(String reciverProvince) {
        this.reciverProvince = reciverProvince;
    }

    /**
     * 获取发件人所在城市
     *
     * @return reciver_city - 发件人所在城市
     */
    public String getReciverCity() {
        return reciverCity;
    }

    /**
     * 设置发件人所在城市
     *
     * @param reciverCity 发件人所在城市
     */
    public void setReciverCity(String reciverCity) {
        this.reciverCity = reciverCity;
    }

    /**
     * 获取收货地址
     *
     * @return receive_address - 收货地址
     */
    public String getReceiveAddress() {
        return receiveAddress;
    }

    /**
     * 设置收货地址
     *
     * @param receiveAddress 收货地址
     */
    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
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
     * 获取是否删除:0-否,1-是
     *
     * @return is_deleted - 是否删除:0-否,1-是
     */
    public String getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除:0-否,1-是
     *
     * @param isDeleted 是否删除:0-否,1-是
     */
    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 获取出库单创建人
     *
     * @return create_operator_name - 出库单创建人
     */
    public String getCreateOperatorName() {
        return createOperatorName;
    }

    /**
     * 设置出库单创建人
     *
     * @param createOperatorName 出库单创建人
     */
    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }

    /**
     * 获取创建人
     *
     * @return create_operator - 创建人
     */
    public String getCreateOperator() {
        return createOperator;
    }

    /**
     * 设置创建人
     *
     * @param createOperator 创建人
     */
    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    /**
     * 获取创建时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @return create_time - 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @param createTime 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取最后更新时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @return update_time - 最后更新时间,格式yyyy-mm-dd hh:mi:ss
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置最后更新时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @param updateTime 最后更新时间,格式yyyy-mm-dd hh:mi:ss
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
}