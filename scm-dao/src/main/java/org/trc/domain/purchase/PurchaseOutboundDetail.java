package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "purchase_outbound_detail")
public class PurchaseOutboundDetail extends BaseDO {

    private static final long serialVersionUID = 344433932044368816L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 采购退货单编号
     */
    @ApiModelProperty("采购退货单编号")
    @Column(name = "purchase_outbound_order_code")
    @FormParam("purchaseOutboundOrderCode")
    private String purchaseOutboundOrderCode;

    /**
     * 采购退货单code
     */
    @ApiModelProperty("采购退货单code")
    @Column(name = "outbound_notice_code")
    @FormParam("outboundNoticeCode")
    private String outboundNoticeCode;

    /**
     * 出库状态:0-待通知出库,1-出库仓接收成功,2-出库仓接收失败,3-出库完成,4-出库异常,5-已取消
     */
    @ApiModelProperty("出库状态:0-待通知出库,1-出库仓接收成功,2-出库仓接收失败,3-出库完成,4-出库异常,5-已取消")
    @FormParam("status")
    private String status;

    /**
     * 商品详情出库状态：1-等待出库，2-出库完成，3-出库异常，其他-""
     */
    @ApiModelProperty("商品详情出库状态：1-等待出库，2-出库完成，3-出库异常，其他-\"\"")
    @Column(name = "outbound_status")
    @FormParam("outboundStatus")
    private String outboundStatus;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    @Column(name = "item_name")
    @FormParam("itemName")
    private String itemName;

    @ApiModelProperty("skuCode")
    @Column(name = "sku_code")
    @FormParam("skuCode")
    private String skuCode;

    @ApiModelProperty("skuName")
    @Column(name = "sku_name")
    @FormParam("skuName")
    private String skuName;

    /**
     * 品牌id
     */
    @ApiModelProperty("品牌id")
    @Column(name = "brand_id")
    @FormParam("brandId")
    private String brandId;

    /**
     * 所属分类id
     */
    @ApiModelProperty("所属分类id")
    @Column(name = "category_id")
    @FormParam("categoryId")
    private String categoryId;

    /**
     * 所有分类id
     */
    @ApiModelProperty("所有分类id")
    @Column(name = "all_category")
    @FormParam("allCategory")
    private String allCategory;

    /**
     * 条形码
     */
    @ApiModelProperty("条形码")
    @Column(name = "bar_code")
    @FormParam("barCode")
    private String barCode;

    /**
     * 含税退货单价,单位/分
     */
    @ApiModelProperty("含税退货单价,单位/分")
    @FormParam("price")
    private BigDecimal price;

    /**
     * 退货总金额
     */
    @ApiModelProperty("退货总金额")
    @Column(name = "total_amount")
    @FormParam("totalAmount")
    private BigDecimal totalAmount;

    /**
     * 可退数量
     */
    @ApiModelProperty("可退数量")
    @Column(name = "can_back_quantity")
    @FormParam("canBackQuantity")
    private Long canBackQuantity;

    /**
     * 退货数量
     */
    @ApiModelProperty("退货数量")
    @Column(name = "outbound_quantity")
    @FormParam("outboundQuantity")
    private Long outboundQuantity;

    /**
     * 实际出库数量
     */
    @ApiModelProperty("实际出库数量")
    @Column(name = "actual_storage_quantity")
    @FormParam("actualStorageQuantity")
    private Long actualStorageQuantity;

    /**
     * 库存ID
     */
    @ApiModelProperty("库存ID")
    @Column(name = "sku_stock_id")
    @FormParam("skuStockId")
    private Long skuStockId;

    /**
     * 商品SPU编号
     */
    @ApiModelProperty("商品SPU编号")
    @Column(name = "spu_code")
    @FormParam("spuCode")
    private String spuCode;

    /**
     * 商品货号
     */
    @ApiModelProperty("商品货号")
    @Column(name = "item_no")
    @FormParam("itemNo")
    private String itemNo;

    /**
     * 退货类型1-正品，2-残品
     */
    @ApiModelProperty("退货类型1-正品，2-残品")
    @Column(name = "return_order_type")
    @FormParam("returnOrderType")
    private String returnOrderType;

    /**
     * 退货税率
     */
    @ApiModelProperty("退货税率")
    @Column(name = "tax_rate")
    @FormParam("taxRate")
    private BigDecimal taxRate;

    /**
     * 货主编码
     */
    @ApiModelProperty("货主编码")
    @Column(name = "owner_code")
    @FormParam("ownerCode")
    private String ownerCode;

    /**
     * 实际出库时间
     */
    @ApiModelProperty("实际出库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    @Column(name = "storage_time")
    @FormParam("storageTime")
    private Date storageTime;

    /**
     * 出库异常原因
     */
    @ApiModelProperty("出库异常原因")
    @Column(name = "outbound_exception")
    @FormParam("outboundException")
    private String outboundException;

    ///**
    // * 是否有效:0-无效,1-有效
    // */
    //@Column(name = "is_valid")
    //private String isValid;
    //
    ///**
    // * 是否删除:0-否,1-是
    // */
    //@Column(name = "is_deleted")
    //private String isDeleted;
    //
    ///**
    // * 创建人
    // */
    //@Column(name = "create_operator")
    //private String createOperator;
    //
    ///**
    // * 创建时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //@Column(name = "create_time")
    //private Date createTime;
    //
    ///**
    // * 更新时间
    // */
    //@Column(name = "update_time")
    //private Date updateTime;


    public String getOutboundStatus() {
        return outboundStatus;
    }

    public void setOutboundStatus(String outboundStatus) {
        this.outboundStatus = outboundStatus;
    }

    /**
     * 规格描述
     */
    @ApiModelProperty("规格描述")
    @Column(name = "spec_nature_info")
    @FormParam("specNatureInfo")
    private String specNatureInfo;

    /**
     * 品牌名称
     */
    @ApiModelProperty("品牌名称")
    @Transient
    @FormParam("brandName")
    @Length(max = 256, message = "商品的品牌名称字母和数字不能超过256个,汉字不能超过128个")
    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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
     * 获取采购退货单编号
     *
     * @return purchase_outbound_order_code - 采购退货单编号
     */
    public String getPurchaseOutboundOrderCode() {
        return purchaseOutboundOrderCode;
    }

    /**
     * 设置采购退货单编号
     *
     * @param purchaseOutboundOrderCode 采购退货单编号
     */
    public void setPurchaseOutboundOrderCode(String purchaseOutboundOrderCode) {
        this.purchaseOutboundOrderCode = purchaseOutboundOrderCode;
    }

    /**
     * 获取采购退货单id
     *
     * @return outbound_notice_code - 采购退货单id
     */
    public String getOutboundNoticeCode() {
        return outboundNoticeCode;
    }

    /**
     * 设置采购退货单id
     *
     * @param outboundNoticeCode 采购退货单id
     */
    public void setOutboundNoticeCode(String outboundNoticeCode) {
        this.outboundNoticeCode = outboundNoticeCode;
    }

    /**
     * 获取出库状态 1-出库完成，2-出库异常
     *
     * @return status - 出库状态 1-出库完成，2-出库异常
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置出库状态 1-出库完成，2-出库异常
     *
     * @param status 出库状态 1-出库完成，2-出库异常
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取商品名称
     *
     * @return item_name - 商品名称
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置商品名称
     *
     * @param itemName 商品名称
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
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
     * 获取品牌id
     *
     * @return brand_id - 品牌id
     */
    public String getBrandId() {
        return brandId;
    }

    /**
     * 设置品牌id
     *
     * @param brandId 品牌id
     */
    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    /**
     * 获取所属分类id
     *
     * @return category_id - 所属分类id
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * 设置所属分类id
     *
     * @param categoryId 所属分类id
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取所有分类id
     *
     * @return all_category - 所有分类id
     */
    public String getAllCategory() {
        return allCategory;
    }

    /**
     * 设置所有分类id
     *
     * @param allCategory 所有分类id
     */
    public void setAllCategory(String allCategory) {
        this.allCategory = allCategory;
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
     * 获取含税退货单价,单位/分
     *
     * @return price - 含税退货单价,单位/分
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置含税退货单价,单位/分
     *
     * @param price 含税退货单价,单位/分
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取退货总金额
     *
     * @return total_amount - 退货总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置退货总金额
     *
     * @param totalAmount 退货总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取可退数量
     *
     * @return can_back_quantity - 可退数量
     */
    public Long getCanBackQuantity() {
        return canBackQuantity;
    }

    /**
     * 设置可退数量
     *
     * @param canBackQuantity 可退数量
     */
    public void setCanBackQuantity(Long canBackQuantity) {
        this.canBackQuantity = canBackQuantity;
    }

    /**
     * 获取退货数量
     *
     * @return outbound_quantity - 退货数量
     */
    public Long getOutboundQuantity() {
        return outboundQuantity;
    }

    /**
     * 设置退货数量
     *
     * @param outboundQuantity 退货数量
     */
    public void setOutboundQuantity(Long outboundQuantity) {
        this.outboundQuantity = outboundQuantity;
    }

    /**
     * 获取实际出库数量
     *
     * @return actual_storage_quantity - 实际出库数量
     */
    public Long getActualStorageQuantity() {
        return actualStorageQuantity;
    }

    /**
     * 设置实际出库数量
     *
     * @param actualStorageQuantity 实际出库数量
     */
    public void setActualStorageQuantity(Long actualStorageQuantity) {
        this.actualStorageQuantity = actualStorageQuantity;
    }

    /**
     * 获取库存ID
     *
     * @return sku_stock_id - 库存ID
     */
    public Long getSkuStockId() {
        return skuStockId;
    }

    /**
     * 设置库存ID
     *
     * @param skuStockId 库存ID
     */
    public void setSkuStockId(Long skuStockId) {
        this.skuStockId = skuStockId;
    }

    /**
     * 获取商品SPU编号
     *
     * @return spu_code - 商品SPU编号
     */
    public String getSpuCode() {
        return spuCode;
    }

    /**
     * 设置商品SPU编号
     *
     * @param spuCode 商品SPU编号
     */
    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    /**
     * 获取商品货号
     *
     * @return item_no - 商品货号
     */
    public String getItemNo() {
        return itemNo;
    }

    /**
     * 设置商品货号
     *
     * @param itemNo 商品货号
     */
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 获取退货类型1-正品，2-残品
     *
     * @return return_order_type - 退货类型1-正品，2-残品
     */
    public String getReturnOrderType() {
        return returnOrderType;
    }

    /**
     * 设置退货类型1-正品，2-残品
     *
     * @param returnOrderType 退货类型1-正品，2-残品
     */
    public void setReturnOrderType(String returnOrderType) {
        this.returnOrderType = returnOrderType;
    }

    /**
     * 获取退货税率
     *
     * @return tax_rate - 退货税率
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * 设置退货税率
     *
     * @param taxRate 退货税率
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * 获取货主编码
     *
     * @return owner_code - 货主编码
     */
    public String getOwnerCode() {
        return ownerCode;
    }

    /**
     * 设置货主编码
     *
     * @param ownerCode 货主编码
     */
    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    /**
     * 获取实际出库时间
     *
     * @return storage_time - 实际出库时间
     */
    public Date getStorageTime() {
        return storageTime;
    }

    /**
     * 设置实际出库时间
     *
     * @param storageTime 实际出库时间
     */
    public void setStorageTime(Date storageTime) {
        this.storageTime = storageTime;
    }

    /**
     * 获取出库异常原因
     *
     * @return outbound_exception - 出库异常原因
     */
    public String getOutboundException() {
        return outboundException;
    }

    /**
     * 设置出库异常原因
     *
     * @param outboundException 出库异常原因
     */
    public void setOutboundException(String outboundException) {
        this.outboundException = outboundException;
    }

    /**
     * 获取是否有效:0-无效,1-有效
     *
     * @return is_valid - 是否有效:0-无效,1-有效
     */
    //public String getIsValid() {
    //    return isValid;
    //}
    //
    ///**
    // * 设置是否有效:0-无效,1-有效
    // *
    // * @param isValid 是否有效:0-无效,1-有效
    // */
    //public void setIsValid(String isValid) {
    //    this.isValid = isValid;
    //}
    //
    ///**
    // * 获取是否删除:0-否,1-是
    // *
    // * @return is_deleted - 是否删除:0-否,1-是
    // */
    //public String getIsDeleted() {
    //    return isDeleted;
    //}
    //
    ///**
    // * 设置是否删除:0-否,1-是
    // *
    // * @param isDeleted 是否删除:0-否,1-是
    // */
    //public void setIsDeleted(String isDeleted) {
    //    this.isDeleted = isDeleted;
    //}
    //
    ///**
    // * 获取创建人
    // *
    // * @return create_operator - 创建人
    // */
    //public String getCreateOperator() {
    //    return createOperator;
    //}
    //
    ///**
    // * 设置创建人
    // *
    // * @param createOperator 创建人
    // */
    //public void setCreateOperator(String createOperator) {
    //    this.createOperator = createOperator;
    //}
    //
    ///**
    // * 获取创建时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @return create_time - 创建时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public Date getCreateTime() {
    //    return createTime;
    //}
    //
    ///**
    // * 设置创建时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @param createTime 创建时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public void setCreateTime(Date createTime) {
    //    this.createTime = createTime;
    //}
    //
    ///**
    // * 获取更新时间
    // *
    // * @return update_time - 更新时间
    // */
    //public Date getUpdateTime() {
    //    return updateTime;
    //}
    //
    ///**
    // * 设置更新时间
    // *
    // * @param updateTime 更新时间
    // */
    //public void setUpdateTime(Date updateTime) {
    //    this.updateTime = updateTime;
    //}

    /**
     * 获取规格描述
     *
     * @return spec_nature_info - 规格描述
     */
    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    /**
     * 设置规格描述
     *
     * @param specNatureInfo 规格描述
     */
    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}