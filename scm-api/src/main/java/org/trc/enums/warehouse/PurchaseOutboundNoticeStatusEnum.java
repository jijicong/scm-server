package org.trc.enums.warehouse;

/**
 * Created by hzliuw on 2018/7/25.
 */
public enum PurchaseOutboundNoticeStatusEnum {
    TO_BE_NOTIFIED("0", "待通知出库"),
    ON_WAREHOUSE_TICKLING("1", "出库仓接收成功"),
    WAREHOUSE_RECEIVE_FAILED("2", "出库仓接收失败"),
    PASS("3", "出库完成"),
    RECEIVE_EXCEPTION("4", "出库异常"),
    CANCEL("5", "已取消"),
	CANCELLING("6", "取消中");

    public static PurchaseOutboundNoticeStatusEnum queryNameByCode(String code){
        for(PurchaseOutboundNoticeStatusEnum sourceEnum: PurchaseOutboundNoticeStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PurchaseOutboundNoticeStatusEnum(String code, String name) {
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
