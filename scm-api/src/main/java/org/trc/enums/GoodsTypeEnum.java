package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 */
public enum GoodsTypeEnum {
    SELF_PURCHARSE(1,"自采"),SUPPLIER(2,"代发");


    public static GoodsTypeEnum queryNameByCode(Integer code){
        for(GoodsTypeEnum sourceEnum: GoodsTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    GoodsTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
