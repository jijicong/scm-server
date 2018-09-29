package org.trc.enums.report;

/**
 * Created by hzcyn on 2018/9/17.
 */
public enum GoodsTypeEnum {
    XIAO_TAI("1", "小泰良品"),
    NO_XIAO_TAI("2", "非小泰良品");

    public static GoodsTypeEnum queryNameByCode(String code){
        for(GoodsTypeEnum goodsTypeEnum: GoodsTypeEnum.values()){
            if (goodsTypeEnum.getCode().equals(code)){
                return goodsTypeEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    GoodsTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

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
