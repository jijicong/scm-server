package org.trc.form.warehouse;

import java.io.Serializable;

public class ScmWarehouseRequestBase implements Serializable{

    /**
     * 仓库类型:
     */
    private String warehouseType;

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

}
