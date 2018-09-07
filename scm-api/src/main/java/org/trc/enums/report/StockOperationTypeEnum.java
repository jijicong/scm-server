package org.trc.enums.report;


public enum StockOperationTypeEnum {
    PURCHASE("1", "采购入库"),
    SALES_RETURN_IN("2", "退货入库"),
    ALLALLOCATE_IN("3", "调拨入库"),
    INVENTORY_PROFIT("4", "盘盈入库"),

    SALES_OF_OUTBOUND("1", "销售出库"),
    ALLALLOCATE_OUT("2", "调拨出库"),
    SALES_RETURN_OUT("3", "退货出库"),
    INVENTORY_LOSSES("4", "盘亏出库");

    public static StockOperationTypeEnum queryNameByCode(String code){
        for(StockOperationTypeEnum stockOperationTypeEnum: StockOperationTypeEnum.values()){
            if (stockOperationTypeEnum.getCode().equals(code)){
                return stockOperationTypeEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    StockOperationTypeEnum(String code, String name) {
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
