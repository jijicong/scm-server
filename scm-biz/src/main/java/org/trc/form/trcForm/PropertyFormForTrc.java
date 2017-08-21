package org.trc.form.trcForm;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * 对接泰然城的属性查询条件
 * Created by sone321 on 2017/8/16.
 */
public class PropertyFormForTrc extends QueryModel {
    @QueryParam("name")
    @Length(max = 10, message = "属性名称不能超过10个字符")
    private String name;
    @QueryParam("sort")
    private String sort;//排序
    @QueryParam("typeCode")
    private String typeCode;
    //属性的ids查询
    @QueryParam("propertyId")
    private String propertyId;
    @QueryParam("flag")
    private String flag; //0-属性查询(包括属性值数据) 1-属性值查询
    @QueryParam("propertyValueId")
    private String propertyValueId;//属性值的id，多个id用逗号隔开,当flag=1时作为查询条件
    @QueryParam("propertyValue")
    private String propertyValue; //属性值,模糊匹配,当flag=1时作为查询条件

    public String getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(String propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
