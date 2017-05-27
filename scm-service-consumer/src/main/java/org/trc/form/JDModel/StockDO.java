package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/5/26 0026.
 */
public class StockDO {
    //地区编码
    private String area;

    //描述
    private String desc;

    //商品信息
    private String sku;

    //有无货状态
    private String state;

    //剩余数量
    private String remainNum;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(String remainNum) {
        this.remainNum = remainNum;
    }
}
