package org.trc.domain.category;

/**
 * Created by hzwdx on 2017/5/19.
 */
public class CategoryPropertyExt extends CategoryProperty {
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 属性名称
     */
    private String propertyName;
    /**
     * 属性类型
     */
    private String valueType;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
