package org.trc.enums.purchase;

/**
 * Created by hzliuw on 2018/7/31.
 * 提货方式
 */
public enum PickTypeEnum {
    ONESELF("1", "到仓自提"),
    JD_DELIVERY("2", "京东配送"),
    OTHER_DELIVERY("3", "其他物流");

    public static PickTypeEnum queryNameByCode(String code){
        for(PickTypeEnum sourceEnum: PickTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PickTypeEnum(String code, String name) {
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
