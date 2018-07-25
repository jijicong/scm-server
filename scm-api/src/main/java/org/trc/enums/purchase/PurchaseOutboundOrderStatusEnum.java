package org.trc.enums.purchase;

/**
 * Created by hzliuw on 2018/7/25.
 */
public enum PurchaseOutboundOrderStatusEnum {
    HOLD(0, "暂存"),
    AUDIT(1, "提交审核"),
    REJECT(1, "审核驳回"),
    PASS(1, "审核通过"),
    WAREHOUSE_NOTICE(1, "出库通知"),
    DROPPED(1, "作废");

    public static PurchaseOutboundOrderStatusEnum queryNameByCode(Integer code){
        for(PurchaseOutboundOrderStatusEnum sourceEnum: PurchaseOutboundOrderStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    PurchaseOutboundOrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

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
