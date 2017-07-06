package org.trc.form;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class SkuInfo {

    //sku编码
    private String skuCode;
    //sku商品名称
    private String skuName;
    //购买商品数量
    private Integer num;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
