package org.trc.enums;

/**
 * 物流公司类型
 */
public enum LogisticsTypeEnum {
    QIMEN("QIMEN","QIMEN"),TRC("TRC","泰然城");


    public static LogisticsTypeEnum queryNameByCode(Integer code){
        for(LogisticsTypeEnum sourceEnum: LogisticsTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    LogisticsTypeEnum(String code, String name) {
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
