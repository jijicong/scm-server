package org.trc.domain.purchase;

import javax.persistence.Transient;
import javax.ws.rs.FormParam;

public class PurchaseOutboundOrderDataForm extends PurchaseOutboundOrder {
    @FormParam("gridValue")
    @Transient
    private String gridValue;

    public String getGridValue() {
        return gridValue;
    }

    public void setGridValue(String gridValue) {
        this.gridValue = gridValue;
    }
}