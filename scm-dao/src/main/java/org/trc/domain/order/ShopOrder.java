package org.trc.domain.order;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Ding on 2017/6/21.
 */
@Getter
@Setter
public class ShopOrder extends OrderBase {

    private static final long serialVersionUID = 5954865299939357741L;

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //系统订单号
    @NotEmpty
    @Length(max = 32)
    private String scmShopOrderCode;
    // 店铺订单编码
    @NotEmpty
    @Length(max = 32)
    private String shopOrderCode;
    // 平台订单编码
    @NotEmpty
    @Length(max = 32)
    private String platformOrderCode;
    // 渠道编码
    @NotEmpty
    @Length(max = 32)
    private String channelCode;
    // 来源平台编码
    /*@NotEmpty
    @Length(max = 32)
    private String platformCode;
    // 订单来源平台 电脑-pc 手机网页-wap 移动端-app
    @NotEmpty
    @Length(max = 32)
    private String platformType;*/
    // 订单所属的店铺id
    @NotEmpty
    private Long shopId;
    // 店铺名称
    @NotEmpty
    @Length(max = 255)
    private String shopName;
    // 会员id
    @NotEmpty
    @Length(max = 64)
    private String userId;
    // 配送模板ids(1,2,3)
    /*@Length(max = 255)
    private String dlytmplIds;*/
    // 子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
    @NotEmpty
    @Length(max = 32)
    private String status;
    // 是否删除:0-否,1-是
    @NotEmpty
    @Length(max = 2 )
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
    private BigDecimal discountPromotion;
    // 店铺优惠卷分摊总金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountCouponShop;
    // 平台优惠卷分摊总金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountCouponPlatform;
    // 促销优惠金额,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountFee;
    // 交易标题
    /*@Length(max = 128)
    private String title;*/
    // 买家留言
    @Length(max = 255)
    private String buyerMessage;
    // 卖家手工调整金额,子订单调整金额之和,单位/分,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal adjustFee;
    // 子订单商品购买数量总数
    private Integer itemNum;
    // 商品重量,单位/克
    private BigDecimal totalWeight;
    // 评价状态
    /*@Length(max = 4)
    private String rateStatus;
    // 是否是多次发货的订单
    @Length(max = 4)
    private String isPartConsign;
    // 拼团状态
    @Length(max = 32)
    private String groupBuyStatus;*/
    // 订单总税费,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalTax;
    // 支付时间
    @JsonSerialize(using = CustomDateSerializer.class)
    @NotEmpty
    private Date payTime;
    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @NotEmpty
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    // 发货时间
   /* @NotEmpty
    private Date consignTime;*/
    // 更新时间
    @NotEmpty
    private Date updateTime;
    // 卖家备注
    private String shopMemo;
    // 交易备注
    private String tradeMemo;
    //供应商订单状态：1-待发货,2-部分发货,3-全部发货,4-已取消
    private String supplierOrderStatus;
    // 0-接口,1-导入
    private String reciverType;
    /**
     * 订单商品明细
     */
    @Transient
    private List<OrderExt> records;
    @Transient
    private List<OrderItem> orderItems;

    /**
     * 是否门店订单
     */
    @Transient
    private boolean isStoreOrder;

    /**
     * 运单号
     */
    @Transient
    private String waybillNumber;

    public ShopOrder() {

    }

    public boolean getIsStoreOrder() {
        return isStoreOrder;
    }

    public void setIsStoreOrder(boolean storeOrder) {
        isStoreOrder = storeOrder;
    }
}