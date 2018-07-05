package org.trc.domain.goods;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

public class BusiItems implements Serializable {

    @PathParam("id")
    @Id
    private String id;
    @FormParam("spuCode")
    @Length(max = 64, message = "商品SPU编号长度不能超过64个")
    private String spuCode;
    @FormParam("name")
    @NotEmpty(message = "商品名称不能为空!")
    @Length(max = 200, message = "商品名称长度不能超过200个")
    private String name;
    @FormParam("firstCategoryId")
    @NotNull(message = "一级类目ID不能为空")
    private Long firstCategoryId;
    @Transient
    private String firstCategoryName;//分类名称

    @FormParam("secondCategoryId")
    @NotNull(message = "二级类目ID不能为空")
    private Long secondCategoryId;
    @Transient
    private String secondCategoryName;//二级类目名称

    @FormParam("thirdCategoryId")
    @NotNull(message = "三级类目ID不能为空")
    private Long thirdCategoryId;
    @Transient
    private String thirdCategoryName;//三级类目名称

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
    @FormParam("pictrue")
    @Length(max = 256, message = "商品图片路径长度不能超过256个")
    private String pictrue;
    @FormParam("remark")
    @Length(max = 512, message = "备注长度不能超过512个")
    private String remark;

    @FormParam("isQuality")
    @NotNull(message = "是否具有质保管理不能为空")
    private String isQuality;

    @FormParam("qualityDay")
    private Long qualityDay;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime; //更新时间

    @FormParam("createOperator")
    @Length(max = 32, message = "字典类型编码字母和数字不能超过32个,汉字不能超过16个")
    private String createOperator;

    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

    @FormParam("scmIsValid")
    @Length(max = 2, message = "供应链主系统是否有效编码字母和数字不能超过2个")
    private String scmIsValid; //供应链主系统是否有效:0-无效,1-有效

    /**
     * SPU商品主图
     */
    @FormParam("mainPicture")
    private String mainPicture;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture == null ? null : mainPicture.trim();
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

    public Long getFirstCategoryId() {
        return firstCategoryId;
    }

    public void setFirstCategoryId(Long firstCategoryId) {
        this.firstCategoryId = firstCategoryId;
    }

    public String getFirstCategoryName() {
        return firstCategoryName;
    }

    public void setFirstCategoryName(String firstCategoryName) {
        this.firstCategoryName = firstCategoryName;
    }

    public Long getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Long secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public String getSecondCategoryName() {
        return secondCategoryName;
    }

    public void setSecondCategoryName(String secondCategoryName) {
        this.secondCategoryName = secondCategoryName;
    }

    public Long getThirdCategoryId() {
        return thirdCategoryId;
    }

    public void setThirdCategoryId(Long thirdCategoryId) {
        this.thirdCategoryId = thirdCategoryId;
    }

    public String getThirdCategoryName() {
        return thirdCategoryName;
    }

    public void setThirdCategoryName(String thirdCategoryName) {
        this.thirdCategoryName = thirdCategoryName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getScmIsValid() {
        return scmIsValid;
    }

    public void setScmIsValid(String scmIsValid) {
        this.scmIsValid = scmIsValid;
    }
}