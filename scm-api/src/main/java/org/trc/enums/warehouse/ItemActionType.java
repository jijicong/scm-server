package org.trc.enums.warehouse;

/**
 * 商品同步动作类型
 */
public enum ItemActionType {

    ADD("add","新增"), UPDATE("update","更新");

    ItemActionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
