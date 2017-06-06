package org.trc.form.jingdong;

/**
 * Created by hzwyz on 2017/6/3 0003.
 */
public class NewStockDO {
    private String areaId;

    private String stockStateDesc;

    private String skuId;

    private String stockStateId;

    private String remainNum;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getStockStateDesc() {
        return stockStateDesc;
    }

    public void setStockStateDesc(String stockStateDesc) {
        this.stockStateDesc = stockStateDesc;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getStockStateId() {
        return stockStateId;
    }

    public void setStockStateId(String stockStateId) {
        this.stockStateId = stockStateId;
    }

    public String getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(String remainNum) {
        this.remainNum = remainNum;
    }
}
