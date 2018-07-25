package org.trc.enums.warehouse;

/**
 * Created by hzliuw on 2018/7/25.
 */
public enum PurchaseOutboundNoticeStatusEnum {
    TO_BE_NOTIFIED(0, "待通知出库"),
    ON_WAREHOUSE_TICKLING(1, "出库仓接收成功"),
    WAREHOUSE_RECEIVE_FAILED(1, "出库仓接收失败"),
    PASS(1, "出库完成"),
    RECEIVE_EXCEPTION(1, "出库异常"),
    CANCEL(1, "已取消");

    public static PurchaseOutboundNoticeStatusEnum queryNameByCode(Integer code){
        for(PurchaseOutboundNoticeStatusEnum sourceEnum: PurchaseOutboundNoticeStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private Integer code;
    private String name;

    PurchaseOutboundNoticeStatusEnum(Integer code, String name) {
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
