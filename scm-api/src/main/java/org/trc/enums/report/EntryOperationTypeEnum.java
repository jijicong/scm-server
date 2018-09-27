package org.trc.enums.report;

/**
 * Created by hzcyn on 2018/9/27.
 */
public enum EntryOperationTypeEnum {

    PURCHASE("1", "采购入库"),
    SALES_RETURN_IN("2", "退货入库"),
    ALLALLOCATE_IN("3", "调拨入库"),
    INVENTORY_PROFIT("4", "盘盈入库"),
    DEFECTIVE_TO_NORMAL("5", "残品转正品入库数量"),
    OTHER_IN("6", "其他入库");

    public static EntryOperationTypeEnum queryNameByCode(String code){
        for(EntryOperationTypeEnum entryOperationTypeEnum: EntryOperationTypeEnum.values()){
            if (entryOperationTypeEnum.getCode().equals(code)){
                return entryOperationTypeEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    EntryOperationTypeEnum(String code, String name) {
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
