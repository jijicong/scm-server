package org.trc.domain.supplier;

import org.trc.domain.util.ScmDO;

public class SupplierCategoryExt extends SupplierCategory {

    /**
     * 分类编码
     */
    private String categoryCode;
    /**
     * 分类名称
     */
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}