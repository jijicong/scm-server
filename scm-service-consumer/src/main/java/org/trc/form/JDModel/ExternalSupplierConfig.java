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
    //京东账户信息接口url地址
    private String jdBalanceInfoUrl;
    //京东订单对比明细分页查询接口url地址
    private String jdOrderDetailPageUrl;
    //京东余额变动明细分页查询接口url地址
    private String jdBalanceDetailPageUrl;
    //京东订单对比明细导出接口url地址
    private String jdExportOrderUrl;
    //京东余额变动明细导出接口url地址
    private String jdExportBalanceUrl;
    //京东订单对比明细操作接口url地址
    private String jdOrderOperateUrl;
    //京东余额明细统计接口url地址
    private String balancestatisticsUrl;
    //京东订单对比明细操作查询接口url地址
    private String jdOperateStateUrl;
    //粮油下单接口地址
    private String lySubmitOrderUrl;
    //京东报表补偿接口地址
    private String reportCompensateUrl;
    //京东订单状态反查接口地址
    private String jdOrderStatusUrl;

    /**
     * 商品同步接口地址
     */
    private String itemsSyncUrl;

    /**
     * 商品库存查询接口地址
     */
    private String inventoryQueryUrl;

    /**
     * 入库单创建接口URL
     */
    private String entryOrderCreateUrl;

    /**
     * 发货通知单创建接口URL
     */
    private String deliveryOrderCreateUrl;

    /**
     * 入库单详情接口URL
     */
    private String entryOrderDetailQueryUrl;

    /**
     * 出库单详情接口URL
     */
    private String deliveryOrderDetailQueryUrl;

    /**
     * 单据取消接口URL
     */
    private String orderCancelUrl;

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

    public String getJdBalanceInfoUrl() {
        return jdBalanceInfoUrl;
    }

    public void setJdBalanceInfoUrl(String jdBalanceInfoUrl) {
        this.jdBalanceInfoUrl = jdBalanceInfoUrl;
    }

    public String getJdOrderDetailPageUrl() {
        return jdOrderDetailPageUrl;
    }

    public void setJdOrderDetailPageUrl(String jdOrderDetailPageUrl) {
        this.jdOrderDetailPageUrl = jdOrderDetailPageUrl;
    }

    public String getJdBalanceDetailPageUrl() {
        return jdBalanceDetailPageUrl;
    }

    public void setJdBalanceDetailPageUrl(String jdBalanceDetailPageUrl) {
        this.jdBalanceDetailPageUrl = jdBalanceDetailPageUrl;
    }

    public String getJdExportOrderUrl() {
        return jdExportOrderUrl;
    }

    public void setJdExportOrderUrl(String jdExportOrderUrl) {
        this.jdExportOrderUrl = jdExportOrderUrl;
    }

    public String getJdExportBalanceUrl() {
        return jdExportBalanceUrl;
    }

    public void setJdExportBalanceUrl(String jdExportBalanceUrl) {
        this.jdExportBalanceUrl = jdExportBalanceUrl;
    }

    public String getJdOrderOperateUrl() {
        return jdOrderOperateUrl;
    }

    public void setJdOrderOperateUrl(String jdOrderOperateUrl) {
        this.jdOrderOperateUrl = jdOrderOperateUrl;
    }

    public String getJdOperateStateUrl() {
        return jdOperateStateUrl;
    }

    public void setJdOperateStateUrl(String jdOperateStateUrl) {
        this.jdOperateStateUrl = jdOperateStateUrl;
    }

    public String getBalancestatisticsUrl() {
        return balancestatisticsUrl;
    }

    public void setBalancestatisticsUrl(String balancestatisticsUrl) {
        this.balancestatisticsUrl = balancestatisticsUrl;
    }

    public String getReportCompensateUrl() {
        return reportCompensateUrl;
    }

    public void setReportCompensateUrl(String reportCompensateUrl) {
        this.reportCompensateUrl = reportCompensateUrl;
    }

    public String getJdOrderStatusUrl() {
        return jdOrderStatusUrl;
    }

    public void setJdOrderStatusUrl(String jdOrderStatusUrl) {
        this.jdOrderStatusUrl = jdOrderStatusUrl;
    }

    public String getItemsSyncUrl() {
        return itemsSyncUrl;
    }

    public void setItemsSyncUrl(String itemsSyncUrl) {
        this.itemsSyncUrl = itemsSyncUrl;
    }

    public String getInventoryQueryUrl() {
        return inventoryQueryUrl;
    }

    public void setInventoryQueryUrl(String inventoryQueryUrl) {
        this.inventoryQueryUrl = inventoryQueryUrl;
    }

    public String getEntryOrderCreateUrl() {
        return entryOrderCreateUrl;
    }

    public void setEntryOrderCreateUrl(String entryOrderCreateUrl) {
        this.entryOrderCreateUrl = entryOrderCreateUrl;
    }

    public String getDeliveryOrderCreateUrl() {
        return deliveryOrderCreateUrl;
    }

    public void setDeliveryOrderCreateUrl(String deliveryOrderCreateUrl) {
        this.deliveryOrderCreateUrl = deliveryOrderCreateUrl;
    }

    public String getEntryOrderDetailQueryUrl() {
        return entryOrderDetailQueryUrl;
    }

    public void setEntryOrderDetailQueryUrl(String entryOrderDetailQueryUrl) {
        this.entryOrderDetailQueryUrl = entryOrderDetailQueryUrl;
    }

    public String getDeliveryOrderDetailQueryUrl() {
        return deliveryOrderDetailQueryUrl;
    }

    public void setDeliveryOrderDetailQueryUrl(String deliveryOrderDetailQueryUrl) {
        this.deliveryOrderDetailQueryUrl = deliveryOrderDetailQueryUrl;
    }

    public String getOrderCancelUrl() {
        return orderCancelUrl;
    }

    public void setOrderCancelUrl(String orderCancelUrl) {
        this.orderCancelUrl = orderCancelUrl;
    }
}
