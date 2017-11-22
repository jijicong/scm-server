package org.trc.enums;

/**
 * Created by wangyz on 2017/11/21.
 */
public enum ItemTypeEnum {
    NOEMAL("ZC","正常");

    ItemTypeEnum(String code, String name) {
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
