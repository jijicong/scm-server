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
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Ding on 2017/6/21.
 */
@Getter
@Setter
@Table(name = "platform_order")
public class PlatformOrder  implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 平台订单编码
    @Length(max = 32)
    private String platformOrderCode;
    // 渠道编码
    @Length(max = 32)
    private String channelCode;
    // 销售渠道编码
    @Length(max = 32)
    private String sellCode;
    // 来源平台编码
    /*@Length(max = 32)
    private String platformCode;*/
    // 用户id
    @Length(max = 64)
    private String userId;
    // 会员名称
    @Length(max = 32)
    private String userName;
    // 买家购买的商品总数
    @NotEmpty
    private Integer itemNum;
    // 支付类型
    @Length(max = 16)
    private String payType;
    // 实付金额,单位/分
    @NotEmpty
    private BigDecimal payment;
    // 积分抵扣金额,单位/分
    @NotEmpty
    private BigDecimal pointsFee;
    // 订单总金额(商品单价*数量),单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @NotEmpty
    private BigDecimal totalFee;
    // 卖家手工调整金额,子订单调整金额之和,单位/分
    @NotEmpty
    private BigDecimal adjustFee;
    // 邮费,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @NotEmpty
    private BigDecimal postageFee;
    // 总税费,单位/分
    //@JsonSerialize(using = MoneySerializer.class)
    @NotEmpty
    private BigDecimal totalTax;
    // 是否开票 1-是 0-不是
    @NotEmpty
    @Length(max = 10)
    private String needInvoice;
    // 发票抬头
    @Length(max = 128)
    private String invoiceName;
    // 发票类型
    @Length(max = 32)
    private String invoiceType;
    // 发票内容
    @Length(max = 128)
    private String invoiceMain;
    // 收货人所在省
    @Length(max = 16)
    private String receiverProvince;
    // 收货人所在城市
    @Length(max = 16)
    private String receiverCity;
    // 收货人所在地区
    @Length(max = 16)
    private String receiverDistrict;
    // 收货人详细地址
    @Length(max = 256)
    private String receiverAddress;
    // 收货人邮编
    @Length(max = 16)
    private String receiverZip;
    // 收货人姓名
    @Length(max = 128)
    private String receiverName;
    // 收货人身份证
    @Length(max = 32)
    private String receiverIdCard;
    // 收货人身份证正面
    @Length(max = 255)
    private String receiverIdCardFront;
    // 收货人身份证背面
    @Length(max = 255)
    private String receiverIdCardBack;
    // 收货人电话号码
    @Length(max = 16)
    private String receiverPhone;
    // 收货人手机号码
    @Length(max = 16)
    private String receiverMobile;
    // 收货人电子邮箱
    @Length(max = 64)
    private String receiverEmail;
    // 买家下单地区
    @Length(max = 32)
    private String buyerArea;
    // 自提备注
    @Length(max = 255)
    private String zitiMemo;
    // 自提地址
    @Length(max = 255)
    private String zitiAddr;
    // 是否匿名下单 1-匿名 0-实名
    /*@NotEmpty
    @Length(max = 4)
    private String anony;
    // 买家下单送积分
    private Integer obtainPointFee;*/
    // 买家使用积分
    private Integer realPointFee;
    // 分阶段付款状态
    @Length(max = 32)
    private String stepTradeStatus;
    // 分阶段已付金额,单位/分
    private BigDecimal stepPaidFee;
    // 是否生成结算清单 0-否 1-是
   /* @Length(max = 4)
    private String isClearing;*/
    // 订单取消原因
    @Length(max = 255)
    private String cancelReason;
    // 成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL
    @Length(max = 32)
    private String cancelStatus;
    // 订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)
    @Length(max = 32)
    private String status;
    // 是否为虚拟订单 0-否 1-是
    /*@Length(max = 4)
    private String isVirtual;*/
    // ip地址
    /*@Length(max = 32)
    private String ip;*/
    // 订单类型：0-普通订单 1-零元购 2-分期购 3-拼团
    @NotEmpty
    @Length(max = 4)
    private String type;
    // 促销优惠总金额,单位/分
    private BigDecimal discountPromotion;
    // 店铺优惠卷优惠金额,单位/分
    private BigDecimal discountCouponShop;
    // 平台优惠卷优惠金额,单位/分
    private BigDecimal discountCouponPlatform;
    // 订单优惠总金额,单位/分
    private BigDecimal discountFee;
    // 配送类型
    @Length(max = 32)
    private String shippingType;
    // 订单来源平台 电脑-pc 手机网页-wap 移动端-app
   /* @NotEmpty
    @Length(max = 32)
    private String platformType;
    // 用户评价状态 0-未评价 1-已评价
    @NotEmpty
    private String rateStatus;
    // 应用优惠卷码
    @Length(max = 32)
    private String couponCode;
    // 拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败
    @Length(max = 32)
    private String groupBuyStatus;*/
    // 是否删除:0-否,1-是
    @NotEmpty
    @Length(max = 2)
    private String isDeleted;
    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @NotEmpty
    private Date createTime;
    // 支付时间
    @JsonSerialize(using = CustomDateSerializer.class)
    @NotEmpty
    private Date payTime;
    // 发货时间
    /*@NotEmpty
    private Date consignTime;
    // 确认收货时间
    @NotEmpty
    private Date receiveTime;*/
    // 修改时间
    @NotEmpty
    private Date updateTime;
    // 订单未支付超时过期时间
   /* @NotEmpty
    private Date timeoutActionTime;
    // 订单结束时间
    @NotEmpty
    private Date endTime;*/
    // 支付流水号
    @Length(max = 32)
    private String payBillId;

    public PlatformOrder(){

    }

}