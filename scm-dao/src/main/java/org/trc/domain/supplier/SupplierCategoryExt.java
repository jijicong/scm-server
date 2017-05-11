package org.trc.domain.supplier;

import org.trc.domain.util.ScmDO;

public class SupplierCategoryExt extends SupplierCategory {

    /**
     * 分类名称
     */
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}