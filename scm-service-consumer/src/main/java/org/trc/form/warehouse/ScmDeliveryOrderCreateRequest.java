package org.trc.form.warehouse;

import java.util.List;

public class ScmDeliveryOrderCreateRequest  extends ScmWarehouseRequestBase{

    /**
     * 采购单参数列表
     */
    private List<ScmDeliveryOrderDO> scmDeleveryOrderDOList;

    public List<ScmDeliveryOrderDO> getScmDeleveryOrderDOList() {
        return scmDeleveryOrderDOList;
    }

    public void setScmDeleveryOrderDOList(List<ScmDeliveryOrderDO> scmDeleveryOrderDOList) {
        this.scmDeleveryOrderDOList = scmDeleveryOrderDOList;
    }


}
