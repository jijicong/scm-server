package org.trc.form.order;

import java.io.Serializable;
import java.util.List;

public class InventoryQueryResponseForm implements Serializable{

    private List<InventoryQueryItemDO> inventoryQueryItemDOList;

    public List<InventoryQueryItemDO> getInventoryQueryItemDOList() {
        return inventoryQueryItemDOList;
    }

    public void setInventoryQueryItemDOList(List<InventoryQueryItemDO> inventoryQueryItemDOList) {
        this.inventoryQueryItemDOList = inventoryQueryItemDOList;
    }
}
