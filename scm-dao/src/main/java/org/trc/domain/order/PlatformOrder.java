package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;
import org.trc.custom.MoneySerializer;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ding on 2017/6/21.
 */
@Table(name = "platform_order")
public class PlatformOrder {

    public PlatformOrder(){

    }

    public PlatformOrder(String platformOrderCode, String channelCode, String platformCode, String userId, String userName, Integer itemNum, String payType, Long payment, Long pointsFee, Long totalFee, Long adjustFee, Long postageFee, Long totalTax, Byte needInvoice, String invoiceName, String invoiceType, String invoiceMain, String receiverProvince, String receiverCity, String receiverDistrict, String receiverAddress, String receiverZip, String receiverName, String receiverIdCard, String receiverIdCardFront, String receiverIdCardBack, String receiverPhone, String receiverMobile, String buyerArea, String zitiMemo, String zitiAddr, Byte anony, Integer obtainPointFee, Integer realPointFee, String stepTradeStatus, Long stepPaidFee, Byte isClearing, String cancelReason, String cancelStatus, String status, Byte isVirtual, String ip, Byte type, Long discountPromotion, Long discountCouponShop, Long discountCouponPlatform, Long discountFee, String shippingType, String platformType, Byte rateStatus, String couponCode, String groupBuyStatus, String isDeleted, Date createTime, Date payTime, Date consignTime, Date receiveTime, Date updateTime, Date timeoutActionTime, Date endTime, String payBillId) {
        this.platformOrderCode = platformOrderCode;
        this.channelCode = channelCode;
        this.platformCode = platformCode;
        this.userId = userId;
        this.userName = userName;
        this.itemNum = itemNum;
        this.payType = payType;
        this.payment = payment;
        this.pointsFee = pointsFee;
        this.totalFee = totalFee;
        this.adjustFee = adjustFee;
        this.postageFee = postageFee;
        this.totalTax = totalTax;
        this.needInvoice = needInvoice;
        this.invoiceName = invoiceName;
        this.invoiceType = invoiceType;
        this.invoiceMain = invoiceMain;
        this.receiverProvince = receiverProvince;
        this.receiverCity = receiverCity;
        this.receiverDistrict = receiverDistrict;
        this.receiverAddress = receiverAddress;
        this.receiverZip = receiverZip;
        this.receiverName = receiverName;
        this.receiverIdCard = receiverIdCard;
        this.receiverIdCardFront = receiverIdCardFront;
        this.receiverIdCardBack = receiverIdCardBack;
        this.receiverPhone = receiverPhone;
        this.receiverMobile = receiverMobile;
        this.buyerArea = buyerArea;
        this.zitiMemo = zitiMemo;
        this.zitiAddr = zitiAddr;
        this.anony = anony;
        this.obtainPointFee = obtainPointFee;
        this.realPointFee = realPointFee;
        this.stepTradeStatus = stepTradeStatus;
        this.stepPaidFee = stepPaidFee;
        this.isClearing = isClearing;
        this.cancelReason = cancelReason;
        this.cancelStatus = cancelStatus;
        this.status = status;
        this.isVirtual = isVirtual;
        this.ip = ip;
        this.type = type;
        this.discountPromotion = discountPromotion;
        this.discountCouponShop = discountCouponShop;
        this.discountCouponPlatform = discountCouponPlatform;
        this.discountFee = discountFee;
        this.shippingType = shippingType;
        this.platformType = platformType;
        this.rateStatus = rateStatus;
        this.couponCode = couponCode;
        this.groupBuyStatus = groupBuyStatus;
        this.isDeleted = isDeleted;
        this.createTime = createTime;
        this.payTime = payTime;
        this.consignTime = consignTime;
        this.receiveTime = receiveTime;
        this.updateTime = updateTime;
        this.timeoutActionTime = timeoutActionTime;
        this.endTime = endTime;
        this.payBillId = payBillId;
    }


    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 平台订单编码
    private String platformOrderCode;

    // 渠道编码
    private String channelCode;

    // 来源平台编码
    private String platformCode;

    // 用户id
    private String userId;

    // 会员名称
    private String userName;

    // 买家购买的商品总数
    private Integer itemNum;

    // 支付类型
    private String payType;

    // 实付金额,单位/分
    private Long payment;

    // 积分抵扣金额,单位/分
    private Long pointsFee;

    // 订单总金额(商品单价*数量),单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long totalFee;

    // 卖家手工调整金额,子订单调整金额之和,单位/分
    private Long adjustFee;

    // 邮费,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long postageFee;

    // 总税费,单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long totalTax;

    // 是否开票 1-是 0-不是
    private Byte needInvoice;

    // 发票抬头
    private String invoiceName;

    // 发票类型
    private String invoiceType;

    // 发票内容
    private String invoiceMain;

    // 收货人所在省
    private String receiverProvince;

    // 收货人所在城市
    private String receiverCity;

    // 收货人所在地区
    private String receiverDistrict;

    // 收货人详细地址
    private String receiverAddress;

    // 收货人邮编
    private String receiverZip;

    // 收货人姓名
    private String receiverName;

    // 收货人身份证
    private String receiverIdCard;

    // 收货人身份证正面
    private String receiverIdCardFront;

    // 收货人身份证背面
    private String receiverIdCardBack;

    // 收货人电话号码
    private String receiverPhone;

    // 收货人手机号码
    private String receiverMobile;

    // 收货人电子邮箱
    private String receiverEmail;

    // 买家下单地区
    private String buyerArea;

    // 自提备注
    private String zitiMemo;

    // 自提地址
    private String zitiAddr;

    // 是否匿名下单 1-匿名 0-实名
    private Byte anony;

    // 买家下单送积分
    private Integer obtainPointFee;

    // 买家使用积分
    private Integer realPointFee;

    // 分阶段付款状态
    private String stepTradeStatus;

    // 分阶段已付金额,单位/分
    private Long stepPaidFee;

    // 是否生成结算清单 0-否 1-是
    private Byte isClearing;

    // 订单取消原因
    private String cancelReason;

    // 成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL
    private String cancelStatus;

    // 订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
    private String status;

    // 是否为虚拟订单 0-否 1-是
    private Byte isVirtual;

    // ip地址
    private String ip;

    // 订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
    private Byte type;

    // 促销优惠总金额,单位/分
    private Long discountPromotion;

    // 店铺优惠卷优惠金额,单位/分
    private Long discountCouponShop;

    // 平台优惠卷优惠金额,单位/分
    private Long discountCouponPlatform;

    // 订单优惠总金额,单位/分
    private Long discountFee;

    // 配送类型
    private String shippingType;

    // 订单来源平台 电脑-pc 手机网页-wap 移动端-app
    private String platformType;

    // 用户评价状态 0-未评价 1-已评价
    private Byte rateStatus;

    // 应用优惠卷码
    private String couponCode;

    // 拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败
    private String groupBuyStatus;

    // 是否删除:0-否,1-是
    private String isDeleted;

    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    private Date createTime;

    // 支付时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;

    // 发货时间
    private Date consignTime;

    // 确认收货时间
    private Date receiveTime;

    // 修改时间
    private Date updateTime;

    // 订单未支付超时过期时间
    private Date timeoutActionTime;

    // 订单结束时间
    private Date endTime;

    // 支付流水号
    private String payBillId;

    /**
     * 返回主键
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回平台订单编码
     * @return 平台订单编码
     */
    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    /**
     * 设置平台订单编码
     */
    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode == null ? null : platformOrderCode.trim();
    }

    /**
     * 返回渠道编码
     * @return 渠道编码
     */
    public String getChannelCode() {
        return channelCode;
    }

    /**
     * 设置渠道编码
     */
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    /**
     * 返回来源平台编码
     * @return 来源平台编码
     */
    public String getPlatformCode() {
        return platformCode;
    }

    /**
     * 设置来源平台编码
     */
    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode == null ? null : platformCode.trim();
    }

    /**
     * 返回用户id
     * @return 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 返回会员名称
     * @return 会员名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置会员名称
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 返回买家购买的商品总数
     * @return 买家购买的商品总数
     */
    public Integer getItemNum() {
        return itemNum;
    }

    /**
     * 设置买家购买的商品总数
     */
    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    /**
     * 返回支付类型
     * @return 支付类型
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置支付类型
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    /**
     * 返回实付金额,单位/分
     * @return 实付金额,单位/分
     */
    public Long getPayment() {
        return payment;
    }

    /**
     * 设置实付金额,单位/分
     */
    public void setPayment(Long payment) {
        this.payment = payment;
    }

    /**
     * 返回积分抵扣金额,单位/分
     * @return 积分抵扣金额,单位/分
     */
    public Long getPointsFee() {
        return pointsFee;
    }

    /**
     * 设置积分抵扣金额,单位/分
     */
    public void setPointsFee(Long pointsFee) {
        this.pointsFee = pointsFee;
    }

    /**
     * 返回订单总金额(商品单价*数量),单位/分
     * @return 订单总金额(商品单价*数量),单位/分
     */
    public Long getTotalFee() {
        return totalFee;
    }

    /**
     * 设置订单总金额(商品单价*数量),单位/分
     */
    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * 返回卖家手工调整金额,子订单调整金额之和,单位/分
     * @return 卖家手工调整金额,子订单调整金额之和,单位/分
     */
    public Long getAdjustFee() {
        return adjustFee;
    }

    /**
     * 设置卖家手工调整金额,子订单调整金额之和,单位/分
     */
    public void setAdjustFee(Long adjustFee) {
        this.adjustFee = adjustFee;
    }

    /**
     * 返回邮费,单位/分
     * @return 邮费,单位/分
     */
    public Long getPostageFee() {
        return postageFee;
    }

    /**
     * 设置邮费,单位/分
     */
    public void setPostageFee(Long postageFee) {
        this.postageFee = postageFee;
    }

    /**
     * 返回总税费,单位/分
     * @return 总税费,单位/分
     */
    public Long getTotalTax() {
        return totalTax;
    }

    /**
     * 设置总税费,单位/分
     */
    public void setTotalTax(Long totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * 返回是否开票 1-是 0-不是
     * @return 是否开票 1-是 0-不是
     */
    public Byte getNeedInvoice() {
        return needInvoice;
    }

    /**
     * 设置是否开票 1-是 0-不是
     */
    public void setNeedInvoice(Byte needInvoice) {
        this.needInvoice = needInvoice;
    }

    /**
     * 返回发票抬头
     * @return 发票抬头
     */
    public String getInvoiceName() {
        return invoiceName;
    }

    /**
     * 设置发票抬头
     */
    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName == null ? null : invoiceName.trim();
    }

    /**
     * 返回发票类型
     * @return 发票类型
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * 设置发票类型
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType == null ? null : invoiceType.trim();
    }

    /**
     * 返回发票内容
     * @return 发票内容
     */
    public String getInvoiceMain() {
        return invoiceMain;
    }

    /**
     * 设置发票内容
     */
    public void setInvoiceMain(String invoiceMain) {
        this.invoiceMain = invoiceMain == null ? null : invoiceMain.trim();
    }

    /**
     * 返回收货人所在省
     * @return 收货人所在省
     */
    public String getReceiverProvince() {
        return receiverProvince;
    }

    /**
     * 设置收货人所在省
     */
    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince == null ? null : receiverProvince.trim();
    }

    /**
     * 返回收货人所在城市
     * @return 收货人所在城市
     */
    public String getReceiverCity() {
        return receiverCity;
    }

    /**
     * 设置收货人所在城市
     */
    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity == null ? null : receiverCity.trim();
    }

    /**
     * 返回收货人所在地区
     * @return 收货人所在地区
     */
    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    /**
     * 设置收货人所在地区
     */
    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict == null ? null : receiverDistrict.trim();
    }

    /**
     * 返回收货人详细地址
     * @return 收货人详细地址
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * 设置收货人详细地址
     */
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    /**
     * 返回收货人邮编
     * @return 收货人邮编
     */
    public String getReceiverZip() {
        return receiverZip;
    }

    /**
     * 设置收货人邮编
     */
    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip == null ? null : receiverZip.trim();
    }

    /**
     * 返回收货人姓名
     * @return 收货人姓名
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * 设置收货人姓名
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    /**
     * 返回收货人身份证
     * @return 收货人身份证
     */
    public String getReceiverIdCard() {
        return receiverIdCard;
    }

    /**
     * 设置收货人身份证
     */
    public void setReceiverIdCard(String receiverIdCard) {
        this.receiverIdCard = receiverIdCard == null ? null : receiverIdCard.trim();
    }

    /**
     * 返回收货人身份证正面
     * @return 收货人身份证正面
     */
    public String getReceiverIdCardFront() {
        return receiverIdCardFront;
    }

    /**
     * 设置收货人身份证正面
     */
    public void setReceiverIdCardFront(String receiverIdCardFront) {
        this.receiverIdCardFront = receiverIdCardFront == null ? null : receiverIdCardFront.trim();
    }

    /**
     * 返回收货人身份证背面
     * @return 收货人身份证背面
     */
    public String getReceiverIdCardBack() {
        return receiverIdCardBack;
    }

    /**
     * 设置收货人身份证背面
     */
    public void setReceiverIdCardBack(String receiverIdCardBack) {
        this.receiverIdCardBack = receiverIdCardBack == null ? null : receiverIdCardBack.trim();
    }

    /**
     * 返回收货人电话号码
     * @return 收货人电话号码
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * 设置收货人电话号码
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    /**
     * 返回收货人手机号码
     * @return 收货人手机号码
     */
    public String getReceiverMobile() {
        return receiverMobile;
    }

    /**
     * 设置收货人手机号码
     */
    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile == null ? null : receiverMobile.trim();
    }

    /**
     * 返回买家下单地区
     * @return 买家下单地区
     */
    public String getBuyerArea() {
        return buyerArea;
    }

    /**
     * 设置买家下单地区
     */
    public void setBuyerArea(String buyerArea) {
        this.buyerArea = buyerArea == null ? null : buyerArea.trim();
    }

    /**
     * 返回自提备注
     * @return 自提备注
     */
    public String getZitiMemo() {
        return zitiMemo;
    }

    /**
     * 设置自提备注
     */
    public void setZitiMemo(String zitiMemo) {
        this.zitiMemo = zitiMemo == null ? null : zitiMemo.trim();
    }

    /**
     * 返回自提地址
     * @return 自提地址
     */
    public String getZitiAddr() {
        return zitiAddr;
    }

    /**
     * 设置自提地址
     */
    public void setZitiAddr(String zitiAddr) {
        this.zitiAddr = zitiAddr == null ? null : zitiAddr.trim();
    }

    /**
     * 返回是否匿名下单 1-匿名 0-实名
     * @return 是否匿名下单 1-匿名 0-实名
     */
    public Byte getAnony() {
        return anony;
    }

    /**
     * 设置是否匿名下单 1-匿名 0-实名
     */
    public void setAnony(Byte anony) {
        this.anony = anony;
    }

    /**
     * 返回买家下单送积分
     * @return 买家下单送积分
     */
    public Integer getObtainPointFee() {
        return obtainPointFee;
    }

    /**
     * 设置买家下单送积分
     */
    public void setObtainPointFee(Integer obtainPointFee) {
        this.obtainPointFee = obtainPointFee;
    }

    /**
     * 返回买家使用积分
     * @return 买家使用积分
     */
    public Integer getRealPointFee() {
        return realPointFee;
    }

    /**
     * 设置买家使用积分
     */
    public void setRealPointFee(Integer realPointFee) {
        this.realPointFee = realPointFee;
    }

    /**
     * 返回分阶段付款状态
     * @return 分阶段付款状态
     */
    public String getStepTradeStatus() {
        return stepTradeStatus;
    }

    /**
     * 设置分阶段付款状态
     */
    public void setStepTradeStatus(String stepTradeStatus) {
        this.stepTradeStatus = stepTradeStatus == null ? null : stepTradeStatus.trim();
    }

    /**
     * 返回分阶段已付金额,单位/分
     * @return 分阶段已付金额,单位/分
     */
    public Long getStepPaidFee() {
        return stepPaidFee;
    }

    /**
     * 设置分阶段已付金额,单位/分
     */
    public void setStepPaidFee(Long stepPaidFee) {
        this.stepPaidFee = stepPaidFee;
    }

    /**
     * 返回是否生成结算清单 0-否 1-是
     * @return 是否生成结算清单 0-否 1-是
     */
    public Byte getIsClearing() {
        return isClearing;
    }

    /**
     * 设置是否生成结算清单 0-否 1-是
     */
    public void setIsClearing(Byte isClearing) {
        this.isClearing = isClearing;
    }

    /**
     * 返回订单取消原因
     * @return 订单取消原因
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 设置订单取消原因
     */
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    /**
     * 返回成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL
     * @return 成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL
     */
    public String getCancelStatus() {
        return cancelStatus;
    }

    /**
     * 设置成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL
     */
    public void setCancelStatus(String cancelStatus) {
        this.cancelStatus = cancelStatus == null ? null : cancelStatus.trim();
    }

    /**
     * 返回订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     * @return 订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 返回是否为虚拟订单 0-否 1-是
     * @return 是否为虚拟订单 0-否 1-是
     */
    public Byte getIsVirtual() {
        return isVirtual;
    }

    /**
     * 设置是否为虚拟订单 0-否 1-是
     */
    public void setIsVirtual(Byte isVirtual) {
        this.isVirtual = isVirtual;
    }

    /**
     * 返回ip地址
     * @return ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip地址
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * 返回订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
     * @return 订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 返回促销优惠总金额,单位/分
     * @return 促销优惠总金额,单位/分
     */
    public Long getDiscountPromotion() {
        return discountPromotion;
    }

    /**
     * 设置促销优惠总金额,单位/分
     */
    public void setDiscountPromotion(Long discountPromotion) {
        this.discountPromotion = discountPromotion;
    }

    /**
     * 返回店铺优惠卷优惠金额,单位/分
     * @return 店铺优惠卷优惠金额,单位/分
     */
    public Long getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置店铺优惠卷优惠金额,单位/分
     */
    public void setDiscountCouponShop(Long discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回平台优惠卷优惠金额,单位/分
     * @return 平台优惠卷优惠金额,单位/分
     */
    public Long getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置平台优惠卷优惠金额,单位/分
     */
    public void setDiscountCouponPlatform(Long discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回订单优惠总金额,单位/分
     * @return 订单优惠总金额,单位/分
     */
    public Long getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置订单优惠总金额,单位/分
     */
    public void setDiscountFee(Long discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回配送类型
     * @return 配送类型
     */
    public String getShippingType() {
        return shippingType;
    }

    /**
     * 设置配送类型
     */
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType == null ? null : shippingType.trim();
    }

    /**
     * 返回订单来源平台 电脑-pc 手机网页-wap 移动端-app
     * @return 订单来源平台 电脑-pc 手机网页-wap 移动端-app
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * 设置订单来源平台 电脑-pc 手机网页-wap 移动端-app
     */
    public void setPlatformType(String platformType) {
        this.platformType = platformType == null ? null : platformType.trim();
    }

    /**
     * 返回用户评价状态 0-未评价 1-已评价
     * @return 用户评价状态 0-未评价 1-已评价
     */
    public Byte getRateStatus() {
        return rateStatus;
    }

    /**
     * 设置用户评价状态 0-未评价 1-已评价
     */
    public void setRateStatus(Byte rateStatus) {
        this.rateStatus = rateStatus;
    }

    /**
     * 返回应用优惠卷码
     * @return 应用优惠卷码
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * 设置应用优惠卷码
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode == null ? null : couponCode.trim();
    }

    /**
     * 返回拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败
     * @return 拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败
     */
    public String getGroupBuyStatus() {
        return groupBuyStatus;
    }

    /**
     * 设置拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败
     */
    public void setGroupBuyStatus(String groupBuyStatus) {
        this.groupBuyStatus = groupBuyStatus == null ? null : groupBuyStatus.trim();
    }

    /**
     * 返回是否删除:0-否,1-是
     * @return 是否删除:0-否,1-是
     */
    public String getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除:0-否,1-是
     */
    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted == null ? null : isDeleted.trim();
    }

    /**
     * 返回创建时间,格式yyyy-mm-dd hh:mi:ss
     * @return 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 返回支付时间
     * @return 支付时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置支付时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 返回发货时间
     * @return 发货时间
     */
    public Date getConsignTime() {
        return consignTime;
    }

    /**
     * 设置发货时间
     */
    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
    }

    /**
     * 返回确认收货时间
     * @return 确认收货时间
     */
    public Date getReceiveTime() {
        return receiveTime;
    }

    /**
     * 设置确认收货时间
     */
    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * 返回修改时间
     * @return 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 返回订单未支付超时过期时间
     * @return 订单未支付超时过期时间
     */
    public Date getTimeoutActionTime() {
        return timeoutActionTime;
    }

    /**
     * 设置订单未支付超时过期时间
     */
    public void setTimeoutActionTime(Date timeoutActionTime) {
        this.timeoutActionTime = timeoutActionTime;
    }

    /**
     * 返回订单结束时间
     * @return 订单结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置订单结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 返回支付流水号
     * @return 支付流水号
     */
    public String getPayBillId() {
        return payBillId;
    }

    /**
     * 设置支付流水号
     */
    public void setPayBillId(String payBillId) {
        this.payBillId = payBillId == null ? null : payBillId.trim();
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }
}