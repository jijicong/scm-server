package org.trc.domain.stock;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "jd_stock_out_detail")
public class JdStockOutDetail implements Serializable {

    private static final long serialVersionUID = -1582067265815285113L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 仓库code
     */
    @Column(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 库存类型1.正品2.残品
     */
    @Column(name = "stock_type")
    private String stockType;

    /**
     * 出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     */
    @Column(name = "operation_type")
    private String operationType;

    /**
     * 出库单编号
     */
    @Column(name = "outbound_order_code")
    private String outboundOrderCode;

    /**
     * 仓库反馈出库单号
     */
    @Column(name = "warehouse_outbound_order_code")
    private String warehouseOutboundOrderCode;

    /**
     * 平台订单号
     */
    @Column(name = "platform_order_code")
    private String platformOrderCode;

    /**
     * 销售渠道订单号
     */
    @Column(name = "sell_channel_code")
    private String sellChannelCode;

    /**
     * 销售渠道
     */
    @Column(name = "sell_code")
    private String sellCode;

    /**
     * 商品订单号
     */
    @Column(name = "goods_order_code")
    private String goodsOrderCode;

    /**
     * 渠道编码
     */
    @Column(name = "channel_code")
    private String channelCode;

    /**
     * SKU编号
     */
    @Column(name = "sku_code")
    private String skuCode;

    /**
     * 条形码
     */
    @Column(name = "bar_code")
    private String barCode;

    /**
     * 商品类别1.小泰良品2.非小泰良品
     */
    @Column(name = "goods_type")
    private String goodsType;

    /**
     * 规格
     */
    @Column(name = "spec_info")
    private String specInfo;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 总价
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 销售出库实付总金额（元）
     */
    private BigDecimal payment;

    /**
     * 计划数量
     */
    @Column(name = "planned_quantity")
    private Long plannedQuantity;

    /**
     * 库存变动数量
     */
    private Long quantity;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人手机
     */
    private String mobile;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 快递单号
     */
    @Column(name = "waybill_number")
    private String waybillNumber;

    /**
     * 退供应商出库金额（元）
     */
    @Column(name = "outbound_supplier_amount")
    private BigDecimal outboundSupplierAmount;

    /**
     * 税率
     */
    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    /**
     * 是否删除:0-否,1-是
     */
    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 快递公司
     */
    private String express;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取仓库code
     *
     * @return warehouse_code - 仓库code
     */
    public String getWarehouseCode() {
        return warehouseCode;
    }

    /**
     * 设置仓库code
     *
     * @param warehouseCode 仓库code
     */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    /**
     * 获取库存类型1.正品2.残品
     *
     * @return stock_type - 库存类型1.正品2.残品
     */
    public String getStockType() {
        return stockType;
    }

    /**
     * 设置库存类型1.正品2.残品
     *
     * @param stockType 库存类型1.正品2.残品
     */
    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    /**
     * 获取出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     *
     * @return operation_type - 出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 设置出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     *
     * @param operationType 出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * 获取出库单编号
     *
     * @return outbound_order_code - 出库单编号
     */
    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    /**
     * 设置出库单编号
     *
     * @param outboundOrderCode 出库单编号
     */
    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    /**
     * 获取仓库反馈出库单号
     *
     * @return warehouse_outbound_order_code - 仓库反馈出库单号
     */
    public String getWarehouseOutboundOrderCode() {
        return warehouseOutboundOrderCode;
    }

    /**
     * 设置仓库反馈出库单号
     *
     * @param warehouseOutboundOrderCode 仓库反馈出库单号
     */
    public void setWarehouseOutboundOrderCode(String warehouseOutboundOrderCode) {
        this.warehouseOutboundOrderCode = warehouseOutboundOrderCode;
    }

    /**
     * 获取平台订单号
     *
     * @return platform_order_code - 平台订单号
     */
    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    /**
     * 设置平台订单号
     *
     * @param platformOrderCode 平台订单号
     */
    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }

    /**
     * 获取销售渠道订单号
     *
     * @return sell_channel_code - 销售渠道订单号
     */
    public String getSellChannelCode() {
        return sellChannelCode;
    }

    /**
     * 设置销售渠道订单号
     *
     * @param sellChannelCode 销售渠道订单号
     */
    public void setSellChannelCode(String sellChannelCode) {
        this.sellChannelCode = sellChannelCode;
    }

    /**
     * 获取销售渠道
     *
     * @return sell_code - 销售渠道
     */
    public String getSellCode() {
        return sellCode;
    }

    /**
     * 设置销售渠道
     *
     * @param sellCode 销售渠道
     */
    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    /**
     * 获取商品订单号
     *
     * @return goods_order_code - 商品订单号
     */
    public String getGoodsOrderCode() {
        return goodsOrderCode;
    }

    /**
     * 设置商品订单号
     *
     * @param goodsOrderCode 商品订单号
     */
    public void setGoodsOrderCode(String goodsOrderCode) {
        this.goodsOrderCode = goodsOrderCode;
    }

    /**
     * 获取渠道编码
     *
     * @return channel_code - 渠道编码
     */
    public String getChannelCode() {
        return channelCode;
    }

    /**
     * 设置渠道编码
     *
     * @param channelCode 渠道编码
     */
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    /**
     * 获取SKU编号
     *
     * @return sku_code - SKU编号
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * 设置SKU编号
     *
     * @param skuCode SKU编号
     */
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    /**
     * 获取条形码
     *
     * @return bar_code - 条形码
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * 设置条形码
     *
     * @param barCode 条形码
     */
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    /**
     * 获取商品类别1.小泰良品2.非小泰良品
     *
     * @return goods_type - 商品类别1.小泰良品2.非小泰良品
     */
    public String getGoodsType() {
        return goodsType;
    }

    /**
     * 设置商品类别1.小泰良品2.非小泰良品
     *
     * @param goodsType 商品类别1.小泰良品2.非小泰良品
     */
    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    /**
     * 获取规格
     *
     * @return spec_info - 规格
     */
    public String getSpecInfo() {
        return specInfo;
    }

    /**
     * 设置规格
     *
     * @param specInfo 规格
     */
    public void setSpecInfo(String specInfo) {
        this.specInfo = specInfo;
    }

    /**
     * 获取单价
     *
     * @return price - 单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置单价
     *
     * @param price 单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取总价
     *
     * @return total_amount - 总价
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置总价
     *
     * @param totalAmount 总价
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取销售出库实付总金额（元）
     *
     * @return payment - 销售出库实付总金额（元）
     */
    public BigDecimal getPayment() {
        return payment;
    }

    /**
     * 设置销售出库实付总金额（元）
     *
     * @param payment 销售出库实付总金额（元）
     */
    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    /**
     * 获取计划数量
     *
     * @return planned_quantity - 计划数量
     */
    public Long getPlannedQuantity() {
        return plannedQuantity;
    }

    /**
     * 设置计划数量
     *
     * @param plannedQuantity 计划数量
     */
    public void setPlannedQuantity(Long plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    /**
     * 获取库存变动数量
     *
     * @return quantity - 库存变动数量
     */
    public Long getQuantity() {
        return quantity;
    }

    /**
     * 设置库存变动数量
     *
     * @param quantity 库存变动数量
     */
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取收货人
     *
     * @return receiver - 收货人
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * 设置收货人
     *
     * @param receiver 收货人
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取收货人手机
     *
     * @return mobile - 收货人手机
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置收货人手机
     *
     * @param mobile 收货人手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取收货地址
     *
     * @return address - 收货地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置收货地址
     *
     * @param address 收货地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取快递单号
     *
     * @return waybill_number - 快递单号
     */
    public String getWaybillNumber() {
        return waybillNumber;
    }

    /**
     * 设置快递单号
     *
     * @param waybillNumber 快递单号
     */
    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    /**
     * 获取退供应商出库金额（元）
     *
     * @return outbound_supplier_amount - 退供应商出库金额（元）
     */
    public BigDecimal getOutboundSupplierAmount() {
        return outboundSupplierAmount;
    }

    /**
     * 设置退供应商出库金额（元）
     *
     * @param outboundSupplierAmount 退供应商出库金额（元）
     */
    public void setOutboundSupplierAmount(BigDecimal outboundSupplierAmount) {
        this.outboundSupplierAmount = outboundSupplierAmount;
    }

    /**
     * 获取税率
     *
     * @return tax_rate - 税率
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * 设置税率
     *
     * @param taxRate 税率
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * 获取是否删除:0-否,1-是
     *
     * @return is_deleted - 是否删除:0-否,1-是
     */
    public String getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除:0-否,1-是
     *
     * @param isDeleted 是否删除:0-否,1-是
     */
    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取快递公司
     *
     * @return express - 快递公司
     */
    public String getExpress() {
        return express;
    }

    /**
     * 设置快递公司
     *
     * @param express 快递公司
     */
    public void setExpress(String express) {
        this.express = express;
    }
}