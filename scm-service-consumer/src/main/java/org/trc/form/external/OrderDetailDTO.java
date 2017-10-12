package org.trc.form.external;

import java.math.BigDecimal;

/**
 * Created by hzwyz on 2017/9/21 0021.
 */
public class OrderDetailDTO {
    private Long id;
    //操作
    private String operate;
    //异常说明
    private String errMsg;
    //渠道订单提交时间
    private String channelOrderSubmitTime;
    //京东订单生成时间
    private String jingdongOrderCreateTime;
    //渠道平台订单号
    private String channelPlatformOrder;
    //京东父订单号
    private String parentOrderCode;
    //京东子订单编号
    private String orderCode;
    //京东商品编号
    private String itemSkuCode;
    //京东商品名称
    private String itemSkuName;
    //京东一级分类
    private String  firstClassify;
    //京东二级分类
    private String  secondClassify;
    //京东三级分类
    private String  thirdClassify;
    //渠道商品数量
    private Integer channelItemsNum;
    //京东商品数量
    private Integer jdItemsNum;
    //京东商品单价
    private BigDecimal price;
    //京东商品总计金额
    private BigDecimal totalPrice;
    //买家实付商品金额
    private BigDecimal pay;
    //运费
    private BigDecimal freight;
    //京东子订单总计金额
    private BigDecimal subTotalPrice;
    //京东父订单总计金额
    private BigDecimal parentTotalPrice;
    //账户实际支付金额
    private BigDecimal actualPay;
    //账户实际退款金额
    private BigDecimal refund;
    //余额变动时间
    private String balanceCreateTime;
    //订单状态
    private String state;
    //备注
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getChannelOrderSubmitTime() {
        return channelOrderSubmitTime;
    }

    public void setChannelOrderSubmitTime(String channelOrderSubmitTime) {
        this.channelOrderSubmitTime = channelOrderSubmitTime;
    }

    public String getJingdongOrderCreateTime() {
        return jingdongOrderCreateTime;
    }

    public void setJingdongOrderCreateTime(String jingdongOrderCreateTime) {
        this.jingdongOrderCreateTime = jingdongOrderCreateTime;
    }

    public String getChannelPlatformOrder() {
        return channelPlatformOrder;
    }

    public void setChannelPlatformOrder(String channelPlatformOrder) {
        this.channelPlatformOrder = channelPlatformOrder;
    }

    public String getParentOrderCode() {
        return parentOrderCode;
    }

    public void setParentOrderCode(String parentOrderCode) {
        this.parentOrderCode = parentOrderCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getItemSkuCode() {
        return itemSkuCode;
    }

    public void setItemSkuCode(String itemSkuCode) {
        this.itemSkuCode = itemSkuCode;
    }

    public String getItemSkuName() {
        return itemSkuName;
    }

    public void setItemSkuName(String itemSkuName) {
        this.itemSkuName = itemSkuName;
    }

    public String getFirstClassify() {
        return firstClassify;
    }

    public void setFirstClassify(String firstClassify) {
        this.firstClassify = firstClassify;
    }

    public String getSecondClassify() {
        return secondClassify;
    }

    public void setSecondClassify(String secondClassify) {
        this.secondClassify = secondClassify;
    }

    public String getThirdClassify() {
        return thirdClassify;
    }

    public void setThirdClassify(String thirdClassify) {
        this.thirdClassify = thirdClassify;
    }

    public Integer getChannelItemsNum() {
        return channelItemsNum;
    }

    public void setChannelItemsNum(Integer channelItemsNum) {
        this.channelItemsNum = channelItemsNum;
    }

    public Integer getJdItemsNum() {
        return jdItemsNum;
    }

    public void setJdItemsNum(Integer jdItemsNum) {
        this.jdItemsNum = jdItemsNum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getPay() {
        return pay;
    }

    public void setPay(BigDecimal pay) {
        this.pay = pay;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getSubTotalPrice() {
        return subTotalPrice;
    }

    public void setSubTotalPrice(BigDecimal subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    public BigDecimal getParentTotalPrice() {
        return parentTotalPrice;
    }

    public void setParentTotalPrice(BigDecimal parentTotalPrice) {
        this.parentTotalPrice = parentTotalPrice;
    }

    public BigDecimal getRefund() {
        return refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public String getBalanceCreateTime() {
        return balanceCreateTime;
    }

    public void setBalanceCreateTime(String balanceCreateTime) {
        this.balanceCreateTime = balanceCreateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getActualPay() {
        return actualPay;
    }

    public void setActualPay(BigDecimal actualPay) {
        this.actualPay = actualPay;
    }
}
