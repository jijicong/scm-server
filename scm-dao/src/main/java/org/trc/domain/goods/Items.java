package org.trc.domain.goods;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Items extends BaseDO{

    private static final long serialVersionUID = -8948886744275187652L;
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("spuCode")
    @Length(max = 64, message = "商品SPU编号长度不能超过64个")
    private String spuCode;
    @FormParam("name")
    @NotEmpty(message = "商品名称不能为空!")
    @Length(max = 200, message = "商品名称长度不能超过200个")
    private String name;
    @FormParam("categoryId")
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @Transient
    private String categoryName;//分类名称
    @FormParam("brandId")
    @NotNull(message = "品牌不能为空")
    private Long brandId;
    @Transient
    private String brandName;//供应商名称
    @FormParam("tradeType")
    @NotEmpty(message = "贸易类型不能为空")
    @Length(max = 32, message = "贸易类型长度不能超过32个")
    private String tradeType;
    @FormParam("itemNo")
    @Length(max = 32, message = "商品货号长度不能超过32个")
    private String itemNo;
    @FormParam("weight")
    private Long weight;
    @FormParam("producer")
    @Length(max = 128, message = "生产商长度不能超过32个")
    private String producer;
    @FormParam("marketPrice")
    private Long marketPrice;
    @FormParam("pictrue")
    @Length(max = 256, message = "商品图片路径长度不能超过256个")
    private String pictrue;
    @FormParam("remark")
    @Length(max = 512, message = "备注长度不能超过512个")
    private String remark;
    @FormParam("properties")
    @Length(max = 512, message = "属性量长度不能超过512个")
    private String properties;

    @FormParam("isQuality")
    @NotNull(message = "是否具有质保管理不能为空")
    private String isQuality;

    @FormParam("qualityDay")
    private Long qualityDay;

    /**
     * SKU列表
     */
    @Transient
    private List<Skus> records;

    @Transient
    private String categoryCode;
    @Transient
    private String brandCode;

    /**
     * SPU商品主图
     */
    @FormParam("mainPicture")
    private String mainPicture;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode == null ? null : spuCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo == null ? null : itemNo.trim();
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer == null ? null : producer.trim();
    }

    public Long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getPictrue() {
        return pictrue;
    }

    public void setPictrue(String pictrue) {
        this.pictrue = pictrue == null ? null : pictrue.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties == null ? null : properties.trim();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<Skus> getRecords() {
        return records;
    }

    public void setRecords(List<Skus> records) {
        this.records = records;
    }

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture == null ? null : mainPicture.trim();
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
}