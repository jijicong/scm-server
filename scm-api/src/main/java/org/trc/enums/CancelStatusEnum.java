package org.trc.enums;

/**
 * 取消状态
 * Created by hzqph on 2017/5/16.
 */
public enum CancelStatusEnum {
    CLOASE_CANCEL("0","关闭取消"),CANCEL("1","已取消");
    public static CancelStatusEnum queryNameByCode(String code){
        for(CancelStatusEnum auditStatusEnum: CancelStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    CancelStatusEnum(String code, String name) {
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
