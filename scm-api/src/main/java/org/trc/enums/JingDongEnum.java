package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzwyz on 2017/6/3 0003.
 */
public enum JingDongEnum {
    ORDER_SUBMIT_ORDER_SUCCESS("000001","统一下单成功");
    private String code;
    private String message;

    JingDongEnum(String code, String message){
        this.code = code;
        this.message = message;
    }


    /**
     *
     * @Title: getJingDongEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return JingDongEnum
     * @throws
     */
    public static JingDongEnum getJingDongEnumByCode(String code){
        for(JingDongEnum jdEnum : JingDongEnum.values()){
            if(StringUtils.equals(jdEnum.getCode(), code)){
                return jdEnum;
            }
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
