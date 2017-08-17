package org.trc.form.trc;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzszy on 2017/5/5.
 */
public class CategoryForm2 extends QueryModel {
    /**
     * 0-普通分页查询,1-根据父分类ID查询子分类
     */
    @QueryParam("flag")
    private String flag;
    @QueryParam("level")
    private String level;
    @QueryParam("name")
    private String name;
    @QueryParam("categoryId")
    private String categoryId;
    @QueryParam("categoryCode")
    private String categoryCode;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
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
