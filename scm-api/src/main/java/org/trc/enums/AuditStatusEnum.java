package org.trc.enums;

/**
 * Created by hzqph on 2017/5/16.
 */
public enum AuditStatusEnum {
    HOLD(0,"暂存"),COMMIT(1,"提交审核"),PASS(2,"审核通过"),REJECT(3,"审核驳回");


    public static AuditStatusEnum queryNameByCode(Integer code){
        for(AuditStatusEnum auditStatusEnum: AuditStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    AuditStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
