package org.trc.form.JDModel;

/**
 * Created by hzwdx on 2017/6/21.
 */
public class ExternalSupplierConfig {
    //scm-external项目服务地址
    private String scmExternalUrl;
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
    //京东sku价格查询接口url地址
    private String jdSkuPriceUrl;
    //京东地址查询接口url地址
    private String jdAddressUrl;
    //京东下单接口地址
    private String jdSubmitOrderUrl;
    //京东订单反查接口地址
    private String orderQueryUrl;
    //配送信息查看url
    private String orderLogisticsUrl;
    //京东下单发票抬头
    private String companyName;
    //京东对账明细
    private String checkOrderDetailUrl;
    //京东业务类型接口
    private String treadTypeUrl;

    //粮油下单接口地址
    private String lySubmitOrderUrl;

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

    public String getJdSubmitOrderUrl() {
        return jdSubmitOrderUrl;
    }

    public void setJdSubmitOrderUrl(String jdSubmitOrderUrl) {
        this.jdSubmitOrderUrl = jdSubmitOrderUrl;
    }

    public String getLySubmitOrderUrl() {
        return lySubmitOrderUrl;
    }

    public void setLySubmitOrderUrl(String lySubmitOrderUrl) {
        this.lySubmitOrderUrl = lySubmitOrderUrl;
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

    public String getScmExternalUrl() {
        return scmExternalUrl;
    }

    public void setScmExternalUrl(String scmExternalUrl) {
        this.scmExternalUrl = scmExternalUrl;
    }

    public String getJdSkuPriceUrl() {
        return jdSkuPriceUrl;
    }

    public void setJdSkuPriceUrl(String jdSkuPriceUrl) {
        this.jdSkuPriceUrl = jdSkuPriceUrl;
    }

    public String getCheckOrderDetailUrl() {
        return checkOrderDetailUrl;
    }

    public void setCheckOrderDetailUrl(String checkOrderDetailUrl) {
        this.checkOrderDetailUrl = checkOrderDetailUrl;
    }

    public String getTreadTypeUrl() {
        return treadTypeUrl;
    }

    public void setTreadTypeUrl(String treadTypeUrl) {
        this.treadTypeUrl = treadTypeUrl;
    }

    public String getJdAddressUrl() {
        return jdAddressUrl;
    }

    public void setJdAddressUrl(String jdAddressUrl) {
        this.jdAddressUrl = jdAddressUrl;
    }
}
