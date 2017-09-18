package org.trc.domain.trcDomain;

import javax.persistence.Id;

public class CategoryPropertyRels {
    @Id
    private Integer categoryId;
    @Id
    private Integer propertyId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }
}
