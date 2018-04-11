package org.trc.form.warehouse;

/**
 * Created by hzcyn on 2018/4/11.
 */
public class ScmOrderPacksRequest extends ScmWarehouseRequestBase {

    /**
     * 开放平台出库单号(多条以逗号的格式分开)
     */
    private String orderIds;

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds;
    }
}
