package org.trc.form.warehouse;

import java.util.List;

/**
 * 库存查询参数
 */
public class ScmInventoryQueryRequest  extends ScmWarehouseRequestBase{

    /**
     * 查询条件列表
     */
    private List<ScmInventoryQueryItem> scmInventoryQueryItemList;

    public List<ScmInventoryQueryItem> getScmInventoryQueryItemList() {
        return scmInventoryQueryItemList;
    }

    public void setScmInventoryQueryItemList(List<ScmInventoryQueryItem> scmInventoryQueryItemList) {
        this.scmInventoryQueryItemList = scmInventoryQueryItemList;
    }



}
