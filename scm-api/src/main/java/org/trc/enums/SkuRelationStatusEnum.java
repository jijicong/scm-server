package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 * sku关联状态
 */
public enum SkuRelationStatusEnum {
    RELATION("1","已关联"),NOT_RELATION("0","未关联");


    public static SkuRelationStatusEnum queryNameByCode(Integer code){
        for(SkuRelationStatusEnum sourceEnum: SkuRelationStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    SkuRelationStatusEnum(String code, String name) {
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
