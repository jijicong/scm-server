package org.trc.domain.goods;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BusiSkus implements Serializable {

    private static final long serialVersionUID = 6939917346316442364L;
    @PathParam("id")
    @Id
    private String id;
    @PathParam("skuCode")
    private String skuCode;
    @FormParam("skuName")
    @Length(max = 256, message = "商品SKU名字不能超过256个")
    private String skuName;
    @FormParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @FormParam("propertyValueId")
    @Length(max = 64, message = "属性值id长度不能超过64个")
    private String propertyValueId;
    @FormParam("barCode")
    @Length(max = 512, message = "条形码长度不能超过256个")
    private String barCode;
    @FormParam("marketPrice")
    private BigDecimal marketPrice;
    @FormParam("weight")
    private Long weight;
    @FormParam("picture")
    @Length(max = 1024, message = "商品SKU图片长度不能超过1024个")
    private String picture;
    /**
     * sku规格信息
     */   
    @FormParam("specInfo")
    private String specInfo;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(String propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSpecInfo() {
        return specInfo;
    }

    public void setSpecInfo(String specInfo) {
        this.specInfo = specInfo;
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