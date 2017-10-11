package org.trc.form.external;

import java.util.Date;

/**
 * Created by hzwyz on 2017/7/5 0005.
 */
public class OrderDetail {

    //主键
    private Long id;
    //仓库级订单
    private String warehouseOrderCode;
    //子订单号
    private String spitOrderCode;
    //商品sku
    private String skuId;
    //京东下单商品数量
    private Integer num;
    //商品名称
    private String name;
    //订单类型
    private String type;
    //创建时间
    private Date createTime;
    //渠道订单提交时间
    private Date channelOrderSubmitTime;
    //京东订单生成时间
    private Date orderCreateTime;
    //渠道平台订单号
    private String channelPlatformOrderCode;
    //父订单号
    private String orderId;
    //京东一级分类
    private String firstClassify;
    //京东二级分类
    private String secondClassify;
    //京东三级分类
    private String thirdClassify;
    //渠道商品数量
    private Integer channelItemsNum;
    //商品价格
    private Long price;
    //京东商品总计金额
    private Long jdItemsTotalPrice;
    //买家实付商品金额
    private Long purchaserPay;
    //运费
    private Long freight;
    //京东子订单总计金额
    private Long jdSubOrderTotalPay;
    //京东父订单总计金额
    private Long jdParentOrderTotalPay;
    //订单状态
    private Integer orderState;
    //检查状态
    private Integer checkState;

    //账户实际支付金额
    private Long totalPrice;
    //账户实际退款金额
    private Long refund;
    //余额变动时间
    private Date balanceCreateTime;

    private String balanceDetailCode;
    //操作
    private Integer operate;
    //异常说明
    private String errMsg;
    //备注
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getSpitOrderCode() {
        return spitOrderCode;
    }

    public void setSpitOrderCode(String spitOrderCode) {
        this.spitOrderCode = spitOrderCode;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getChannelOrderSubmitTime() {
        return channelOrderSubmitTime;
    }

    public void setChannelOrderSubmitTime(Date channelOrderSubmitTime) {
        this.channelOrderSubmitTime = channelOrderSubmitTime;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getChannelPlatformOrderCode() {
        return channelPlatformOrderCode;
    }

    public void setChannelPlatformOrderCode(String channelPlatformOrderCode) {
        this.channelPlatformOrderCode = channelPlatformOrderCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getJdItemsTotalPrice() {
        return jdItemsTotalPrice;
    }

    public void setJdItemsTotalPrice(Long jdItemsTotalPrice) {
        this.jdItemsTotalPrice = jdItemsTotalPrice;
    }

    public Long getPurchaserPay() {
        return purchaserPay;
    }

    public void setPurchaserPay(Long purchaserPay) {
        this.purchaserPay = purchaserPay;
    }

    public Long getFreight() {
        return freight;
    }

    public void setFreight(Long freight) {
        this.freight = freight;
    }

    public Long getJdSubOrderTotalPay() {
        return jdSubOrderTotalPay;
    }

    public void setJdSubOrderTotalPay(Long jdSubOrderTotalPay) {
        this.jdSubOrderTotalPay = jdSubOrderTotalPay;
    }

    public Long getJdParentOrderTotalPay() {
        return jdParentOrderTotalPay;
    }

    public void setJdParentOrderTotalPay(Long jdParentOrderTotalPay) {
        this.jdParentOrderTotalPay = jdParentOrderTotalPay;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public Integer getCheckState() {
        return checkState;
    }

    public void setCheckState(Integer checkState) {
        this.checkState = checkState;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getRefund() {
        return refund;
    }

    public void setRefund(Long refund) {
        this.refund = refund;
    }

    public Date getBalanceCreateTime() {
        return balanceCreateTime;
    }

    public void setBalanceCreateTime(Date balanceCreateTime) {
        this.balanceCreateTime = balanceCreateTime;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBalanceDetailCode() {
        return balanceDetailCode;
    }

    public void setBalanceDetailCode(String balanceDetailCode) {
        this.balanceDetailCode = balanceDetailCode;
    }
}
