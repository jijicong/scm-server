package org.trc.form.warehouse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ScmDeliveryOrderDetailResponse {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 出库单号
     */
    private String deliveryOrderCode;

    /**
     * 仓储系统出库单号
     */
    private String deliveryOrderId;

    /**
     * 出库单类型
     */
    private String orderType;

    /**
     * 出库单状态
     */
    private String status;

    /**
     * 操作(审核)时间
     */
    private Date operateTime;

    /**
     * 物流公司编码
     */
    private String logisticsCode;

    /**
     * 物流公司名称
     */
    private String logisticsName;

    /**
     * 运单号
     */
    private String expressCode;

    /**
     * 客户姓名
     */
    private String consigneeName;

    /**
     * 客户手机
     */
    private String consigneeMobile;

    /**
     * 客户联系电话
     */
    private String consigneePhone;

    /**
     * 客户邮箱
     */
    private String consigneeEmail;

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
     * 收件人县
     */
    private String reciverCountry;

    /**
     * 收件人镇
     */
    private String reciverTown;

    /**
     * 收件人详细地址
     */
    private String reciverDetailAddress;

    /**
     * 退货人姓名
     */
    private String afterSalesName;

    /**
     * 退货人联系电话
     */
    private String afterSalesMobile;

    /**
     * 退货地址
     */
    private String afterSalesAddress;

    /**
     * 要求送货日期
     */
    private String expectDate;

    /**
     * 预计送达时间
     */
    private String expectDeliveryDate;

    /**
     * 应收金额
     */
    private BigDecimal receivable;

    /**
     * 买家留言
     */
    private String consigneeRemark;

    /**
     * 订单标记位
     */
    private String orderMark;

    /**
     * 下单人（操作人）
     */
    private String pinAccount;

    /**
     * 出库单当前状态
     */
    private String currentStatus;

    /**
     * 是否拆单
     */
    private String splitFlag;

    /**
     * 子单订单号集合,多个以逗号隔开
     */
    private String splitEclpSoNos;

    /**
     * 配送方式
     */
    private String transType;

    /**
     * 发货单商品明细
     */
    private List<ScmDeliveryOrderDetailResponseItem> scmDeliveryOrderDetailResponseItemList;

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

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeEmail() {
        return consigneeEmail;
    }

    public void setConsigneeEmail(String consigneeEmail) {
        this.consigneeEmail = consigneeEmail;
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

    public String getReciverCountry() {
        return reciverCountry;
    }

    public void setReciverCountry(String reciverCountry) {
        this.reciverCountry = reciverCountry;
    }

    public String getReciverTown() {
        return reciverTown;
    }

    public void setReciverTown(String reciverTown) {
        this.reciverTown = reciverTown;
    }

    public String getReciverDetailAddress() {
        return reciverDetailAddress;
    }

    public void setReciverDetailAddress(String reciverDetailAddress) {
        this.reciverDetailAddress = reciverDetailAddress;
    }

    public String getAfterSalesName() {
        return afterSalesName;
    }

    public void setAfterSalesName(String afterSalesName) {
        this.afterSalesName = afterSalesName;
    }

    public String getAfterSalesMobile() {
        return afterSalesMobile;
    }

    public void setAfterSalesMobile(String afterSalesMobile) {
        this.afterSalesMobile = afterSalesMobile;
    }

    public String getAfterSalesAddress() {
        return afterSalesAddress;
    }

    public void setAfterSalesAddress(String afterSalesAddress) {
        this.afterSalesAddress = afterSalesAddress;
    }

    public String getExpectDate() {
        return expectDate;
    }

    public void setExpectDate(String expectDate) {
        this.expectDate = expectDate;
    }

    public String getExpectDeliveryDate() {
        return expectDeliveryDate;
    }

    public void setExpectDeliveryDate(String expectDeliveryDate) {
        this.expectDeliveryDate = expectDeliveryDate;
    }

    public BigDecimal getReceivable() {
        return receivable;
    }

    public void setReceivable(BigDecimal receivable) {
        this.receivable = receivable;
    }

    public String getConsigneeRemark() {
        return consigneeRemark;
    }

    public void setConsigneeRemark(String consigneeRemark) {
        this.consigneeRemark = consigneeRemark;
    }

    public String getOrderMark() {
        return orderMark;
    }

    public void setOrderMark(String orderMark) {
        this.orderMark = orderMark;
    }

    public String getPinAccount() {
        return pinAccount;
    }

    public void setPinAccount(String pinAccount) {
        this.pinAccount = pinAccount;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getSplitFlag() {
        return splitFlag;
    }

    public void setSplitFlag(String splitFlag) {
        this.splitFlag = splitFlag;
    }

    public String getSplitEclpSoNos() {
        return splitEclpSoNos;
    }

    public void setSplitEclpSoNos(String splitEclpSoNos) {
        this.splitEclpSoNos = splitEclpSoNos;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public List<ScmDeliveryOrderDetailResponseItem> getScmDeliveryOrderDetailResponseItemList() {
        return scmDeliveryOrderDetailResponseItemList;
    }

    public void setScmDeliveryOrderDetailResponseItemList(List<ScmDeliveryOrderDetailResponseItem> scmDeliveryOrderDetailResponseItemList) {
        this.scmDeliveryOrderDetailResponseItemList = scmDeliveryOrderDetailResponseItemList;
    }
}
