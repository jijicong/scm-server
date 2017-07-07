package org.trc.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class LogisticForm {

    //仓库订单编码
    private String warehouseOrderCode;
    //信息类型:0-物流单号,1-配送信息
    private String type;
    //物流信息
    private List<Logistic> logistics;


    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Logistic> getLogistics() {
        return logistics;
    }

    public void setLogistics(List<Logistic> logistics) {
        this.logistics = logistics;
    }
}
