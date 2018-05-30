package org.trc.form.outbound;

public class OutboumdWmsDeliverSkuInfoForm {

    //sku编码
    private String skuCode;
    //sku名称
    private String skuName;
    //商品数量
    private Long num;

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

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }
}
