package org.trc.service.tairan.model;

/**
 * 属性
 * Created by hzdzf on 2017/5/24.
 */
public class PropertyToTrc {

    private String name;//属性名称

    private String description;//属性描述

    private Integer sort;//排序

    private String typeCode;//属性类型编码

    private String valueType;//属性值类型

    private String isValid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
