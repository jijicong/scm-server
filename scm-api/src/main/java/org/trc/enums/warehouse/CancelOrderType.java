package org.trc.enums.warehouse;

/**
 * 商品同步动作类型
 */
public enum CancelOrderType {

    /** 取消的单据类型
    * 0：发货单 
    * 1：采购单 
    * 2：调拨出库单 
    * 3：调拨入库单
    */
    DELIVERY("0"), 
    PURCHASE("1"), 
    ALLOCATE_OUT("2"), 
    ALLOCATE_IN("3");

    CancelOrderType(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
