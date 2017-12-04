package org.trc.enums;

/**
 * Created by wangyz on 2017/11/16.
 * 库存类型枚举
 */
public enum InventoryTypeEnum {
    ZP("ZP","正品"),CC("CC","残次"),JS("JS","机损"),XS("XS","箱损"),ZT("ZT","在途库存");

    InventoryTypeEnum(String code, String name) {
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
