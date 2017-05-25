package org.trc.domain.goods;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

public class ItemSalesPropery extends ScmDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("itemId")
    @NotEmpty
    private Long itemId;
    @FormParam("spuCode")
    @NotEmpty
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    @FormParam("skuCode")
    @NotEmpty
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    @FormParam("propertyId")
    @NotEmpty
    private Long propertyId;
    @FormParam("propertyValueId")
    @NotEmpty
    private Long propertyValueId;
    @FormParam("propertyActualValue")
    @Length(max = 256, message = "属性实际值长度不能超过256个")
    private String propertyActualValue;
    @FormParam("picture")
    @Length(max = 256, message = "图片路径长度不能超过256个")
    private String picture;

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

}