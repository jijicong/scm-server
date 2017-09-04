package org.trc.biz.impl.trc.model;

import java.io.Serializable;

/**
 * Created by hzwdx on 2017/8/31.
 */
public class SkusProperty implements Serializable{

    /**
     * 属性ID
     */
    private Long propertyId;
    /**
     * 属性名称
     */
    private String propertyName;
    /**
     * 属性值ID
     */
    private Long propertyValueId;
    /**
     * 属性值
     */
    private String propertyValue;

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Long getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(Long propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
