package org.trc.enums.purchase;

/**
 * Created by hzliuw on 2018/7/31.
 * 采购退货单对应出库单状态
 */
public enum PurchaseOutboundStatusEnum {
    WAIT("1", "等待出库"),
    FINISH("2", "出库完成"),
    EXCEPTION("3", "出库异常"),
    OTHER("4", "其他");

    public static PurchaseOutboundStatusEnum queryNameByCode(String code){
        for(PurchaseOutboundStatusEnum sourceEnum: PurchaseOutboundStatusEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PurchaseOutboundStatusEnum(String code, String name) {
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
