package org.trc.enums.warehouse;

/**
 * Created by hzliuw on 2018/7/31.
 * 采购退货单退货类型
 */
public enum PurchaseOutboundOrderTypeEnum {
    QUALITY("1", "正品"),
    SUBSTANDARD("2", "残品");

    public static PurchaseOutboundOrderTypeEnum queryNameByCode(String code){
        for(PurchaseOutboundOrderTypeEnum sourceEnum: PurchaseOutboundOrderTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    PurchaseOutboundOrderTypeEnum(String code, String name) {
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
