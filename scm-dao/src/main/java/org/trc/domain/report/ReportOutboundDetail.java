package org.trc.domain.report;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "report_outbound_detail")
public class ReportOutboundDetail extends ReportBase implements Serializable {

    private static final long serialVersionUID = 6545215501803784916L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 仓库code
     */
    @Column(name = "warehouse_code")
    @ApiModelProperty(value = "仓库code")
    private String warehouseCode;

    /**
     * 出库时间
     */
    @Column(name = "outbound_time")
    @ApiModelProperty(value = "出库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date outboundTime;

    /**
     * 出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库
     */
    @Column(name = "operation_type")
    @ApiModelProperty(value = "出库类型1.销售出库 2.调拨出库 3.退货出库 4.盘亏出库")
    private String operationType;

    /**
     * 出库单编号
     */
    @Column(name = "outbound_order_code")
    @ApiModelProperty(value = "出库单编号")
    private String outboundOrderCode;

    /**
     * 仓库反馈出库单号
     */
    @Column(name = "warehouse_outbound_order_code")
    @ApiModelProperty(value = "仓库反馈出库单号")
    private String warehouseOutboundOrderCode;

    /**
     * 平台订单号
     */
    @Column(name = "platform_order_code")
    @ApiModelProperty(value = "平台订单号")
    private String platformOrderCode;

    /**
     * 销售渠道订单号
     */
    @Column(name = "sell_channel_code")
    @ApiModelProperty(value = "销售渠道订单号")
    private String sellChannelCode;

    /**
     * 销售渠道
     */
    @Column(name = "sell_code")
    @ApiModelProperty(value = "销售渠道")
    private String sellCode;

    /**
     * 商品订单号
     */
    @Column(name = "goods_order_code")
    @ApiModelProperty(value = "商品订单号")
    private String goodsOrderCode;

    /**
     * 渠道编码
     */
    @Column(name = "channel_code")
    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    /**
     * SKU编号
     */
    @Column(name = "sku_code")
    @ApiModelProperty(value = "SKU编号")
    private String skuCode;

    /**
     * 条形码
     */
    @Column(name = "bar_code")
    @ApiModelProperty(value = "条形码")
    private String barCode;

    /**
     * 规格
     */
    @Column(name = "spec_info")
    @ApiModelProperty(value = "规格")
    private String specInfo;

    /**
     * 计划出库数量
     */
    @Column(name = "outbound_quantity")
    @ApiModelProperty(value = "计划出库数量")
    private Long outboundQuantity;

    /**
     * 销售单价
     */
    @Column(name = "sales_price")
    @ApiModelProperty(value = "销售单价")
    private BigDecimal salesPrice;

    /**
     * 销售总价
     */
    @Column(name = "sales_total_amount")
    @ApiModelProperty(value = "销售总价")
    private BigDecimal salesTotalAmount;

    /**
     * 销售出库实付总金额（元）
     */
    @ApiModelProperty(value = "销售出库实付总金额（元）")
    private BigDecimal payment;

    /**
     * 实际出库数量
     */
    @Column(name = "real_quantity")
    @ApiModelProperty(value = "实际出库数量")
    private Long realQuantity;

    /**
     * 未出库数量
     */
    @Column(name = "residual_quantity")
    @ApiModelProperty(value = "未出库数量")
    private Long residualQuantity;

    /**
     * 收货人
     */
    @ApiModelProperty(value = "收货人")
    private String receiver;

    /**
     * 收货人手机
     */
    @ApiModelProperty(value = "收货人手机")
    private String mobile;

    /**
     * 收货地址
     */
    @ApiModelProperty(value = "收货地址")
    private String address;

    /**
     * 快递单号
     */
    @Column(name = "waybill_number")
    @ApiModelProperty(value = "快递单号")
    private String waybillNumber;

    /**
     * 退供应商出库金额（元）
     */
    @Column(name = "outbound_supplier_amount")
    @ApiModelProperty(value = "退供应商出库金额（元）")
    private BigDecimal outboundSupplierAmount;

    /**
     * 是否删除:0-否,1-是
     */
    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "create_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @Column(name = "update_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    @Transient
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    /**
     * 快递公司
     */
    @ApiModelProperty(value = "快递公司")
    private String express;

    @Transient
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @Transient
    @ApiModelProperty(value = "销售渠道名称")

    private String sellName;

    public String getSellChannelCode() {
        return sellChannelCode;
    }

    public void setSellChannelCode(String sellChannelCode) {
        this.sellChannelCode = sellChannelCode;
    }

    public String getSellName() {
        return sellName;
    }

    public void setSellName(String sellName) {
        this.sellName = sellName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

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
     * 获取出库时间
     *
     * @return outbound_time - 出库时间
     */
    public Date getOutboundTime() {
        return outboundTime;
    }

    /**
     * 设置出库时间
     *
     * @param outboundTime 出库时间
     */
    public void setOutboundTime(Date outboundTime) {
        this.outboundTime = outboundTime;
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
     * @return sell_code - 销售渠道订单号
     */
    public String getSellCode() {
        return sellCode;
    }

    /**
     * 设置销售渠道订单号
     *
     * @param sellCode 销售渠道订单号
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
     * 获取计划出库数量
     *
     * @return outbound_quantity - 计划出库数量
     */
    public Long getOutboundQuantity() {
        return outboundQuantity;
    }

    /**
     * 设置计划出库数量
     *
     * @param outboundQuantity 计划出库数量
     */
    public void setOutboundQuantity(Long outboundQuantity) {
        this.outboundQuantity = outboundQuantity;
    }

    /**
     * 获取销售单价
     *
     * @return sales_price - 销售单价
     */
    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    /**
     * 设置销售单价
     *
     * @param salesPrice 销售单价
     */
    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    /**
     * 获取销售总价
     *
     * @return sales_total_amount - 销售总价
     */
    public BigDecimal getSalesTotalAmount() {
        return salesTotalAmount;
    }

    /**
     * 设置销售总价
     *
     * @param salesTotalAmount 销售总价
     */
    public void setSalesTotalAmount(BigDecimal salesTotalAmount) {
        this.salesTotalAmount = salesTotalAmount;
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
     * 获取实际出库数量
     *
     * @return real_quantity - 实际出库数量
     */
    public Long getRealQuantity() {
        return realQuantity;
    }

    /**
     * 设置实际出库数量
     *
     * @param realQuantity 实际出库数量
     */
    public void setRealQuantity(Long realQuantity) {
        this.realQuantity = realQuantity;
    }

    /**
     * 获取未出库数量
     *
     * @return residual_quantity - 未出库数量
     */
    public Long getResidualQuantity() {
        return residualQuantity;
    }

    /**
     * 设置未出库数量
     *
     * @param residualQuantity 未出库数量
     */
    public void setResidualQuantity(Long residualQuantity) {
        this.residualQuantity = residualQuantity;
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