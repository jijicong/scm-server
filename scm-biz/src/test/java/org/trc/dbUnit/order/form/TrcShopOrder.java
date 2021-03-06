package org.trc.dbUnit.order.form;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商铺订单
 * Created by Ding on 2017/6/21.
 */
public class TrcShopOrder {

    public TrcShopOrder() {

    }

    public TrcShopOrder(String shopOrderCode, String platformOrderCode, String channelCode, String platformCode, String platformType, Long shopId, String shopName, String userId, String dlytmplIds, String status, String isDeleted, BigDecimal payment, BigDecimal totalFee, BigDecimal postageFee, Double discountPromotion, Double discountCouponShop, Double discountCouponPlatform, Double discountFee, String title, String buyerMessage, Double adjustFee, Integer itemNum, Double totalWeight, String rateStatus, String isPartConsign, String groupBuyStatus, BigDecimal totalTax, Date createTime, Date consignTime, Date updateTime, String shopMemo, String tradeMemo) {
        this.shopOrderCode = shopOrderCode;
        this.platformOrderCode = platformOrderCode;
        this.channelCode = channelCode;
        this.platformCode = platformCode;
        this.platformType = platformType;
        this.shopId = shopId;
        this.shopName = shopName;
        this.userId = userId;
        this.dlytmplIds = dlytmplIds;
        this.status = status;
        this.isDeleted = isDeleted;
        this.payment = payment;
        this.totalFee = totalFee;
        this.postageFee = postageFee;
        this.discountPromotion = discountPromotion;
        this.discountCouponShop = discountCouponShop;
        this.discountCouponPlatform = discountCouponPlatform;
        this.discountFee = discountFee;
        this.title = title;
        this.buyerMessage = buyerMessage;
        this.adjustFee = adjustFee;
        this.itemNum = itemNum;
        this.totalWeight = totalWeight;
        this.rateStatus = rateStatus;
        this.isPartConsign = isPartConsign;
        this.groupBuyStatus = groupBuyStatus;
        this.totalTax = totalTax;
        this.createTime = createTime;
        this.consignTime = consignTime;
        this.updateTime = updateTime;
        this.shopMemo = shopMemo;
        this.tradeMemo = tradeMemo;
    }

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 店铺订单编码
    private String shopOrderCode;

    // 平台订单编码
    private String platformOrderCode;

    // 渠道编码
    private String channelCode;

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    // 销售渠道编码
    private String sellCode;

    // 来源平台编码
    private String platformCode;

    // 订单来源平台 电脑-pc 手机网页-wap 移动端-app
    private String platformType;

    // 订单所属的店铺id
    private Long shopId;

    // 店铺名称
    private String shopName;


    // 会员id
    private String userId;

    // 配送模板ids(1,2,3)
    private String dlytmplIds;

    // 子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
    private String status;

    // 是否删除:0-否,1-是
    private String isDeleted;

    // 实付金额,订单最终总额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal payment;

    // 各子订单中商品price * num的和，不包括任何优惠信息,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalFee;

    // 邮费分摊,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal postageFee;

    // 促销优惠总金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private Double discountPromotion;

    // 店铺优惠卷分摊总金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private Double discountCouponShop;

    // 平台优惠卷分摊总金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private Double discountCouponPlatform;

    // 促销优惠金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private Double discountFee;

    // 交易标题
    private String title;

    // 买家留言
    private String buyerMessage;

    // 卖家手工调整金额,子订单调整金额之和,单位/分,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private Double adjustFee;

    // 子订单商品购买数量总数
    private Integer itemNum;

    // 商品重量
    private Double totalWeight;

    // 评价状态
    private String rateStatus;

    // 是否是多次发货的订单
    private String isPartConsign;

    // 拼团状态
    private String groupBuyStatus;

    // 订单总税费,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalTax;

    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    private Date createTime;

    // 发货时间
    private Date consignTime;

    // 更新时间
    private Date updateTime;

    // 卖家备注
    private String shopMemo;

    // 交易备注
    private String tradeMemo;


    /**
     * 返回卖家备注
     *
     * @return 卖家备注
     */
    public String getShopMemo() {
        return shopMemo;
    }

    /**
     * 设置卖家备注
     */
    public void setShopMemo(String shopMemo) {
        this.shopMemo = shopMemo == null ? null : shopMemo.trim();
    }

    /**
     * 返回交易备注
     *
     * @return 交易备注
     */
    public String getTradeMemo() {
        return tradeMemo;
    }

    /**
     * 设置交易备注
     */
    public void setTradeMemo(String tradeMemo) {
        this.tradeMemo = tradeMemo == null ? null : tradeMemo.trim();
    }

    /**
     * 返回主键
     *
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
     * 返回店铺订单编码
     *
     * @return 店铺订单编码
     */
    public String getShopOrderCode() {
        return shopOrderCode;
    }

    /**
     * 设置店铺订单编码
     */
    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode == null ? null : shopOrderCode.trim();
    }

    /**
     * 返回平台订单编码
     *
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
     *
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
     *
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
     * 返回订单来源平台 电脑-pc 手机网页-wap 移动端-app
     *
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
     * 返回订单所属的店铺id
     *
     * @return 订单所属的店铺id
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * 设置订单所属的店铺id
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * 返回店铺名称
     *
     * @return 店铺名称
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * 设置店铺名称
     */
    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }


    /**
     * 返回会员id
     *
     * @return 会员id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置会员id
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 返回配送模板ids(1,2,3)
     *
     * @return 配送模板ids(1, 2, 3)
     */
    public String getDlytmplIds() {
        return dlytmplIds;
    }

    /**
     * 设置配送模板ids(1,2,3)
     */
    public void setDlytmplIds(String dlytmplIds) {
        this.dlytmplIds = dlytmplIds == null ? null : dlytmplIds.trim();
    }

    /**
     * 返回子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     *
     * @return 子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 返回是否删除:0-否,1-是
     *
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
     * 返回实付金额,订单最终总额,单位/分
     *
     * @return 实付金额, 订单最终总额, 单位/分
     */
    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    /**
     * 返回各子订单中商品price * num的和，不包括任何优惠信息,单位/分
     *
     * @return 各子订单中商品price * num的和，不包括任何优惠信息,单位/分
     */
    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getPostageFee() {
        return postageFee;
    }

    public void setPostageFee(BigDecimal postageFee) {
        this.postageFee = postageFee;
    }

    /**
     * 返回促销优惠总金额,单位/分
     *
     * @return 促销优惠总金额, 单位/分
     */
    public Double getDiscountPromotion() {
        return discountPromotion;
    }

    /**
     * 设置促销优惠总金额,单位/分
     */
    public void setDiscountPromotion(Double discountPromotion) {
        this.discountPromotion = discountPromotion;
    }

    /**
     * 返回店铺优惠卷分摊总金额,单位/分
     *
     * @return 店铺优惠卷分摊总金额, 单位/分
     */
    public Double getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置店铺优惠卷分摊总金额,单位/分
     */
    public void setDiscountCouponShop(Double discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回平台优惠卷分摊总金额,单位/分
     *
     * @return 平台优惠卷分摊总金额, 单位/分
     */
    public Double getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置平台优惠卷分摊总金额,单位/分
     */
    public void setDiscountCouponPlatform(Double discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回促销优惠金额,单位/分
     *
     * @return 促销优惠金额, 单位/分
     */
    public Double getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置促销优惠金额,单位/分
     */
    public void setDiscountFee(Double discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回交易标题
     *
     * @return 交易标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置交易标题
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * 返回买家留言
     *
     * @return 买家留言
     */
    public String getBuyerMessage() {
        return buyerMessage;
    }

    /**
     * 设置买家留言
     */
    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage == null ? null : buyerMessage.trim();
    }

    /**
     * 返回卖家手工调整金额,子订单调整金额之和,单位/分,单位/分
     *
     * @return 卖家手工调整金额, 子订单调整金额之和, 单位/分,单位/分
     */
    public Double getAdjustFee() {
        return adjustFee;
    }

    /**
     * 设置卖家手工调整金额,子订单调整金额之和,单位/分,单位/分
     */
    public void setAdjustFee(Double adjustFee) {
        this.adjustFee = adjustFee;
    }

    /**
     * 返回子订单商品购买数量总数
     *
     * @return 子订单商品购买数量总数
     */
    public Integer getItemNum() {
        return itemNum;
    }

    /**
     * 设置子订单商品购买数量总数
     */
    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    /**
     * 返回商品重量,单位/克
     *
     * @return 商品重量, 单位/克
     */
    public Double getTotalWeight() {
        return totalWeight;
    }

    /**
     * 设置商品重量,单位/克
     */
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * 返回评价状态
     *
     * @return 评价状态
     */
    public String getRateStatus() {
        return rateStatus;
    }

    /**
     * 设置评价状态
     */
    public void setRateStatus(String rateStatus) {
        this.rateStatus = rateStatus;
    }

    /**
     * 返回是否是多次发货的订单
     *
     * @return 是否是多次发货的订单
     */
    public String getIsPartConsign() {
        return isPartConsign;
    }

    /**
     * 设置是否是多次发货的订单
     */
    public void setIsPartConsign(String isPartConsign) {
        this.isPartConsign = isPartConsign;
    }

    /**
     * 返回拼团状态
     *
     * @return 拼团状态
     */
    public String getGroupBuyStatus() {
        return groupBuyStatus;
    }

    /**
     * 设置拼团状态
     */
    public void setGroupBuyStatus(String groupBuyStatus) {
        this.groupBuyStatus = groupBuyStatus == null ? null : groupBuyStatus.trim();
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * 返回创建时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @return 创建时间, 格式yyyy-mm-dd hh:mi:ss
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
     * 返回发货时间
     *
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
     * 返回更新时间
     *
     * @return 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}