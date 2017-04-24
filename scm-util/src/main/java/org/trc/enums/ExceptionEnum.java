package org.trc.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Created by hzwdx on 2017/4/22.
 */
public enum ExceptionEnum {
    /**
     * 异常编码按模块划分：
     * 系统管理:000开头
     * 配置管理:100开头
     * 类目管理:200开头
     * 供应商管理:300开头
     * 商品管理:400开头
     * 采购管理:500开头
     * 订单管理:600开头
     * 库存管理:700开头
     * 审批管理:800开头
     * 权限管理:900开头
     * 外部调用:1000开头
     * 通用功能:2000开头
     */
    PARAM_CHECK_EXCEPTION("2000100","参数校验错误"),
    CONFIG_DICT_QUERY_EXCEPTION("100101","数据字典查询异常"),
    CONFIG_DICT_UPDATE_EXCEPTION("100102","数据字典更新异常"),

    JAVA_BEAN_TO_MAP_EXCEPTION("2000100","JavaBean转Map数据转换异常"),
    MAPJ_TO_AVA_BEAN_EXCEPTION("2000100","Map转JavaBean数据转换异常"),

    NOVALID("0","停用");

    private String code;
    private String message;

    ExceptionEnum(String code, String message){
        this.code = code;
        this.message = message;
    }


    /**
     *
     * @Title: getExceptionEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return ExceptionEnum
     * @throws
     */
    public static ExceptionEnum getExceptionEnumByCode(String code){
        for(ExceptionEnum exceptionEnum : ExceptionEnum.values()){
            if(StringUtils.equals(exceptionEnum.getCode(), code)){
                return exceptionEnum;
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
