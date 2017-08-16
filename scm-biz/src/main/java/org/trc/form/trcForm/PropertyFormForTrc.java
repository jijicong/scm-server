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
