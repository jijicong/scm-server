package org.trc.enums.report;

/**
 * Created by hzcyn on 2018/9/27.
 */
public enum OutboundOperationTypeEnum {

    SALES_OF_OUTBOUND("1", "销售出库"),
    ALLALLOCATE_OUT("2", "调拨出库"),
    SALES_RETURN_OUT("3", "退货出库"),
    INVENTORY_LOSSES("4", "盘亏出库"),
    NORMAL_TO_DEFECTIVE("5", "正品转残品出库数量"),
    OTHER_OUT("6", "其他出库");

    public static OutboundOperationTypeEnum queryNameByCode(String code){
        for(OutboundOperationTypeEnum outboundOperationTypeEnum: OutboundOperationTypeEnum.values()){
            if (outboundOperationTypeEnum.getCode().equals(code)){
                return outboundOperationTypeEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    OutboundOperationTypeEnum(String code, String name) {
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
