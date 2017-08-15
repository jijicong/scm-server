package org.trc.form.category;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzszy on 2017/5/5.
 */
public class CategoryForm extends QueryModel {
    @QueryParam("level")
    private String level;
    @QueryParam("name")
    private String name;
    @QueryParam("sort")
    private String sort;
    @QueryParam("id")
    private Long id;
    @QueryParam("categoryId")
    private Long categoryId;
    @QueryParam("categoryCode")
    private String categoryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Override
    public String toString() {
        return  ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
