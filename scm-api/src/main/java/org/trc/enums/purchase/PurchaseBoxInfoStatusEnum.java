package org.trc.enums.purchase;

/**
 * Created by hzcyn on 2018/7/25.
 */
public enum PurchaseBoxInfoStatusEnum {
    UNFINISH(0, "未完成"),
    FINISH(1, "完成");

    public static PurchaseBoxInfoStatusEnum queryNameByCode(Integer code){
        for(PurchaseBoxInfoStatusEnum sourceEnum: PurchaseBoxInfoStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    PurchaseBoxInfoStatusEnum(Integer code, String name) {
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
