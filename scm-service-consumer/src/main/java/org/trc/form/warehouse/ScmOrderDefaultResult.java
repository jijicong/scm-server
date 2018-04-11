package org.trc.form.warehouse;

import java.util.List;

/**
 * Created by hzcyn on 2018/4/11.
 */
public class ScmOrderDefaultResult {

    /**
     * 开放平台出库单号
     */
    private String orderId;

    /**
     * 发货单号
     */
    private String orderCode;

    /**
     * 运单号(多条运单号以逗号隔开)
     */
    private String wayBill;

    /**
     * 物流公司编码
     */
    private String logisticsCode;

    /**
     * 物流公司名称
     */
    private String logisticsName;

    /**
     * 包裹信息集合
     */
    private List<ScmOrderPackage> scmOrderPackageList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getWayBill() {
        return wayBill;
    }

    public void setWayBill(String wayBill) {
        this.wayBill = wayBill;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public List<ScmOrderPackage> getScmOrderPackageList() {
        return scmOrderPackageList;
    }

    public void setScmOrderPackageList(List<ScmOrderPackage> scmOrderPackageList) {
        this.scmOrderPackageList = scmOrderPackageList;
    }
}
