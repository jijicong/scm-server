package org.trc.enums.purchase;

/**
 * Created by hzliuw on 2018/7/25.
 */
public enum PurchaseOutboundOrderStatusEnum {
    HOLD("0", "暂存"),
    AUDIT("1", "提交审核"),
    REJECT("2", "审核驳回"),
    PASS("3", "审核通过"),
    WAREHOUSE_NOTICE("4", "出库通知"),
    DROPPED("5", "作废");

    public static PurchaseOutboundOrderStatusEnum queryNameByCode(String code){
        for(PurchaseOutboundOrderStatusEnum sourceEnum: PurchaseOutboundOrderStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PurchaseOutboundOrderStatusEnum(String code, String name) {
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
