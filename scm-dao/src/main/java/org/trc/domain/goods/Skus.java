package org.trc.domain.goods;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.math.BigDecimal;
import java.util.List;

//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Skus extends ScmDO {

    private static final long serialVersionUID = 2671659563299088628L;
    @ApiParam("主键ID")
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ApiParam("sku编码")
    @PathParam("skuCode")
    private String skuCode;
    @ApiParam("商品ID")
    @FormParam("itemId")
    private Long itemId;
    @ApiParam("商品SPU编号")
    @FormParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @ApiParam("属性值id")
    @FormParam("propertyValueId")
    @Length(max = 64, message = "属性值id长度不能超过64个")
    private String propertyValueId;
    @ApiParam("属性值")
    @FormParam("propertyValue")
    @Length(max = 128, message = "属性值长度不能超过128个")
    private String propertyValue;
    @ApiParam("条形码")
    @FormParam("barCode")
    @Length(max = 512, message = "条形码长度不能超过256个")
    private String barCode;
    @ApiParam("市场参考价")
    @FormParam("marketPrice")
    private Long marketPrice;
    @ApiParam("重量")
    @FormParam("weight")
    private Long weight;
    @ApiParam("市场参考价2")
    @FormParam("marketPrice2")
    @Transient
    private BigDecimal marketPrice2;
    @ApiParam("重量2")
    @FormParam("weight2")
    @Transient
    private BigDecimal weight2;
    @FormParam("predictChannelPrice")
    private Long predictChannelPrice;
    @ApiParam("商品SKU图片")
    @FormParam("picture")
    @Length(max = 1024, message = "商品SKU图片长度不能超过1024个")
    private String picture;
    @FormParam("channel1PreSellPrices")
    private Long channel1PreSellPrices;
    @FormParam("channel2PreSellPrices")
    private Long channel2PreSellPrices;
    @FormParam("channel3PreSellPrices")
    private Long channel3PreSellPrices;
    @FormParam("channel4PreSellPrices")
    private Long channel4PreSellPrices;
    @FormParam("channel5PreSellPrices")
    private Long channel5PreSellPrices;
    @FormParam("channel6PreSellPrices")
    private Long channel6PreSellPrices;
    @FormParam("channel7PreSellPrices")
    private Long channel7PreSellPrices;

    @ApiParam("是否有效:0-否,1-是")
    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是
    /**
     * sku信息
     */
    @ApiParam("sku信息")
    @FormParam("skusInfo")
    @Transient
    @NotEmpty
    private String skusInfo;

    /**
     * 采购属性组合名称
     */
    @ApiParam(" 采购属性组合名称")
    @Transient
    private String propertyCombineName;

    /**
     * 可用库存
     */
    @ApiParam("可用库存")
    @Transient
    private Long availableInventory;
    /**
     * 真实库存
     */
    @ApiParam("真实库存")
    @Transient
    private Long realInventory;
    /**
     * 残次品库存
     */
    @ApiParam("残次品库存")
    @Transient
    private Long defectiveInventory;

    /**
     * 商品名称
     */
    @ApiParam("商品名称")
    @Transient
    private String itemsName;
    /**
     * 分类名称
     */
    @ApiParam("分类名称")
    @Transient
    private String categoryName;
    /**
     * 品牌名称
     */
    @ApiParam("品牌名称")
    @Transient
    private String brandName;
    /**
     * 仓库名称
     */
    @ApiParam("仓库名称")
    @Transient
    private String warehouse;

    /**
     * 库存
     */
    @ApiParam("库存")
    @Transient
    private Long stock;

    @ApiParam("商品SKU名称")
    @FormParam("skuName")
    @Length(max = 256, message = "商品SKU名字不能超过256个")
    private String skuName;
    
    /**
     * 商品主图，来自spu
     */
    @ApiParam("商品主图")
    @Transient
    private String mainPicture;
    
    /**
     * sku名称，与skuName相同，提供给泰然城时用
     */
    @ApiParam("sku名称，与skuName相同，提供给泰然城时用")
    @Transient
    private String name;
    
    /**
     * sku规格信息
     */
    @ApiParam("sku规格信息")
    @FormParam("specInfo")
    private String specInfo;

    @Transient
    private List<RequestSkuStock> requestSkuStockList;

    //sku关联状态: 1-已关联,0-未关联
    @ApiParam("sku关联状态: 1-已关联,0-未关联")
    @Transient
    private String skuRelationStatus;

    @ApiParam("分类编码")
    @Transient
    private String categoryCode;
    @ApiParam("品牌编码")
    @Transient
    private String brandCode;
    @ApiParam("分类ID")
    @Transient
    private Long categoryId;
    @ApiParam("品牌ID")
    @Transient
    private Long brandId;
    @ApiParam("库存列表")
    @Transient
    private List<SkuStock> stockList;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    public String getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(String propertyValueId) {
        this.propertyValueId = propertyValueId == null ? null : propertyValueId.trim();
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue == null ? null : propertyValue.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public Long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getPredictChannelPrice() {
        return predictChannelPrice;
    }

    public void setPredictChannelPrice(Long predictChannelPrice) {
        this.predictChannelPrice = predictChannelPrice;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public Long getChannel1PreSellPrices() {
        return channel1PreSellPrices;
    }

    public void setChannel1PreSellPrices(Long channel1PreSellPrices) {
        this.channel1PreSellPrices = channel1PreSellPrices;
    }

    public Long getChannel2PreSellPrices() {
        return channel2PreSellPrices;
    }

    public void setChannel2PreSellPrices(Long channel2PreSellPrices) {
        this.channel2PreSellPrices = channel2PreSellPrices;
    }

    public Long getChannel3PreSellPrices() {
        return channel3PreSellPrices;
    }

    public void setChannel3PreSellPrices(Long channel3PreSellPrices) {
        this.channel3PreSellPrices = channel3PreSellPrices;
    }

    public Long getChannel4PreSellPrices() {
        return channel4PreSellPrices;
    }

    public void setChannel4PreSellPrices(Long channel4PreSellPrices) {
        this.channel4PreSellPrices = channel4PreSellPrices;
    }

    public Long getChannel5PreSellPrices() {
        return channel5PreSellPrices;
    }

    public void setChannel5PreSellPrices(Long channel5PreSellPrices) {
        this.channel5PreSellPrices = channel5PreSellPrices;
    }

    public Long getChannel6PreSellPrices() {
        return channel6PreSellPrices;
    }

    public void setChannel6PreSellPrices(Long channel6PreSellPrices) {
        this.channel6PreSellPrices = channel6PreSellPrices;
    }

    public Long getChannel7PreSellPrices() {
        return channel7PreSellPrices;
    }

    public void setChannel7PreSellPrices(Long channel7PreSellPrices) {
        this.channel7PreSellPrices = channel7PreSellPrices;
    }

    public String getSkusInfo() {
        return skusInfo;
    }

    public void setSkusInfo(String skusInfo) {
        this.skusInfo = skusInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public BigDecimal getMarketPrice2() {
        return marketPrice2;
    }

    public void setMarketPrice2(BigDecimal marketPrice2) {
        this.marketPrice2 = marketPrice2;
    }

    public BigDecimal getWeight2() {
        return weight2;
    }

    public void setWeight2(BigDecimal weight2) {
        this.weight2 = weight2;
    }

    public String getPropertyCombineName() {
        return propertyCombineName;
    }

    public void setPropertyCombineName(String propertyCombineName) {
        this.propertyCombineName = propertyCombineName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(Long availableInventory) {
        this.availableInventory = availableInventory;
    }

    public Long getRealInventory() {
        return realInventory;
    }

    public void setRealInventory(Long realInventory) {
        this.realInventory = realInventory;
    }

    public Long getDefectiveInventory() {
        return defectiveInventory;
    }

    public void setDefectiveInventory(Long defectiveInventory) {
        this.defectiveInventory = defectiveInventory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemsName() {
        return itemsName;
    }

    public void setItemsName(String itemsName) {
        this.itemsName = itemsName;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

	public String getSpecInfo() {
		return specInfo;
	}

	public void setSpecInfo(String specInfo) {
		this.specInfo = specInfo;
	}



    public String getSkuRelationStatus() {
        return skuRelationStatus;
    }

    public void setSkuRelationStatus(String skuRelationStatus) {
        this.skuRelationStatus = skuRelationStatus;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public List<RequestSkuStock> getRequestSkuStockList() {
        return requestSkuStockList;
    }

    public void setRequestSkuStockList(List<RequestSkuStock> requestSkuStockList) {
        this.requestSkuStockList = requestSkuStockList;
    }

    public List<SkuStock> getStockList() {
        return stockList;
    }

    public void setStockList(List<SkuStock> stockList) {
        this.stockList = stockList;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
}