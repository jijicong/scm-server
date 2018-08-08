package org.trc.form.order;

import org.trc.domain.order.SupplierOrderLogistics;

public class SkuLogisticsInfo {

    public SkuLogisticsInfo(){}

    public SkuLogisticsInfo(SupplierOrderLogistics supplierOrderLogistics, Integer sendNum){
        this.supplierOrderLogistics = supplierOrderLogistics;
        this.sendNum = sendNum;
    }

    //物流信息
    private SupplierOrderLogistics supplierOrderLogistics;
    //发货数量
    private Integer sendNum;

    public SupplierOrderLogistics getSupplierOrderLogistics() {
        return supplierOrderLogistics;
    }

    public void setSupplierOrderLogistics(SupplierOrderLogistics supplierOrderLogistics) {
        this.supplierOrderLogistics = supplierOrderLogistics;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }
}
