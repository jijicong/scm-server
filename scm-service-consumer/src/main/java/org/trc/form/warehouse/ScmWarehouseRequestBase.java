package org.trc.form.warehouse;

import java.io.Serializable;

public class ScmWarehouseRequestBase implements Serializable{

    /**
     * 仓库类型:默认为京东开放平台
     * Qimen("QM","奇门"),
	 * Jingdong("JD","京东");
     */
    private String warehouseType = "JD";

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

}
