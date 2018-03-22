package org.trc.form.warehouse;

import java.util.List;

public class ScmDeliveryOrderCreateRequest  extends ScmWarehouseRequestBase{

    /**
     * 采购单参数列表
     */
    private List<ScmDeleveryOrderDO> scmDeleveryOrderDOList;

    public List<ScmDeleveryOrderDO> getScmDeleveryOrderDOList() {
        return scmDeleveryOrderDOList;
    }

    public void setScmDeleveryOrderDOList(List<ScmDeleveryOrderDO> scmDeleveryOrderDOList) {
        this.scmDeleveryOrderDOList = scmDeleveryOrderDOList;
    }
}
