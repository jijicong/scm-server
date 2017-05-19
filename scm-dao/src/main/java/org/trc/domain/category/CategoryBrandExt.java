package org.trc.domain.category;

/**
 * Created by hzwdx on 2017/5/19.
 */
public class CategoryBrandExt extends CategoryBrand{
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 品牌名称
     */
    private String brandName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

}
