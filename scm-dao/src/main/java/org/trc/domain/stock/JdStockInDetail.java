package org.trc.domain.stock;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "jd_stock_in_detail")
public class JdStockInDetail implements Serializable {

    private static final long serialVersionUID = 369747257336174104L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 供应商code
     */
    @Column(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 库存类型1.正品2.残品
     */
    @Column(name = "stock_type")
    private String stockType;

    /**
     * 入库类型:1.采购入库 2.退货入库 3.调拨入库 4.盘盈入库
     */
    @Column(name = "operation_type")
    private String operationType;

    /**
     * 供应商编号
     */
    @Column(name = "supplier_code")
    private String supplierCode;

    /**
     * 入库单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 仓库反馈入库单编号
     */
    @Column(name = "warehouse_order_code")
    private String warehouseOrderCode;

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
     * 含税采购单价（元）
     */
    private BigDecimal price;

    /**
     * 含税采购总金额（元）
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

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
     * 正品数量
     */
    @Column(name = "normal_quantity")
    private Long normalQuantity;

    /**
     * 残次品数量
     */
    @Column(name = "defective_quantity")
    private Long defectiveQuantity;

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
     * 获取正品数量
     *
     * @return normal_quantity - 正品数量
     */
    public Long getNormalQuantity() {
        return normalQuantity;
    }

    /**
     * 设置正品数量
     *
     * @param normalQuantity 正品数量
     */
    public void setNormalQuantity(Long normalQuantity) {
        this.normalQuantity = normalQuantity;
    }

    /**
     * 获取残次品数量
     *
     * @return defective_quantity - 残次品数量
     */
    public Long getDefectiveQuantity() {
        return defectiveQuantity;
    }

    /**
     * 设置残次品数量
     *
     * @param defectiveQuantity 残次品数量
     */
    public void setDefectiveQuantity(Long defectiveQuantity) {
        this.defectiveQuantity = defectiveQuantity;
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
}