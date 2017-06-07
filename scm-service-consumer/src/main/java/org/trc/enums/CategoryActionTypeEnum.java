package org.trc.enums;

/**
 * Created by hzdzf on 2017/6/6.
 */
public enum CategoryActionTypeEnum {

    ADD_CATEGORY("ADD_CATEGORY","新增分类"),
    EDIT_CATEGORY("EDIT_CATEGORY","编辑分类"),
    STOP_CATEGORY("STOP_CATEGORY","停用分类"),
    EDIT_CATEGORY_BRAND("EDIT_CATEGORY_BRAND","编辑分类品牌"),
    EDIT_CATEGORY_PROPERTY("EDIT_CATEGORY_PROPERTY","编辑分类属性");

    private String code;

    private String description;

    CategoryActionTypeEnum(String code,String description){
        this.code = code;
        this.description =description;
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
