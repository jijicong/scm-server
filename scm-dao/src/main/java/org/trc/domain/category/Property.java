package org.trc.domain.category;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.forTrc.PropertyValueForTrc;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.List;

public class Property extends BaseDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @FormParam("name")
    @Length(max = 10, message = "属性名称不得超过10个字符")
    private String name;//属性名称
    @Length(max = 20, message = "属性描述不得超过20个字符")
    @FormParam("description")
    private String description;//属性描述
    @NotEmpty
    @FormParam("typeCode")
    private String typeCode;//属性类型编码
    @NotEmpty
    @FormParam("valueType")
    private String valueType;//属性值类型
    @NotNull
    @FormParam("sort")
    private Integer sort;//排序
    @Transient
    @FormParam("gridValue")
    private String gridValue;
    private String lastEditOperator;//最新更新人
    @Transient
    private List<PropertyValueForTrc> propertyValueList;

    public List<PropertyValueForTrc> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValueForTrc> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getGridValue() {
        return gridValue;
    }

    public void setGridValue(String gridValue) {
        this.gridValue = gridValue;
    }

    public String getLastEditOperator() {
        return lastEditOperator;
    }

    public void setLastEditOperator(String lastEditOperator) {
        this.lastEditOperator = lastEditOperator;
    }
}