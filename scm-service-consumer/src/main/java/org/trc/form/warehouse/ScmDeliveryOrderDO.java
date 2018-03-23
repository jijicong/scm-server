package org.trc.form.warehouse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发货单信息
 */
public class ScmDeliveryOrderDO {

    /**
     * 出库单号
     */
    private String deliveryOrderCode;

    /**
     * 出库单类型
     */
    private String orderType;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopNick;


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
     * 是否需要发票：0-否,1-是
     */
    private String invoiceFlag;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 发票标识
     */
    private String invoiceState;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 购方税号(税务识别号) 
     */
    private String invoiceTax;

    /**
     * 买家留言
     */
    private String buyerMessage;

    /**
     * 卖家留言
     */
    private String sellerMessage;

    /**
     * 发货单创建时间
     */
    private Date createTime;

    /**
     * 前台订单/店铺订单的创建时间/下单时间
     */
    private Date placeOrderTime;

    /**
     * 操作(审核)时间
     */
    private Date operateTime;

    /**
     * 物流公司编码
     */
    private String logisticsCode;

    /**
     * 订单标记位，首位为1代表货到付款(京东)
     */
    private String orderMark;

    /**
     * ISV来源编号(京东)
     */
    private String isvSource;

    /**
     * 销售平台来源(京东)
     */
    private String salePlatformSource;

    /**
     * 承运商编号(京东)
     */
    private String shipperNo;

    /**
     * 安维标识，0 不需要安装 1 京东安装 2 厂家安装(京东)
     */
    private String installVenderId;

    /**
     * 订单列表
     */
    private List<ScmDeliveryOrderItem> scmDeleveryOrderItemList;

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getShopNick() {
        return shopNick;
    }

    public void setShopNick(String shopNick) {
        this.shopNick = shopNick;
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

    public String getInvoiceFlag() {
        return invoiceFlag;
    }

    public void setInvoiceFlag(String invoiceFlag) {
        this.invoiceFlag = invoiceFlag;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceState() {
        return invoiceState;
    }

    public void setInvoiceState(String invoiceState) {
        this.invoiceState = invoiceState;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getInvoiceTax() {
        return invoiceTax;
    }

    public void setInvoiceTax(String invoiceTax) {
        this.invoiceTax = invoiceTax;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getSellerMessage() {
        return sellerMessage;
    }

    public void setSellerMessage(String sellerMessage) {
        this.sellerMessage = sellerMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPlaceOrderTime() {
        return placeOrderTime;
    }

    public void setPlaceOrderTime(Date placeOrderTime) {
        this.placeOrderTime = placeOrderTime;
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

    public String getOrderMark() {
        return orderMark;
    }

    public void setOrderMark(String orderMark) {
        this.orderMark = orderMark;
    }

    public String getIsvSource() {
        return isvSource;
    }

    public void setIsvSource(String isvSource) {
        this.isvSource = isvSource;
    }

    public String getSalePlatformSource() {
        return salePlatformSource;
    }

    public void setSalePlatformSource(String salePlatformSource) {
        this.salePlatformSource = salePlatformSource;
    }

    public List<ScmDeliveryOrderItem> getScmDeleveryOrderItemList() {
        return scmDeleveryOrderItemList;
    }

    public void setScmDeleveryOrderItemList(List<ScmDeliveryOrderItem> scmDeleveryOrderItemList) {
        this.scmDeleveryOrderItemList = scmDeleveryOrderItemList;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getShipperNo() {
        return shipperNo;
    }

    public void setShipperNo(String shipperNo) {
        this.shipperNo = shipperNo;
    }

    public String getInstallVenderId() {
        return installVenderId;
    }

    public void setInstallVenderId(String installVenderId) {
        this.installVenderId = installVenderId;
    }
}
