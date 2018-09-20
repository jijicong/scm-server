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

@Table(name = "report_entry_detail")
@Setter
@Getter
public class ReportEntryDetail extends ReportBase implements Serializable {

    private static final long serialVersionUID = -1391712478270728374L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 供应商code
     */
    @ApiModelProperty(value = "供应商code")
    @Column(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 入库时间
     */
    @Column(name = "entry_time")
    @ApiModelProperty(value = "入库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date entryTime;

    /**
     * 入库时间格式化
     */
    @Transient
    private String entryTimeValue;

    /**
     * 入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     */
    @Column(name = "operation_type")
    @ApiModelProperty(value = "入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库")
    private String operationType;

    /**
     * 供应商编号
     */
    @Column(name = "supplier_code")
    @ApiModelProperty(value = "供应商编号")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Column(name = "supplier_name")
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 入库单编号
     */
    @Column(name = "order_code")
    @ApiModelProperty(value = "入库单编号")
    private String orderCode;

    /**
     * 仓库反馈入库单编号
     */
    @Column(name = "warehouse_order_code")
    @ApiModelProperty(value = "仓库反馈入库单编号")
    private String warehouseOrderCode;

    @Column(name = "sku_code")
    @ApiModelProperty(value = "skucode")
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
     * 计划入库数量
     */
    @Column(name = "entry_quantity")
    @ApiModelProperty(value = "计划入库数量")
    private Long entryQuantity;

    /**
     * 含税采购单价（元）
     */
    @ApiModelProperty(value = "含税采购单价（元）")
    private BigDecimal price;

    /**
     * 含税采购总金额（元）
     */
    @Column(name = "total_price")
    @ApiModelProperty(value = "含税采购总金额（元）")
    private BigDecimal totalPrice;

    /**
     * 实际入库数量
     */
    @Column(name = "real_quantity")
    @ApiModelProperty(value = "实际入库数量")
    private Long realQuantity;

    /**
     * 正品数量
     */
    @Column(name = "normal_quantity")
    @ApiModelProperty(value = "正品数量")
    private Long normalQuantity;

    /**
     * 残品数量
     */
    @Column(name = "defective_quantity")
    @ApiModelProperty(value = "实际入库数量")
    private Long defectiveQuantity;

    /**
     * 未入库数量
     */
    @Column(name = "residual_quantity")
    @ApiModelProperty(value = "未入库数量")
    private Long residualQuantity;

    /**
     * 系统备注
     */
    @ApiModelProperty(value = "系统备注")
    private String remark;

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

    @Transient
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

}