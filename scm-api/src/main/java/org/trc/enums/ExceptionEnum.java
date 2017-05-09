package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzwdx on 2017/4/22.
 */
public enum ExceptionEnum{
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
     * 数据库:3000开头
     * 系统异常:4000开头
     */
    CONFIG_DICT_QUERY_EXCEPTION("100101","数据字典查询异常"),
    CONFIG_DICT_SAVE_EXCEPTION("100102","数据字典保存异常"),
    CONFIG_DICT_UPDATE_EXCEPTION("100103","数据字典更新异常"),

    SUPPLIER_QUERY_EXCEPTION("300100","供应商查询异常"),
    SUPPLIER_SAVE_EXCEPTION("300101","供应商保存异常"),
    SUPPLIER_UPDATE_EXCEPTION("300102","供应商更新异常"),

    FILE_UPLOAD_EXCEPTION("1000100","文件上传异常"),
    FILE_DOWNLOAD_EXCEPTION("1000101","文件下载异常"),
    FILE_SHOW_EXCEPTION("1000102","文件显示异常"),

    CATEGORY_BRAND_QUERY_EXCEPTION("200101","品牌查询异常"),
    CATEGORY_BRAND_UPDATE_EXCEPTION("200102","品牌更新异常"),

    SYSTEM_CHANNEL_QUERY_EXCEPTION("000101","渠道查询异常"),
    SYSTEM_CHANNEL_SAVE_EXCEPTION("000102","渠道保存异常"),
    SYSTEM_CHANNEL_UPDATE_EXCEPTION("000103","渠道更新异常"),
    SYSTEM_WAREHOUSE_QUERY_EXCEPTION("000201","仓库查询异常"),
    SYSTEM_WAREHOUSE_SAVE_EXCEPTION("000202","仓库保存异常"),
    SYSTEM_WAREHOUSE_UPDATE_EXCEPTION("000203","仓库更新异常"),

    DATABASE_DUPLICATE_KEY_EXCEPTION("3000100","数据库主键重复异常"),
    DATABASE_PERMISSION_DENIED_EXCEPTION("3000101","数据库数据访问权限异常"),
    DATABASE_QUERY_TIME_OUT_EXCEPTION("3000102","数据库查询超时异常"),
    DATABASE_DEADLOCK_DATA_ACESS_EXCEPTION("3000103","数据库死锁访问数据异常"),

    DATABASE_DATA_VERSION_EXCEPTION("3000104","数据库的流水记录正在使用"),

    SYSTEM_EXCEPTION("4000100","系统异常"),

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
