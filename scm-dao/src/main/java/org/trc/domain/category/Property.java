package org.trc.domain.category;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;

public class Property extends BaseDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @FormParam("name")
    @Length(max=10,message="属性名称不得超过10个字符")
    private String name;//属性名称
    @Length(max=10,message="属性描述不得超过20个字符")
    @FormParam("description")
    private String description;//属性描述
    @NotEmpty
    @FormParam("typeCode")
    private String typeCode;//属性类型编码
    @NotEmpty
    @FormParam("valueType")
    private String valueType;//属性值类型
    @NotEmpty
    @FormParam("sort")
    private String sort;//排序
    @Transient
    @FormParam("gridValue")
    private String gridValue;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode == null ? null : typeCode.trim();
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType == null ? null : valueType.trim();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getGridValue() {
        return gridValue;
    }

    public void setGridValue(String gridValue) {
        this.gridValue = gridValue;
    }
}