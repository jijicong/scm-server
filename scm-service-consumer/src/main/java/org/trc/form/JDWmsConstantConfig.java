package org.trc.form;

/**
 * 京东仓库常量配置
 */
public class JDWmsConstantConfig {

    /**
     * 事业部编码
     */
    private String deptNo;

    /**
     * 开放平台库房编号
     */
    private String warehouseNo;

    /**
     * 供应商编号
     */
    private String supplierNo;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * isv编号
     */
    private String isvSource;

    /**
     * 京东三级分类编号  产品暂定 个护化妆-香水彩妆-腮红
     */
    private String thirdCategoryNo;

    /**
     * 销售平台编号 1-京东---暂时不用 6-其他
     */
    private String salePlatformSource;

    /**
     * 承运商编号，默认为京东快递
     */
    private String shipperNo;

    /**
     * 订单标记位
     */
    private String orderMark;

    /**
     * 商品状态
     */
    private String goodStatus;

    /**
     * 入库保质期阈值
     */
    private String instoreThreshold;

    /**
     *出库保质期阈值
     */
    private String outstoreThreshold;

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getIsvSource() {
        return isvSource;
    }

    public void setIsvSource(String isvSource) {
        this.isvSource = isvSource;
    }

    public String getThirdCategoryNo() {
        return thirdCategoryNo;
    }

    public void setThirdCategoryNo(String thirdCategoryNo) {
        this.thirdCategoryNo = thirdCategoryNo;
    }

    public String getSalePlatformSource() {
        return salePlatformSource;
    }

    public void setSalePlatformSource(String salePlatformSource) {
        this.salePlatformSource = salePlatformSource;
    }

    public String getShipperNo() {
        return shipperNo;
    }

    public void setShipperNo(String shipperNo) {
        this.shipperNo = shipperNo;
    }

    public String getOrderMark() {
        return orderMark;
    }

    public void setOrderMark(String orderMark) {
        this.orderMark = orderMark;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public String getInstoreThreshold() {
        return instoreThreshold;
    }

    public void setInstoreThreshold(String instoreThreshold) {
        this.instoreThreshold = instoreThreshold;
    }

    public String getOutstoreThreshold() {
        return outstoreThreshold;
    }

    public void setOutstoreThreshold(String outstoreThreshold) {
        this.outstoreThreshold = outstoreThreshold;
    }
}
