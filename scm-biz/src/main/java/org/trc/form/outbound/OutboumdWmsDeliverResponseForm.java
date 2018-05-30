package org.trc.form.outbound;

import java.util.List;

public class OutboumdWmsDeliverResponseForm {

    /**
     * 发货单编号
     */
    private String outboundOrderCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 物流信息
     */
    private List<OutboumdWmsLogisticsInfoForm> outboumdWmsLogisticsInfoFormList;

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public List<OutboumdWmsLogisticsInfoForm> getOutboumdWmsLogisticsInfoFormList() {
        return outboumdWmsLogisticsInfoFormList;
    }

    public void setOutboumdWmsLogisticsInfoFormList(List<OutboumdWmsLogisticsInfoForm> outboumdWmsLogisticsInfoFormList) {
        this.outboumdWmsLogisticsInfoFormList = outboumdWmsLogisticsInfoFormList;
    }

}
