package org.trc.form.JDModel;

/**
 * Created by hzwdx on 2017/6/21.
 */
public class ExternalSupplierConfig {

    //京东sku分页查询url地址
    private String skuPageUrl;
    //京东sku查询url地址
    private String skuQueryUrl;
    //添加京东sku通知url地址
    private String skuAddNotice;
    //京东仓库名称
    private String jdWarehouse;
    //粮油仓库名称
    private String lyWarehouse;
    //京东图片查看url
    private String jdPictureUrl;
    //京东下单接口地址
    private String submitOrderUrl;
    //京东订单反查接口地址
    private String orderQueryUrl;
    //配送信息查看url
    private String orderLogisticsUrl;
    //京东下单发票抬头
    private String companyName;

    public String getSkuPageUrl() {
        return skuPageUrl;
    }

    public void setSkuPageUrl(String skuPageUrl) {
        this.skuPageUrl = skuPageUrl;
    }

    public String getSkuQueryUrl() {
        return skuQueryUrl;
    }

    public void setSkuQueryUrl(String skuQueryUrl) {
        this.skuQueryUrl = skuQueryUrl;
    }

    public String getSkuAddNotice() {
        return skuAddNotice;
    }

    public void setSkuAddNotice(String skuAddNotice) {
        this.skuAddNotice = skuAddNotice;
    }

    public String getJdWarehouse() {
        return jdWarehouse;
    }

    public void setJdWarehouse(String jdWarehouse) {
        this.jdWarehouse = jdWarehouse;
    }

    public String getLyWarehouse() {
        return lyWarehouse;
    }

    public void setLyWarehouse(String lyWarehouse) {
        this.lyWarehouse = lyWarehouse;
    }

    public String getJdPictureUrl() {
        return jdPictureUrl;
    }

    public void setJdPictureUrl(String jdPictureUrl) {
        this.jdPictureUrl = jdPictureUrl;
    }

    public String getSubmitOrderUrl() {
        return submitOrderUrl;
    }

    public void setSubmitOrderUrl(String submitOrderUrl) {
        this.submitOrderUrl = submitOrderUrl;
    }

    public String getOrderQueryUrl() {
        return orderQueryUrl;
    }

    public void setOrderQueryUrl(String orderQueryUrl) {
        this.orderQueryUrl = orderQueryUrl;
    }

    public String getOrderLogisticsUrl() {
        return orderLogisticsUrl;
    }

    public void setOrderLogisticsUrl(String orderLogisticsUrl) {
        this.orderLogisticsUrl = orderLogisticsUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
