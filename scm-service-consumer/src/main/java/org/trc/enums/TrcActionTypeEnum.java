package org.trc.enums;

/**
 * Created by hzdzf on 2017/6/6.
 */
public enum TrcActionTypeEnum {

    ADD_BRAND("ADD_BRAND", "新增品牌"),
    EDIT_BRAND("EDIT_BRAND", "编辑品牌"),
    STOP_BRAND("STOP_BRAND", "停用品牌"),
    ADD_PROPERTY("ADD_PROPERTY", "新增属性"),
    EDIT_PROPERTY("EDIT_PROPERTY", "编辑属性"),
    STOP_PROPERTY("STOP_PROPERTY", "停用属性"),
    EDIT_ITEMS("EDIT_ITEMS", "编辑商品"),
    ADD_CATEGORY("ADD_CATEGORY", "新增分类"),
    EDIT_CATEGORY("EDIT_CATEGORY", "编辑分类"),
    STOP_CATEGORY("STOP_CATEGORY", "停用分类"),
    EDIT_CATEGORY_BRAND("EDIT_CATEGORY_BRAND", "编辑分类品牌"),
    EDIT_CATEGORY_PROPERTY("EDIT_CATEGORY_PROPERTY", "编辑分类属性");

    private String code;

    private String description;

    TrcActionTypeEnum() {
    }

    TrcActionTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
