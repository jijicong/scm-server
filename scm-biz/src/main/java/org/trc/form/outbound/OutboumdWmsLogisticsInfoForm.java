package org.trc.form.outbound;

import java.util.List;

public class OutboumdWmsLogisticsInfoForm {

    /**
     * 物流公司编码
     */
    private String logistictsCode;
    /**
     * 物流公司名称
     */
    private String logistictsName;
    /**
     * 运单号
     */
    private String wayBill;
    /**
     * 物流包裹SKU信息
     */
    private List<OutboumdWmsDeliverSkuInfoForm> outboumdWmsDeliverSkuInfoFormList;

    public String getLogistictsCode() {
        return logistictsCode;
    }

    public void setLogistictsCode(String logistictsCode) {
        this.logistictsCode = logistictsCode;
    }

    public String getLogistictsName() {
        return logistictsName;
    }

    public void setLogistictsName(String logistictsName) {
        this.logistictsName = logistictsName;
    }

    public List<OutboumdWmsDeliverSkuInfoForm> getOutboumdWmsDeliverSkuInfoFormList() {
        return outboumdWmsDeliverSkuInfoFormList;
    }

    public void setOutboumdWmsDeliverSkuInfoFormList(List<OutboumdWmsDeliverSkuInfoForm> outboumdWmsDeliverSkuInfoFormList) {
        this.outboumdWmsDeliverSkuInfoFormList = outboumdWmsDeliverSkuInfoFormList;
    }

    public String getWayBill() {
        return wayBill;
    }

    public void setWayBill(String wayBill) {
        this.wayBill = wayBill;
    }
}
