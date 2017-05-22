package org.trc.form.category;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/18.
 */
public class CategoryBrandForm{

    @QueryParam("brandId")
    private String brandId;
    @QueryParam("categoryId")
    private String categoryId;//多个分类ID用逗号分隔

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
