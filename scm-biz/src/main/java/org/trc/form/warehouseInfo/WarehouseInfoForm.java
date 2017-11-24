package org.trc.form.warehouseInfo;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfoForm extends QueryModel {
    /**
     * 入库通知单编码
     */
    @QueryParam("warehouseName")
    private String warehouseName;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
