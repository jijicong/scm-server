package org.trc.enums;

public enum AfterSaleTypeEnum {
    RETURN_GOODS(1, "退货"),
    CANCEL_DELIVER(0, "取消发货");
    private int code;
    private String name;

    AfterSaleTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
