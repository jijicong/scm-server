package org.trc.enums;

/**
 * 物流类型
 * Created by hzqph on 2017/5/16.
 */
public enum LogsticsTypeEnum {
    WAYBILL_NUMBER("0","物流单号"),LOGSTICS("1","配送信息");


    public static LogsticsTypeEnum queryNameByCode(String code){
        for(LogsticsTypeEnum auditStatusEnum: LogsticsTypeEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    LogsticsTypeEnum(String code, String name) {
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
