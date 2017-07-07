package org.trc.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class Logistic {

    //供应商订单编码
    private String supplierOrderCode;
    //物流公司
    private String logisticsCorporation;
    //运单号
    private String waybillNumber;
    //物流状态,0-新建,1-妥投,2-拒收
    private String logisticsStatus;
    //京东物理配送信息
    private List<LogisticInfo> logisticInfo;
    //skus
    private List<SkuInfo> skus;

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode;
    }

    public String getLogisticsCorporation() {
        return logisticsCorporation;
    }

    public void setLogisticsCorporation(String logisticsCorporation) {
        this.logisticsCorporation = logisticsCorporation;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public List<LogisticInfo> getLogisticInfo() {
        return logisticInfo;
    }

    public void setLogisticInfo(List<LogisticInfo> logisticInfo) {
        this.logisticInfo = logisticInfo;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public List<SkuInfo> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuInfo> skus) {
        this.skus = skus;
    }
}
