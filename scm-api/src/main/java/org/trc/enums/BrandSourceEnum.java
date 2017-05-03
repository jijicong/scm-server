package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 */
public enum BrandSourceEnum {
    SCM("scm","系统录入"),TRC("trc","泰然城录入");


    public static BrandSourceEnum queryNameByCode(String code){
        for(BrandSourceEnum brandSourceEnum: BrandSourceEnum.values()){
            if (brandSourceEnum.getCode().equals(code)){
                return brandSourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    BrandSourceEnum(String code, String name) {
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
