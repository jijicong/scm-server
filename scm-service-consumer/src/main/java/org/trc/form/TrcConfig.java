package org.trc.form;

/**
 * Created by hzwdx on 2017/7/7.
 */
public class TrcConfig {

    //接口访问key
    private String key;
    //品牌同步URL
    private String brandUrl;
    //属性同步URL
    private String propertyUrl;
    //分类同步URL
    private String categoryUrl;
    //分类品牌同步URL
    private String categoryBrandUrl;
    //分类属性同步URL
    private String categoryPropertyUrl;
    //商品同步URL
    private String itemUrl;
    //代发商品同步URL
    private String externalItemSkuUpdateUrl;
    //物流信息同步URL
    private String logisticsNotifyUrl;
    //订单下单结果同步URL
    private String orderSubmitNotifyUrl;
    //是否通知渠道,0-不通知,1-通知
    private String noticeChannal;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    public String getPropertyUrl() {
        return propertyUrl;
    }

    public void setPropertyUrl(String propertyUrl) {
        this.propertyUrl = propertyUrl;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getCategoryBrandUrl() {
        return categoryBrandUrl;
    }

    public void setCategoryBrandUrl(String categoryBrandUrl) {
        this.categoryBrandUrl = categoryBrandUrl;
    }

    public String getCategoryPropertyUrl() {
        return categoryPropertyUrl;
    }

    public void setCategoryPropertyUrl(String categoryPropertyUrl) {
        this.categoryPropertyUrl = categoryPropertyUrl;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getExternalItemSkuUpdateUrl() {
        return externalItemSkuUpdateUrl;
    }

    public void setExternalItemSkuUpdateUrl(String externalItemSkuUpdateUrl) {
        this.externalItemSkuUpdateUrl = externalItemSkuUpdateUrl;
    }

    public String getLogisticsNotifyUrl() {
        return logisticsNotifyUrl;
    }

    public void setLogisticsNotifyUrl(String logisticsNotifyUrl) {
        this.logisticsNotifyUrl = logisticsNotifyUrl;
    }

    public String getOrderSubmitNotifyUrl() {
        return orderSubmitNotifyUrl;
    }

    public void setOrderSubmitNotifyUrl(String orderSubmitNotifyUrl) {
        this.orderSubmitNotifyUrl = orderSubmitNotifyUrl;
    }

    public String getNoticeChannal() {
        return noticeChannal;
    }

    public void setNoticeChannal(String noticeChannal) {
        this.noticeChannal = noticeChannal;
    }
}
