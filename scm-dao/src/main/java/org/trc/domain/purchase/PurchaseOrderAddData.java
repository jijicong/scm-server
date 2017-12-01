package org.trc.domain.purchase;

import javax.persistence.Transient;
import javax.ws.rs.FormParam;

/**
 * Created by sone on 2017/6/20.
 */
public class PurchaseOrderAddData extends PurchaseOrder{
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
