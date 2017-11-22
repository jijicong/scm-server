package org.trc.form.warehouseInfo;

import java.io.Serializable;

/**
 * Created by hzcyn on 2017/11/17.
 */
public class WarehouseItemInfoException implements Serializable {

    private String skuCode;
    private String itemId;
    private String exceptionReason;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    @Override
    public String toString() {
        return "WarehouseItemInfoException{" +
                "skuCode='" + skuCode + '\'' +
                ", itemId='" + itemId + '\'' +
                ", exceptionReason='" + exceptionReason + '\'' +
                '}';
    }
}
