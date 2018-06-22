package org.trc.domain.order;

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
    private Long skuStockId;
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
    @Length(max = 512)
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
    private String invoiceNo;
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
    @Length(max = 32)
    private String afterSalesStatus;
    // 订单投诉状态
    @Length(max = 32)
    private String complaintsStatus;
    // 退款金额,单位/元
    private BigDecimal refundFee;
    // 商家三级类目签约佣金比例
    private BigDecimal catServiceRate;
    // 商品图片绝对路径
    @Length(max = 255)
    private String picPath;
    // 商家外部编码
    @Length(max = 64)
    private String outerIid;
    // 商家外部sku码
    @Length(max = 64)
    private String outerSkuId;
    // 是否支持下单减库存
    @Length(max = 20)
    private String subStock;
    // 配送模板id
    private Integer dlytmplId;
    // 供应商名称
    @Length(max = 80)
    private String supplierName;
    // 商品税费,单位/元
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal priceTax;
    // 订单应用促销标签
    @Length(max = 32)
    private String promotionTags;
    // 订单商品类型
    @Length(max = 32)
    private String objType;
    // 订单类型 0-普通 1-零元购 2-分期购 3-团购
    @NotEmpty
    private String type;
    // 税率
    private BigDecimal taxRate;
    // 订单冗余参数
    @Length(max = 255)
    private String params;
    // 创建时间,格式yyyy-mm-dd hh:mi:ss
    @NotEmpty
    private Date createTime;
    // 支付时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;
    // 发货时间
    private Date consignTime;
    // 修改时间
    private Date updateTime;
    // 超时确认时间
    private Date timeoutActionTime;
    // 关闭时间
    private Date endTime;
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

    public OrderItem(){

    }

    public OrderItem(String warehouseOrderCode, String shopOrderCode, String platformOrderCode, String channelCode, String platformCode, Long warehouseId, String warehouseName, String category, Long shopId, String shopName, String userId, String spuCode, String skuCode, String supplierSkuCode, Long skuStockId, String itemNo, String barCode, String itemName, BigDecimal price, BigDecimal marketPrice, BigDecimal promotionPrice, BigDecimal customsPrice, BigDecimal transactionPrice, Integer num, Integer sendNum, String skuPropertiesName, String refundId, String  isOversold, String shippingType, String bindOid, String logisticsCompany, String invoiceNo, BigDecimal postDiscount, BigDecimal discountPromotion, BigDecimal discountCouponShop, BigDecimal discountCouponPlatform, BigDecimal discountFee, BigDecimal totalFee, BigDecimal payment, BigDecimal totalWeight, BigDecimal adjustFee, String status, String afterSalesStatus, String complaintsStatus, BigDecimal refundFee, BigDecimal catServiceRate, String picPath, String outerIid, String outerSkuId, String subStock, Integer dlytmplId, String supplierName, BigDecimal priceTax, String promotionTags, String objType, String type, BigDecimal taxRate, String params, Date createTime, Date payTime, Date consignTime, Date updateTime, Date timeoutActionTime, Date endTime, String specNatureInfo) {
        this.warehouseOrderCode = warehouseOrderCode;
        this.shopOrderCode = shopOrderCode;
        this.platformOrderCode = platformOrderCode;
        this.channelCode = channelCode;
        this.platformCode = platformCode;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.category = category;
        this.shopId = shopId;
        this.shopName = shopName;
        this.userId = userId;
        this.spuCode = spuCode;
        this.skuCode = skuCode;
        this.supplierSkuCode = supplierSkuCode;
        this.skuStockId = skuStockId;
        this.itemNo = itemNo;
        this.barCode = barCode;
        this.itemName = itemName;
        this.price = price;
        this.marketPrice = marketPrice;
        this.promotionPrice = promotionPrice;
        this.customsPrice = customsPrice;
        this.transactionPrice = transactionPrice;
        this.num = num;
        this.sendNum = sendNum;
        this.skuPropertiesName = skuPropertiesName;
        this.refundId = refundId;
        this.isOversold = isOversold;
        this.shippingType = shippingType;
        this.bindOid = bindOid;
        this.logisticsCompany = logisticsCompany;
        this.invoiceNo = invoiceNo;
        this.postDiscount = postDiscount;
        this.discountPromotion = discountPromotion;
        this.discountCouponShop = discountCouponShop;
        this.discountCouponPlatform = discountCouponPlatform;
        this.discountFee = discountFee;
        this.totalFee = totalFee;
        this.payment = payment;
        this.totalWeight = totalWeight;
        this.adjustFee = adjustFee;
        this.status = status;
        this.afterSalesStatus = afterSalesStatus;
        this.complaintsStatus = complaintsStatus;
        this.refundFee = refundFee;
        this.catServiceRate = catServiceRate;
        this.picPath = picPath;
        this.outerIid = outerIid;
        this.outerSkuId = outerSkuId;
        this.subStock = subStock;
        this.dlytmplId = dlytmplId;
        this.supplierName = supplierName;
        this.priceTax = priceTax;
        this.promotionTags = promotionTags;
        this.objType = objType;
        this.type = type;
        this.taxRate = taxRate;
        this.params = params;
        this.createTime = createTime;
        this.payTime = payTime;
        this.consignTime = consignTime;
        this.updateTime = updateTime;
        this.timeoutActionTime = timeoutActionTime;
        this.endTime = endTime;
        this.specNatureInfo = specNatureInfo;
    }

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

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
     * 返回仓库订单编码
     * @return 仓库订单编码
     */
    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    /**
     * 设置仓库订单编码
     */
    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode == null ? null : warehouseOrderCode.trim();
    }

    /**
     * 返回店铺订单编码
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
     * 返回所在仓库id
     * @return 所在仓库id
     */
    public Long getWarehouseId() {
        return warehouseId;
    }

    /**
     * 设置所在仓库id
     */
    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * 返回所在仓库名称
     * @return 所在仓库名称
     */
    public String getWarehouseName() {
        return warehouseName;
    }

    /**
     * 设置所在仓库名称
     */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 返回订单所属的店铺id
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
     * 返回商品SPU编号
     * @return 商品SPU编号
     */
    public String getSpuCode() {
        return spuCode;
    }

    /**
     * 设置商品SPU编号
     */
    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    /**
     * 返回sku编码
     * @return sku编码
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * 设置sku编码
     */
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    /**
     * 返回商品sku库存id
     * @return 商品sku库存id
     */
    public Long getSkuStockId() {
        return skuStockId;
    }

    /**
     * 设置商品sku库存id
     */
    public void setSkuStockId(Long skuStockId) {
        this.skuStockId = skuStockId;
    }

    /**
     * 返回商品货号
     * @return 商品货号
     */
    public String getItemNo() {
        return itemNo;
    }

    /**
     * 设置商品货号
     */
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo == null ? null : itemNo.trim();
    }

    /**
     * 返回条形码
     * @return 条形码
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * 设置条形码
     */
    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    /**
     * 返回商品名称
     * @return 商品名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置商品名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    /**
     * 返回商品价格,单位/元
     * @return 商品价格,单位/元
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置商品价格,单位/元
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 返回市场价,单位/元
     * @return 市场价,单位/元
     */
    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    /**
     * 设置市场价,单位/元
     */
    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    /**
     * 返回促销价,单位/元
     * @return 促销价,单位/元
     */
    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    /**
     * 设置促销价,单位/元
     */
    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    /**
     * 返回报关单价,单位/元
     * @return 报关单价,单位/元
     */
    public BigDecimal getCustomsPrice() {
        return customsPrice;
    }

    /**
     * 设置报关单价,单位/元
     */
    public void setCustomsPrice(BigDecimal customsPrice) {
        this.customsPrice = customsPrice;
    }

    /**
     * 返回成交单价,单位/元
     * @return 成交单价,单位/元
     */
    public BigDecimal getTransactionPrice() {
        return transactionPrice;
    }

    /**
     * 设置成交单价,单位/元
     */
    public void setTransactionPrice(BigDecimal transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    /**
     * 返回购买数量
     * @return 购买数量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置购买数量
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 返回明细商品发货数量
     * @return 明细商品发货数量
     */
    public Integer getSendNum() {
        return sendNum;
    }

    /**
     * 设置明细商品发货数量
     */
    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    /**
     * 返回SKU的值
     * @return SKU的值
     */
    public String getSkuPropertiesName() {
        return skuPropertiesName;
    }

    /**
     * 设置SKU的值
     */
    public void setSkuPropertiesName(String skuPropertiesName) {
        this.skuPropertiesName = skuPropertiesName == null ? null : skuPropertiesName.trim();
    }

    /**
     * 返回最近退款ID
     * @return 最近退款ID
     */
    public String getRefundId() {
        return refundId;
    }

    /**
     * 设置最近退款ID
     */
    public void setRefundId(String refundId) {
        this.refundId = refundId == null ? null : refundId.trim();
    }

    /**
     * 返回是否超卖
     * @return 是否超卖
     */
    public String getIsOversold() {
        return isOversold;
    }

    /**
     * 设置是否超卖
     */
    public void setIsOversold(String isOversold) {
        this.isOversold = isOversold;
    }

    /**
     * 返回运送方式
     * @return 运送方式
     */
    public String getShippingType() {
        return shippingType;
    }

    /**
     * 设置运送方式
     */
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType == null ? null : shippingType.trim();
    }

    /**
     * 返回捆绑的子订单号
     * @return 捆绑的子订单号
     */
    public String getBindOid() {
        return bindOid;
    }

    /**
     * 设置捆绑的子订单号
     */
    public void setBindOid(String bindOid) {
        this.bindOid = bindOid == null ? null : bindOid.trim();
    }

    /**
     * 返回子订单发货的快递公司
     * @return 子订单发货的快递公司
     */
    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    /**
     * 设置子订单发货的快递公司
     */
    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany == null ? null : logisticsCompany.trim();
    }

    /**
     * 返回子订单所在包裹的运单号
     * @return 子订单所在包裹的运单号
     */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /**
     * 设置子订单所在包裹的运单号
     */
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo == null ? null : invoiceNo.trim();
    }

    /**
     * 返回运费分摊,单位/元
     * @return 运费分摊,单位/元
     */
    public BigDecimal getPostDiscount() {
        return postDiscount;
    }

    /**
     * 设置运费分摊,单位/元
     */
    public void setPostDiscount(BigDecimal postDiscount) {
        this.postDiscount = postDiscount;
    }

    /**
     * 返回促销优惠分摊,单位/元
     * @return 促销优惠分摊,单位/元
     */
    public BigDecimal getDiscountPromotion() {
        return discountPromotion;
    }

    /**
     * 设置促销优惠分摊,单位/元
     */
    public void setDiscountPromotion(BigDecimal discountPromotion) {
        this.discountPromotion = discountPromotion;
    }

    /**
     * 返回店铺优惠卷分摊金额,单位/元
     * @return 店铺优惠卷分摊金额,单位/元
     */
    public BigDecimal getDiscountCouponShop() {
        return discountCouponShop;
    }

    /**
     * 设置店铺优惠卷分摊金额,单位/元
     */
    public void setDiscountCouponShop(BigDecimal discountCouponShop) {
        this.discountCouponShop = discountCouponShop;
    }

    /**
     * 返回平台优惠卷优惠分摊,单位/元
     * @return 平台优惠卷优惠分摊,单位/元
     */
    public BigDecimal getDiscountCouponPlatform() {
        return discountCouponPlatform;
    }

    /**
     * 设置平台优惠卷优惠分摊,单位/元
     */
    public void setDiscountCouponPlatform(BigDecimal discountCouponPlatform) {
        this.discountCouponPlatform = discountCouponPlatform;
    }

    /**
     * 返回子订单级订单优惠金额,单位/元
     * @return 子订单级订单优惠金额,单位/元
     */
    public BigDecimal getDiscountFee() {
        return discountFee;
    }

    /**
     * 设置子订单级订单优惠金额,单位/元
     */
    public void setDiscountFee(BigDecimal discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 返回应付金额,单位/元
     * @return 应付金额,单位/元
     */
    public BigDecimal getTotalFee() {
        return totalFee;
    }

    /**
     * 设置应付金额,单位/元
     */
    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * 返回实付金额,单位/元
     * @return 实付金额,单位/元
     */
    public BigDecimal getPayment() {
        return payment;
    }

    /**
     * 设置实付金额,单位/元
     */
    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    /**
     * 返回商品重量,单位/克
     * @return 商品重量,单位/克
     */
    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    /**
     * 设置商品重量,单位/克
     */
    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * 返回手工调整金额,单位/元
     * @return 手工调整金额,单位/元
     */
    public BigDecimal getAdjustFee() {
        return adjustFee;
    }

    /**
     * 设置手工调整金额,单位/元
     */
    public void setAdjustFee(BigDecimal adjustFee) {
        this.adjustFee = adjustFee;
    }

    /**
     * 返回订单状态:1-待出库 2-部分出库 3-全部出库
     * @return 订单状态:1-待出库 2-部分出库 3-全部出库
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置订单状态:1-待出库 2-部分出库 3-全部出库
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 返回售后状态
     * @return 售后状态
     */
    public String getAfterSalesStatus() {
        return afterSalesStatus;
    }

    /**
     * 设置售后状态
     */
    public void setAfterSalesStatus(String afterSalesStatus) {
        this.afterSalesStatus = afterSalesStatus == null ? null : afterSalesStatus.trim();
    }

    /**
     * 返回订单投诉状态
     * @return 订单投诉状态
     */
    public String getComplaintsStatus() {
        return complaintsStatus;
    }

    /**
     * 设置订单投诉状态
     */
    public void setComplaintsStatus(String complaintsStatus) {
        this.complaintsStatus = complaintsStatus == null ? null : complaintsStatus.trim();
    }

    /**
     * 返回退款金额,单位/元
     * @return 退款金额,单位/元
     */
    public BigDecimal getRefundFee() {
        return refundFee;
    }

    /**
     * 设置退款金额,单位/元
     */
    public void setRefundFee(BigDecimal refundFee) {
        this.refundFee = refundFee;
    }

    /**
     * 返回商家三级类目签约佣金比例
     * @return 商家三级类目签约佣金比例
     */
    public BigDecimal getCatServiceRate() {
        return catServiceRate;
    }

    /**
     * 设置商家三级类目签约佣金比例
     */
    public void setCatServiceRate(BigDecimal catServiceRate) {
        this.catServiceRate = catServiceRate;
    }

    /**
     * 返回商品图片绝对路径
     * @return 商品图片绝对路径
     */
    public String getPicPath() {
        return picPath;
    }

    /**
     * 设置商品图片绝对路径
     */
    public void setPicPath(String picPath) {
        this.picPath = picPath == null ? null : picPath.trim();
    }

    /**
     * 返回商家外部编码
     * @return 商家外部编码
     */
    public String getOuterIid() {
        return outerIid;
    }

    /**
     * 设置商家外部编码
     */
    public void setOuterIid(String outerIid) {
        this.outerIid = outerIid == null ? null : outerIid.trim();
    }

    /**
     * 返回商家外部sku码
     * @return 商家外部sku码
     */
    public String getOuterSkuId() {
        return outerSkuId;
    }

    /**
     * 设置商家外部sku码
     */
    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId == null ? null : outerSkuId.trim();
    }

    /**
     * 返回是否支持下单减库存
     * @return 是否支持下单减库存
     */
    public String getSubStock() {
        return subStock;
    }

    /**
     * 设置是否支持下单减库存
     */
    public void setSubStock(String subStock) {
        this.subStock = subStock;
    }

    /**
     * 返回配送模板id
     * @return 配送模板id
     */
    public Integer getDlytmplId() {
        return dlytmplId;
    }

    /**
     * 设置配送模板id
     */
    public void setDlytmplId(Integer dlytmplId) {
        this.dlytmplId = dlytmplId;
    }

    /**
     * 返回供应商名称
     * @return 供应商名称
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 设置供应商名称
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    /**
     * 返回商品税费,单位/元
     * @return 商品税费,单位/元
     */
    public BigDecimal getPriceTax() {
        return priceTax;
    }

    /**
     * 设置商品税费,单位/元
     */
    public void setPriceTax(BigDecimal priceTax) {
        this.priceTax = priceTax;
    }

    /**
     * 返回订单应用促销标签
     * @return 订单应用促销标签
     */
    public String getPromotionTags() {
        return promotionTags;
    }

    /**
     * 设置订单应用促销标签
     */
    public void setPromotionTags(String promotionTags) {
        this.promotionTags = promotionTags == null ? null : promotionTags.trim();
    }

    /**
     * 返回订单商品类型
     * @return 订单商品类型
     */
    public String getObjType() {
        return objType;
    }

    /**
     * 设置订单商品类型
     */
    public void setObjType(String objType) {
        this.objType = objType == null ? null : objType.trim();
    }

    /**
     * 返回订单类型 0-普通 1-零元购 2-分期购 3-团购
     * @return 订单类型 0-普通 1-零元购 2-分期购 3-团购
     */
    public String getType() {
        return type;
    }

    /**
     * 设置订单类型 0-普通 1-零元购 2-分期购 3-团购
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 返回税率
     * @return 税率
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * 设置税率
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * 返回订单冗余参数
     * @return 订单冗余参数
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置订单冗余参数
     */
    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
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
     * 返回超时确认时间
     * @return 超时确认时间
     */
    public Date getTimeoutActionTime() {
        return timeoutActionTime;
    }

    /**
     * 设置超时确认时间
     */
    public void setTimeoutActionTime(Date timeoutActionTime) {
        this.timeoutActionTime = timeoutActionTime;
    }

    /**
     * 返回关闭时间
     * @return 关闭时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置关闭时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 返回商品规格描述
     * @return 商品规格描述
     */
    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    /**
     * 设置商品规格描述
     */
    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo == null ? null : specNatureInfo.trim();
    }

    public String getSupplierOrderStatus() {
        return supplierOrderStatus;
    }

    public void setSupplierOrderStatus(String supplierOrderStatus) {
        this.supplierOrderStatus = supplierOrderStatus;
    }

    public String getOldSupplierOrderStatus() {
        return oldSupplierOrderStatus;
    }

    public void setOldSupplierOrderStatus(String oldSupplierOrderStatus) {
        this.oldSupplierOrderStatus = oldSupplierOrderStatus;
    }

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getDeliverNum() {
        return deliverNum;
    }

    public void setDeliverNum(Integer deliverNum) {
        this.deliverNum = deliverNum;
    }

    public List<DeliverPackageForm> getDeliverPackageFormList() {
        return deliverPackageFormList;
    }

    public void setDeliverPackageFormList(List<DeliverPackageForm> deliverPackageFormList) {
        this.deliverPackageFormList = deliverPackageFormList;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    public String getOrderItemCode() {
        return orderItemCode;
    }

    public void setOrderItemCode(String orderItemCode) {
        this.orderItemCode = orderItemCode;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getScmShopOrderCode() {
        return scmShopOrderCode;
    }

    public void setScmShopOrderCode(String scmShopOrderCode) {
        this.scmShopOrderCode = scmShopOrderCode;
    }

    public String getTradeMemo() {
        return tradeMemo;
    }

    public void setTradeMemo(String tradeMemo) {
        this.tradeMemo = tradeMemo;
    }

    public Integer getIsStoreOrder() {
        return isStoreOrder;
    }

    public void setIsStoreOrder(Integer isStoreOrder) {
        this.isStoreOrder = isStoreOrder;
    }
}