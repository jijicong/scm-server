package org.trc.domain.order;

import java.io.Serializable;

public class DeliverPackageForm implements Serializable{

    /**
     *物流公司,当字段type=0-物流信息时不为空
     */
    private String logisticsCorporation;
    /**
     *运单号,当字段type=0-物流信息时不为空
     */
    private String waybillNumber;

    /**
     *sku编码
     */
    private String skuCode;
    /**
     * 商品数量
     */
    private Integer skuNum;

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

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }
}
