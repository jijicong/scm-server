package org.trc.enums;

/**
 * 成功/失败
 * Created by hzqph on 2017/5/16.
 */
public enum SuccessFailureEnum {
    FAILURE("0","失败"),SUCCESS("1","成功"),SOCKET_TIME_OUT("2","网络超时"),ERROR("3","异常");

    public static SuccessFailureEnum queryNameByCode(String code){
        for(SuccessFailureEnum auditStatusEnum: SuccessFailureEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SuccessFailureEnum(String code, String name) {
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
