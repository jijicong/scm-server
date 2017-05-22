package org.trc.enums;

/**
 * Created by hzqph on 2017/5/18.
 */
public enum SortStatusEnum {
    NOT_UPDATE(0,"未更新"),UPDATE(1,"更新");


    public static SortStatusEnum queryNameByCode(Integer code){
        for(SortStatusEnum sortStatusEnum: SortStatusEnum.values()){
            if (sortStatusEnum.getCode().equals(code)){
                return sortStatusEnum;
            }
        }
        return null;
    }
    SortStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

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
