package org.trc.form.warehouseInfo;

import com.qimen.api.request.ItemsSynchronizeRequest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzcyn on 2017/11/22.
 */
public class ItemsSynchronizeForm implements Serializable {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;
    /**
     *同步的商品信息
     */
    private List<ItemsSynchronizeRequest.Item> items;

    public ItemsSynchronizeForm(String warehouseCode, String ownerCode, List<ItemsSynchronizeRequest.Item> items) {
        this.warehouseCode = warehouseCode;
        this.ownerCode = ownerCode;
        this.items = items;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public List<ItemsSynchronizeRequest.Item> getItems() {
        return items;
    }

    public void setItems(List<ItemsSynchronizeRequest.Item> items) {
        this.items = items;
    }
}
