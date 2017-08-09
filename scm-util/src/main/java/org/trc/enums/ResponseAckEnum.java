package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzwdx on 2017/8/9.
 */
public enum ResponseAckEnum {

    SUCCESS("200","成功"),




    /*CONFIG_DICT_QUERY_EXCEPTION("100101","数据字典查询异常"),
    CONFIG_DICT_QUERY_EXCEPTION("100101","数据字典查询异常"),*/
    ;

    private String code;
    private String message;

    ResponseAckEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    /**
     *
     * @Title: getExceptionEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return CommonExceptionEnum
     * @throws
     */
    public static CommonExceptionEnum getExceptionEnumByCode(String code){
        for(CommonExceptionEnum exceptionEnum : CommonExceptionEnum.values()){
            if(StringUtils.equals(exceptionEnum.getCode(), code)){
                return exceptionEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
