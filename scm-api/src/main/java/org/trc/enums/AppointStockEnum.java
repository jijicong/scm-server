package org.trc.enums;

import org.trc.util.StringUtil;

/**
 * 是否指定仓库
 */
public enum AppointStockEnum {
    YES("0", "是"), NO("1", "否");


    public static AppointStockEnum queryNameByCode(StringUtil code){
        for(AppointStockEnum appointStockEnum: AppointStockEnum.values()){
            if (appointStockEnum.getCode().equals(code)){
                return appointStockEnum;
            }
        }
        return null;
    }

    AppointStockEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

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
