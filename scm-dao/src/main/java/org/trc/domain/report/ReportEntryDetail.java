package org.trc.domain.report;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "report_entry_detail")
public class ReportEntryDetail implements Serializable {

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
     * 库存类型1.正品2.残品
     */
    @Column(name = "stock_type")
    @ApiModelProperty(value = "库存类型1.正品2.残品")
    private String stockType;

    /**
     * 入库时间
     */
    @Column(name = "entry_time")
    @ApiModelProperty(value = "入库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date entryTime;

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
     * 商品类别1.小泰良品2.非小泰良品
     */
    @Column(name = "goods_type")
    @ApiModelProperty(value = "商品类别1.小泰良品2.非小泰良品")
    private String goodsType;

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
     * 获取供应商code
     *
     * @return warehouse_code - 供应商code
     */
    public String getWarehouseCode() {
        return warehouseCode;
    }

    /**
     * 设置供应商code
     *
     * @param warehouseCode 供应商code
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
     * 获取入库时间
     *
     * @return entry_time - 入库时间
     */
    public Date getEntryTime() {
        return entryTime;
    }

    /**
     * 设置入库时间
     *
     * @param entryTime 入库时间
     */
    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    /**
     * 获取入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     *
     * @return operation_type - 入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 设置入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     *
     * @param operationType 入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * 获取供应商编号
     *
     * @return supplier_code - 供应商编号
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * 设置供应商编号
     *
     * @param supplierCode 供应商编号
     */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * 获取供应商名称
     *
     * @return supplier_name - 供应商名称
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 设置供应商名称
     *
     * @param supplierName 供应商名称
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * 获取入库单编号
     *
     * @return order_code - 入库单编号
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * 设置入库单编号
     *
     * @param orderCode 入库单编号
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    /**
     * 获取仓库反馈入库单编号
     *
     * @return warehouse_order_code - 仓库反馈入库单编号
     */
    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    /**
     * 设置仓库反馈入库单编号
     *
     * @param warehouseOrderCode 仓库反馈入库单编号
     */
    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    /**
     * @return sku_code
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * @param skuCode
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
     * 获取计划入库数量
     *
     * @return entry_quantity - 计划入库数量
     */
    public Long getEntryQuantity() {
        return entryQuantity;
    }

    /**
     * 设置计划入库数量
     *
     * @param entryQuantity 计划入库数量
     */
    public void setEntryQuantity(Long entryQuantity) {
        this.entryQuantity = entryQuantity;
    }

    /**
     * 获取含税采购单价（元）
     *
     * @return price - 含税采购单价（元）
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置含税采购单价（元）
     *
     * @param price 含税采购单价（元）
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取含税采购总金额（元）
     *
     * @return total_price - 含税采购总金额（元）
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 设置含税采购总金额（元）
     *
     * @param totalPrice 含税采购总金额（元）
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 获取实际入库数量
     *
     * @return real_quantity - 实际入库数量
     */
    public Long getRealQuantity() {
        return realQuantity;
    }

    /**
     * 设置实际入库数量
     *
     * @param realQuantity 实际入库数量
     */
    public void setRealQuantity(Long realQuantity) {
        this.realQuantity = realQuantity;
    }

    /**
     * 获取未入库数量
     *
     * @return residual_quantity - 未入库数量
     */
    public Long getResidualQuantity() {
        return residualQuantity;
    }

    /**
     * 设置未入库数量
     *
     * @param residualQuantity 未入库数量
     */
    public void setResidualQuantity(Long residualQuantity) {
        this.residualQuantity = residualQuantity;
    }

    /**
     * 获取系统备注
     *
     * @return remark - 系统备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置系统备注
     *
     * @param remark 系统备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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

    public Long getDefectiveQuantity() {
        return defectiveQuantity;
    }

    public void setDefectiveQuantity(Long defectiveQuantity) {
        this.defectiveQuantity = defectiveQuantity;
    }
}