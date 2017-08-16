package org.trc.domain.forTrc;

import javax.persistence.*;
import java.io.Serializable;

/**
 *用户封装属性值的信息
 */
public class PropertyValueForTrc implements Serializable {

    private Long propertyId;

    private String value;

    private String picture;

    private Integer sort;

    private String isValid; //是否有效:0-否,1-是

    private String isDeleted; //是否删除:0-否,1-是

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}