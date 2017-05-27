package org.trc.form.category;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzqph on 2017/5/4.
 */
public class PropertyForm extends QueryModel {
    @QueryParam("name")
    @Length(max = 10, message = "属性名称不能超过10个字符")
    private String name;
    @QueryParam("sort")
    private String sort;//排序
    @QueryParam("typeCode")
    private String typeCode;

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
