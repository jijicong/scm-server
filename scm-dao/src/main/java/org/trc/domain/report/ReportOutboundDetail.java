package org.trc.domain.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
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
     * 出库时间格式化
     */
    @Transient
    private String outboundTimeValue;

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

}