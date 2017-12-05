package org.trc.form.order;

import com.qimen.api.response.InventoryQueryResponse;


public class InventoryQueryItemDO extends InventoryQueryResponse.Item{

    //奇门货主编码
    private String ownerCode;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }
}
