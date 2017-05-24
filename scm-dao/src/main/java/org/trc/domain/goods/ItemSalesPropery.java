package org.trc.domain.goods;

import org.trc.domain.util.ScmDO;

import java.util.Date;

public class ItemSalesPropery extends ScmDO {
    private Long id;

    private Long itemId;

    private String spuCode;

    private String skuCode;

    private Long propertyId;

    private Long propertyValueId;

    private String propertyActualValue;

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