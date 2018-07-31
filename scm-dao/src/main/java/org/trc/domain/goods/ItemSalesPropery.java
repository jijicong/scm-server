package org.trc.domain.goods;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class ItemSalesPropery extends ScmDO {

    private static final long serialVersionUID = 509046100194015433L;
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("itemId")
    private Long itemId;
    @FormParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @FormParam("skuCode")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    @FormParam("propertyId")
    private Long propertyId;
    @Transient
    private String propertyName;
    @FormParam("propertyValueId")
    private Long propertyValueId;
    @FormParam("propertyActualValue")
    @Length(max = 256, message = "属性实际值长度不能超过256个")
    private String propertyActualValue;
    @FormParam("picture")
    @Length(max = 256, message = "图片路径长度不能超过256个")
    private String picture;
    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

    /**
     * 采购属性信息
     */
    @FormParam("salesPropertys")
    @Transient
    @NotEmpty
    private String salesPropertys;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(Long propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public String getPropertyActualValue() {
        return propertyActualValue;
    }

    public void setPropertyActualValue(String propertyActualValue) {
        this.propertyActualValue = propertyActualValue == null ? null : propertyActualValue.trim();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public String getSalesPropertys() {
        return salesPropertys;
    }

    public void setSalesPropertys(String salesPropertys) {
        this.salesPropertys = salesPropertys;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}