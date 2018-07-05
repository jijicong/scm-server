package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 */
public enum SellChannelTypeEnum {
    ON_LINE(0,"线上"),OFF_LINE(1,"线下(非门店)"),STORE(2,"门店");


    public static SellChannelTypeEnum queryNameByCode(Integer code){
        for(SellChannelTypeEnum sourceEnum: SellChannelTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    SellChannelTypeEnum(Integer code, String name) {
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
