package org.trc.enums.purchase;

/**
 * Created by hzliuw on 2018/08/01.
 * 采购退货单审核状态维护
 */
public enum PurchaseOutboundOrderAuditStatusEnum {
    COMMIT("1", "提交审核"),
    REJECT("2", "审核驳回"),
    PASS("3", "审核通过");

    public static PurchaseOutboundOrderAuditStatusEnum queryNameByCode(String code){
        for(PurchaseOutboundOrderAuditStatusEnum sourceEnum: PurchaseOutboundOrderAuditStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PurchaseOutboundOrderAuditStatusEnum(String code, String name) {
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
