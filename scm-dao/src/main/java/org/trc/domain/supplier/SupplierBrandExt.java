package org.trc.domain.supplier;

/**
 * Created by hzwdx on 2017/5/19.
 */
public class SupplierBrandExt extends SupplierBrand{

    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 品牌名称
     */
    private String brandName;

    private Long times;

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getBrandName() {
        return brandName;
    }

    @Override
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
