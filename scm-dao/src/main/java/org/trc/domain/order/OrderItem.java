package org.trc.domain.order;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Ding on 2017/6/21.
 */
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //系统订单号
    @NotEmpty
    @Length(max = 32)
    private String scmShopOrderCode;

    // 渠道商品订单号
    @NotEmpty
    @Length(max = 32)
    private String orderItemCode;

    // 仓库订单编码
    @NotEmpty
    @Length(max = 32)
    private String warehouseOrderCode;
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
    // 销售渠道编码
    @NotEmpty
    @Length(max = 32)
    private String sellCode;
    // 来源平台编码
    @NotEmpty
    @Length(max = 32)
    private String platformCode;
    // 所在仓库id
    private Long warehouseId;
    // 所在仓库名称
    @Length(max = 64)
    private String warehouseName;
    // 所属类目编号
    @NotEmpty
    private String category;
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
    // 商品SPU编号
    @Length(max = 64)
    private String spuCode;
    // sku编码
    @Length(max = 32)
    private String skuCode;
    //供应商sku编码
    @Length(max = 32)
    private String supplierSkuCode;
    // 商品sku库存id
    /*private Long skuStockId;*/
    // 商品货号
    @NotEmpty
    @Length(max = 32)
    private String itemNo;
    // 条形码
    @Length(max = 64)
    private String barCode;
    // 商品名称
    @NotEmpty
    @Length(max = 128)
    private String itemName;
    // 商品价格,单位/元
    private BigDecimal price;
    // 市场价,单位/元
    private BigDecimal marketPrice;
    // 促销价,单位/元
    private BigDecimal promotionPrice;
    // 报关单价,单位/元
    private BigDecimal customsPrice;
    // 成交单价,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal transactionPrice;
    // 购买数量
    private Integer num;
    // 明细商品发货数量
    private Integer sendNum;
    // SKU的值
    /*@Length(max = 512)
    private String skuPropertiesName;
    // 最近退款ID
    @Length(max = 32)
    private String refundId;
    // 是否超卖
    @Length(max = 20)
    private String isOversold;
    // 运送方式
    @Length(max = 32)
    private String shippingType;
    // 捆绑的子订单号
    @Length(max = 32)
    private String bindOid;
    // 子订单发货的快递公司
    @Length(max = 32)
    private String logisticsCompany;
    // 子订单所在包裹的运单号
    @Length(max = 32)
    private String invoiceNo;*/
    // 运费分摊,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal postDiscount;
    // 促销优惠分摊,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountPromotion;
    // 店铺优惠卷分摊金额,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountCouponShop;
    // 平台优惠卷优惠分摊,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountCouponPlatform;
    // 子订单级订单优惠金额,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal discountFee;
    // 应付金额,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalFee;
    // 实付金额,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal payment;
    // 商品重量,单位/克
    private BigDecimal totalWeight;
    // 手工调整金额,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal adjustFee;
    // 订单状态:1-待出库 2-部分出库 3-全部出库
    @Length(max = 32)
    private String status;
    // 售后状态
    /*@Length(max = 32)
    private String afterSalesStatus;
    // 订单投诉状态
    @Length(max = 32)
    private String complaintsStatus;
    // 退款金额,单位/元
    private BigDecimal refundFee;
    // 商家三级类目签约佣金比例
    private BigDecimal catServiceRate;*/
    // 商品图片绝对路径
    @Length(max = 255)
    private String picPath;
    // 商家外部编码
    /*@Length(max = 64)
    private String outerIid;*/
    // 商家外部sku码
    @Length(max = 64)
    private String outerSkuId;
    // 是否支持下单减库存
    @Length(max = 20)
    private String subStock;
    // 配送模板id
    //private Integer dlytmplId;
    // 供应商名称
    @Length(max = 80)
    private String supplierName;
    // 商品税费,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal priceTax;
    // 订单应用促销标签
    /*@Length(max = 32)
    private String promotionTags;
    // 订单商品类型
    @Length(max = 32)
    private String objType;*/
    // 订单类型 0-普通 1-零元购 2-分期购 3-团购
    @NotEmpty
    private String type;
    // 税率
    /*private BigDecimal taxRate;
    // 订单冗余参数
    @Length(max = 255)
    private String params;*/
    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @NotEmpty
    private Date createTime;
    // 支付时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;
    // 发货时间
    /*private Date consignTime;*/
    // 修改时间
    private Date updateTime;
    // 超时确认时间
    /*private Date timeoutActionTime;
    // 关闭时间
    private Date endTime;*/
    // 商品规格描述
    private String specNatureInfo;
    //供应商订单状态：1-待发送供应商,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消
    private String supplierOrderStatus;
    //老供应商订单状态:1-待发送供应商,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消
    private String oldSupplierOrderStatus;
    //供应商订单号
    @Transient
    private String supplierOrderCode;
    //商品类型:0-自采,1-代发
    @Transient
    private String itemType;
    //实发商品数量
    @Transient
    private Integer deliverNum;
    //物流信息
    @Transient
    private List<DeliverPackageForm> deliverPackageFormList;
    //提交供应商时间
    private Date submitTime;

    // 供货价,单位/元
    private BigDecimal supplyPrice;

    // 交易备注
    private String tradeMemo;

    /**
     * 是否门店订单:1-非门店,2-门店'
     */
    private Integer isStoreOrder;

    /**
     * 是否指定仓库:0-是,1-否'
     */
    @Transient
    private String isAppointStock;
    @Transient
    private String waybillNumber;

    public OrderItem(){

    }

    
}