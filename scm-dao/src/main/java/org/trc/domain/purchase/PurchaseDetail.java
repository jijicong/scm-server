package org.trc.domain.purchase;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.math.BigDecimal;

/**
 * 采购明细信息
 * Created by sone on 2017/5/25.
 */
public class PurchaseDetail extends BaseDO{
	
	private static final long serialVersionUID = -7915245096652290335L;
	
	@PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("purchaseId")
    @NotEmpty
    private Long purchaseId;
    @FormParam("purchaseOrderCode")
    @NotEmpty
    @Length(max = 32, message = "采购单编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    @FormParam("spuCode")
    @NotEmpty
    @Length(max = 32, message = "采购的商品SPU编码长度不能超过32个")
    private String spuCode;
    @FormParam("skuCode")
    @NotEmpty
    @Length(max = 32, message = "商品的sku名称字母和数字不能超过32个,汉字不能超过16个")
    private String skuCode;
    @FormParam("brandId")
    private Long brandId;
    @Transient
    @FormParam("brandName")
    @Length(max = 256, message = "商品的品牌名称字母和数字不能超过256个,汉字不能超过128个")
    private String brandName;
    @FormParam("categoryId")
    private Long  categoryId;
    @Transient
    @FormParam("allCategoryName")
    @Length(max = 128, message = "商品的所属分类的名称字母和数字不能超过128个,汉字不能超过64个")
    private String  allCategoryName;
    @FormParam("allCategory")
    @Length(max = 64, message = "商品的所有分类字母和数字不能超过64个,汉字不能超过32个")
    private String allCategory;//所有分类
    @FormParam("purchasePrice")
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal purchasePrice;//采购单价
    @Transient
    private BigDecimal purchasePriceD;
    @FormParam("purchasingQuantity")
    private Long purchasingQuantity;//采购总数量
    @FormParam("totalPurchaseAmount")
    //@JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalPurchaseAmount;//采购总金额
    @FormParam("barCode")
    private String barCode;//条形码
    @FormParam("itemNo")
    private String itemNo;//商品货号
    @FormParam("skuName")
    private String skuName;//sku名称
    @FormParam("batchCode")
    @Length(max = 10, message = "批次号字母和数字不能超过20个")
    private String batchCode;//批次号
    @FormParam("produceCode")
    @Length(max = 10, message = "生产编码字母和数字不能超过20个")
    private String produceCode;//生产编码
    @FormParam("productDate")
    @Length(max = 10, message = "生产日期长度不能超过10个")
    private String productDate;//生产日期
    @FormParam("expireDate")
    @Length(max = 10, message = "截止日期长度不能超过10个")
    private String expireDate;//截止日期
    @FormParam("shelfLifeDays")
    private Integer shelfLifeDays;//理论保质期限（天）
    @FormParam("warehouseItemInfoId")
    private Long warehouseItemInfoId;//仓库商品信息主键
    @FormParam("specNatureInfo")
    private String specNatureInfo;//规格
    @FormParam("warehouseItemId")
    private String warehouseItemId;

    /**
     * v2.5 商品入库状态 0-等待入库,1-全部入库,2-部分入库,3-入库异常,其他情况为null
     */
    @FormParam("receiveStatus")
    private String receiveStatus;

    /**
     * v2.5 税率
     */
    @FormParam("taxrate")
    private BigDecimal taxRate;

    @Transient
    private BigDecimal totalPurchaseAmountD;



    //是否具有质保期
    @Transient
    private String isQuality;
    @Transient
    private Long qualityDay;

    /**
     * v2.5
     */
    @Transient
    private String storageTime; //入库时间
    @Transient
    private Long normalStorageQuantity; //正品入库数量
    @Transient
    private Long defectiveStorageQuantity;  //残次品入库数量
    @Transient
    private Long actualStorageQuantity; //实际入库数量

    public Long getActualStorageQuantity() {
        return actualStorageQuantity;
    }

    public void setActualStorageQuantity(Long actualStorageQuantity) {
        this.actualStorageQuantity = actualStorageQuantity;
    }

    public String getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(String storageTime) {
        this.storageTime = storageTime;
    }

    public Long getNormalStorageQuantity() {
        return normalStorageQuantity;
    }

    public void setNormalStorageQuantity(Long normalStorageQuantity) {
        this.normalStorageQuantity = normalStorageQuantity;
    }

    public Long getDefectiveStorageQuantity() {
        return defectiveStorageQuantity;
    }

    public void setDefectiveStorageQuantity(Long defectiveStorageQuantity) {
        this.defectiveStorageQuantity = defectiveStorageQuantity;
    }

    public BigDecimal getPurchasePriceD() {
        return purchasePriceD;
    }

    public void setPurchasePriceD(BigDecimal purchasePriceD) {
        this.purchasePriceD = purchasePriceD;
    }

    public BigDecimal getTotalPurchaseAmountD() {
        return totalPurchaseAmountD;
    }

    public void setTotalPurchaseAmountD(BigDecimal totalPurchaseAmountD) {
        this.totalPurchaseAmountD = totalPurchaseAmountD;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getAllCategoryName() {
        return allCategoryName;
    }

    public void setAllCategoryName(String allCategoryName) {
        this.allCategoryName = allCategoryName;
    }

    public String getAllCategory() {
        return allCategory;
    }

    public void setAllCategory(String allCategory) {
        this.allCategory = allCategory;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getPurchasingQuantity() {
        return purchasingQuantity;
    }

    public void setPurchasingQuantity(Long purchasingQuantity) {
        this.purchasingQuantity = purchasingQuantity;
    }

    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {

        this.totalPurchaseAmount = totalPurchaseAmount;

    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(Integer shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public Long getWarehouseItemInfoId() {
        return warehouseItemInfoId;
    }

    public void setWarehouseItemInfoId(Long warehouseItemInfoId) {
        this.warehouseItemInfoId = warehouseItemInfoId;
    }

    public String getWarehouseItemId() {
        return warehouseItemId;
    }

    public void setWarehouseItemId(String warehouseItemId) {
        this.warehouseItemId = warehouseItemId;
    }

    public String getIsQuality() {
        return isQuality;
    }

    public void setIsQuality(String isQuality) {
        this.isQuality = isQuality;
    }

    public Long getQualityDay() {
        return qualityDay;
    }

    public void setQualityDay(Long qualityDay) {
        this.qualityDay = qualityDay;
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}

