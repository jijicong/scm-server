package org.trc.domain.report;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;
import org.trc.custom.CustomLongSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "report_inventory")
public class ReportInventory extends ReportBase implements Serializable {

    private static final long serialVersionUID = 3936501106124320116L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 供应商code
     */
    @Column(name = "warehouse_code")
    @ApiModelProperty(value = "供应商code")
    private String warehouseCode;

    @Column(name = "sku_code")
    @ApiModelProperty(value = "skuCode")
    private String skuCode;

    /**
     * 条形码
     */
    @Column(name = "bar_code")
    @ApiModelProperty(value = "条形码")
    private String barCode;

    /**
     * 所属类目
     */
    @Column(name = "category_id")
    @ApiModelProperty(value = "所属类目")
    private String categoryId;

    /**
     * 规格
     */
    @Column(name = "spec_info")
    @ApiModelProperty(value = "规格")
    private String specInfo;

    /**
     * 期初数量
     */
    @Column(name = "initial_quantity")
    @ApiModelProperty(value = "期初数量")
    @JsonSerialize(using = CustomLongSerializer.class)
    private Long initialQuantity;

    /**
     * 销售出库数量
     */
    @Column(name = "outbound_quantity")
    @ApiModelProperty(value = "销售出库数量")
    private Long outboundQuantity;

    /**
     * 销售出库实付总金额（元）
     */
    @Column(name = "outbound_total_amount")
    @ApiModelProperty(value = "销售出库实付总金额（元）")
    private BigDecimal outboundTotalAmount;

    /**
     * 退货入库数量
     */
    @Column(name = "sales_return_quantity")
    @ApiModelProperty(value = "退货入库数量")
    private Long salesReturnQuantity;

    /**
     * 采购入库数量
     */
    @Column(name = "purchase_quantity")
    @ApiModelProperty(value = "采购入库数量")
    private Long purchaseQuantity;

    /**
     * 含税采购总金额（元）
     */
    @Column(name = "purchase_total_amount")
    @ApiModelProperty(value = "含税采购总金额（元）")
    private BigDecimal purchaseTotalAmount;

    /**
     * 退供应商出库数量
     */
    @Column(name = "supplier_return_outbound_quantity")
    @ApiModelProperty(value = "退供应商出库数量")
    private Long supplierReturnOutboundQuantity;

    /**
     * 退供应商出库金额（元）
     */
    @Column(name = "supplider_return_total_amount")
    @ApiModelProperty(value = "退供应商出库金额（元）")
    private BigDecimal suppliderReturnTotalAmount;

    /**
     * 调拨入库数量
     */
    @Column(name = "allocate_in_quantity")
    @ApiModelProperty(value = "调拨入库数量")
    private Long allocateInQuantity;

    /**
     * 调拨出库数量
     */
    @Column(name = "allocate_out_quantity")
    @ApiModelProperty(value = "调拨出库数量")
    private Long allocateOutQuantity;

    /**
     * 盘盈入库数量
     */
    @Column(name = "inventory_profit_quantity")
    @ApiModelProperty(value = "盘盈入库数量")
    private Long inventoryProfitQuantity;

    /**
     * 盘亏出库数量
     */
    @Column(name = "inventory_losses_quantity")
    @ApiModelProperty(value = "盘亏出库数量")
    private Long inventoryLossesQuantity;

    /**
     * 残品转正品入库数量
     */
    @Column(name = "defective_to_normal")
    @ApiModelProperty(value = "残品转正品数量")
    private Long defectiveToNormal;

    /**
     * 正品转残品出库数量
     */
    @Column(name = "normal_to_defective")
    @ApiModelProperty(value = "正品转残品数量")
    private Long normalToDefective;

    /**
     * 其他入库
     */
    @Column(name = "other_in")
    @ApiModelProperty(value = "其他入库")
    private Long otherIn;

    /**
     * 其他出库
     */
    @Column(name = "other_out")
    @ApiModelProperty(value = "其他出库")
    private Long otherOut;

    /**
     * 本期入库总数量
     */
    @Column(name = "entry_total_quantity")
    @ApiModelProperty(value = "本期入库总数量")
    private Long entryTotalQuantity;

    /**
     * 本期出库总数量
     */
    @Column(name = "outbound_total_quantity")
    @ApiModelProperty(value = "本期出库总数量")
    private Long outboundTotalQuantity;

    /**
     * 期末结存数量
     */
    @Column(name = "balance_total_quantity")
    @ApiModelProperty(value = "期末结存数量")
    @JsonSerialize(using = CustomLongSerializer.class)
    private Long balanceTotalQuantity;

    /**
     * 期数(yyyy-MM-dd)
     */
    @ApiModelProperty(value = "期数(yyyy-MM-dd)")
    private String periods;

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

    @Transient
    @ApiModelProperty(value = "所属类目")
    private String categoryName;

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * 获取所属类目
     *
     * @return category_id - 所属类目
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * 设置所属类目
     *
     * @param categoryId 所属类目
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
     * 获取期初数量
     *
     * @return initial_quantity - 期初数量
     */
    public Long getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * 设置期初数量
     *
     * @param initialQuantity 期初数量
     */
    public void setInitialQuantity(Long initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * 获取销售出库数量
     *
     * @return outbound_quantity - 销售出库数量
     */
    public Long getOutboundQuantity() {
        return outboundQuantity;
    }

    /**
     * 设置销售出库数量
     *
     * @param outboundQuantity 销售出库数量
     */
    public void setOutboundQuantity(Long outboundQuantity) {
        this.outboundQuantity = outboundQuantity;
    }

    /**
     * 获取销售出库实付总金额（元）
     *
     * @return outbound_total_amount - 销售出库实付总金额（元）
     */
    public BigDecimal getOutboundTotalAmount() {
        return outboundTotalAmount;
    }

    /**
     * 设置销售出库实付总金额（元）
     *
     * @param outboundTotalAmount 销售出库实付总金额（元）
     */
    public void setOutboundTotalAmount(BigDecimal outboundTotalAmount) {
        this.outboundTotalAmount = outboundTotalAmount;
    }

    /**
     * 获取退货入库数量
     *
     * @return sales_return_quantity - 退货入库数量
     */
    public Long getSalesReturnQuantity() {
        return salesReturnQuantity;
    }

    /**
     * 设置退货入库数量
     *
     * @param salesReturnQuantity 退货入库数量
     */
    public void setSalesReturnQuantity(Long salesReturnQuantity) {
        this.salesReturnQuantity = salesReturnQuantity;
    }

    /**
     * 获取采购入库数量
     *
     * @return purchase_quantity - 采购入库数量
     */
    public Long getPurchaseQuantity() {
        return purchaseQuantity;
    }

    /**
     * 设置采购入库数量
     *
     * @param purchaseQuantity 采购入库数量
     */
    public void setPurchaseQuantity(Long purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    /**
     * 获取含税采购总金额（元）
     *
     * @return purchase_total_amount - 含税采购总金额（元）
     */
    public BigDecimal getPurchaseTotalAmount() {
        return purchaseTotalAmount;
    }

    /**
     * 设置含税采购总金额（元）
     *
     * @param purchaseTotalAmount 含税采购总金额（元）
     */
    public void setPurchaseTotalAmount(BigDecimal purchaseTotalAmount) {
        this.purchaseTotalAmount = purchaseTotalAmount;
    }

    /**
     * 获取退供应商出库数量
     *
     * @return supplier_return_outbound_quantity - 退供应商出库数量
     */
    public Long getSupplierReturnOutboundQuantity() {
        return supplierReturnOutboundQuantity;
    }

    /**
     * 设置退供应商出库数量
     *
     * @param supplierReturnOutboundQuantity 退供应商出库数量
     */
    public void setSupplierReturnOutboundQuantity(Long supplierReturnOutboundQuantity) {
        this.supplierReturnOutboundQuantity = supplierReturnOutboundQuantity;
    }

    /**
     * 获取退供应商出库金额（元）
     *
     * @return supplider_return_total_amount - 退供应商出库金额（元）
     */
    public BigDecimal getSuppliderReturnTotalAmount() {
        return suppliderReturnTotalAmount;
    }

    /**
     * 设置退供应商出库金额（元）
     *
     * @param suppliderReturnTotalAmount 退供应商出库金额（元）
     */
    public void setSuppliderReturnTotalAmount(BigDecimal suppliderReturnTotalAmount) {
        this.suppliderReturnTotalAmount = suppliderReturnTotalAmount;
    }

    /**
     * 获取调拨入库数量
     *
     * @return allocate_in_quantity - 调拨入库数量
     */
    public Long getAllocateInQuantity() {
        return allocateInQuantity;
    }

    /**
     * 设置调拨入库数量
     *
     * @param allocateInQuantity 调拨入库数量
     */
    public void setAllocateInQuantity(Long allocateInQuantity) {
        this.allocateInQuantity = allocateInQuantity;
    }

    /**
     * 获取调拨出库数量
     *
     * @return allocate_out_quantity - 调拨出库数量
     */
    public Long getAllocateOutQuantity() {
        return allocateOutQuantity;
    }

    /**
     * 设置调拨出库数量
     *
     * @param allocateOutQuantity 调拨出库数量
     */
    public void setAllocateOutQuantity(Long allocateOutQuantity) {
        this.allocateOutQuantity = allocateOutQuantity;
    }

    /**
     * 获取盘盈入库数量
     *
     * @return inventory_profit_quantity - 盘盈入库数量
     */
    public Long getInventoryProfitQuantity() {
        return inventoryProfitQuantity;
    }

    /**
     * 设置盘盈入库数量
     *
     * @param inventoryProfitQuantity 盘盈入库数量
     */
    public void setInventoryProfitQuantity(Long inventoryProfitQuantity) {
        this.inventoryProfitQuantity = inventoryProfitQuantity;
    }

    /**
     * 获取盘亏出库数量
     *
     * @return inventory_losses_quantity - 盘亏出库数量
     */
    public Long getInventoryLossesQuantity() {
        return inventoryLossesQuantity;
    }

    /**
     * 设置盘亏出库数量
     *
     * @param inventoryLossesQuantity 盘亏出库数量
     */
    public void setInventoryLossesQuantity(Long inventoryLossesQuantity) {
        this.inventoryLossesQuantity = inventoryLossesQuantity;
    }

    /**
     * 获取本期入库总数量
     *
     * @return entry_total_quantity - 本期入库总数量
     */
    public Long getEntryTotalQuantity() {
        return entryTotalQuantity;
    }

    /**
     * 设置本期入库总数量
     *
     * @param entryTotalQuantity 本期入库总数量
     */
    public void setEntryTotalQuantity(Long entryTotalQuantity) {
        this.entryTotalQuantity = entryTotalQuantity;
    }

    /**
     * 获取本期出库总数量
     *
     * @return outbound_total_quantity - 本期出库总数量
     */
    public Long getOutboundTotalQuantity() {
        return outboundTotalQuantity;
    }

    /**
     * 设置本期出库总数量
     *
     * @param outboundTotalQuantity 本期出库总数量
     */
    public void setOutboundTotalQuantity(Long outboundTotalQuantity) {
        this.outboundTotalQuantity = outboundTotalQuantity;
    }

    /**
     * 获取期末结存数量
     *
     * @return balance_total_quantity - 期末结存数量
     */
    public Long getBalanceTotalQuantity() {
        return balanceTotalQuantity;
    }

    /**
     * 设置期末结存数量
     *
     * @param balanceTotalQuantity 期末结存数量
     */
    public void setBalanceTotalQuantity(Long balanceTotalQuantity) {
        this.balanceTotalQuantity = balanceTotalQuantity;
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

    public Long getDefectiveToNormal() {
        return defectiveToNormal;
    }

    public void setDefectiveToNormal(Long defectiveToNormal) {
        this.defectiveToNormal = defectiveToNormal;
    }

    public Long getNormalToDefective() {
        return normalToDefective;
    }

    public void setNormalToDefective(Long normalToDefective) {
        this.normalToDefective = normalToDefective;
    }

    public Long getOtherIn() {
        return otherIn;
    }

    public void setOtherIn(Long otherIn) {
        this.otherIn = otherIn;
    }

    public Long getOtherOut() {
        return otherOut;
    }

    public void setOtherOut(Long otherOut) {
        this.otherOut = otherOut;
    }
}