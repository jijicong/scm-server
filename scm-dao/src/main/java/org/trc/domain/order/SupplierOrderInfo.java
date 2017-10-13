package org.trc.domain.order;

import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class SupplierOrderInfo extends ScmDO{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String warehouseOrderCode;

    private String supplierOrderCode;

    private String supplierCode;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 供应商订单状态
     */
    private String supplierOrderStatus;

    /**
     * 下单结果信息
     */
    private String message;

    private String jdCityCode;

    private String jdDistrictCode;

    private String jdProvinceCode;

    private String jdTownCode;

    private String jdCity;

    private String jdDistrict;

    private String jdProvince;

    private String jdTown;

    private String skus;

    private String logisticsStatus;

    //老供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消
    private String oldSupplierOrderStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseOrderCode() {
        return warehouseOrderCode;
    }

    public void setWarehouseOrderCode(String warehouseOrderCode) {
        this.warehouseOrderCode = warehouseOrderCode == null ? null : warehouseOrderCode.trim();
    }

    public String getSupplierOrderCode() {
        return supplierOrderCode;
    }

    public void setSupplierOrderCode(String supplierOrderCode) {
        this.supplierOrderCode = supplierOrderCode == null ? null : supplierOrderCode.trim();
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getJdCityCode() {
        return jdCityCode;
    }

    public void setJdCityCode(String jdCityCode) {
        this.jdCityCode = jdCityCode == null ? null : jdCityCode.trim();
    }

    public String getJdDistrictCode() {
        return jdDistrictCode;
    }

    public void setJdDistrictCode(String jdDistrictCode) {
        this.jdDistrictCode = jdDistrictCode == null ? null : jdDistrictCode.trim();
    }

    public String getJdProvinceCode() {
        return jdProvinceCode;
    }

    public void setJdProvinceCode(String jdProvinceCode) {
        this.jdProvinceCode = jdProvinceCode == null ? null : jdProvinceCode.trim();
    }

    public String getJdTownCode() {
        return jdTownCode;
    }

    public void setJdTownCode(String jdTownCode) {
        this.jdTownCode = jdTownCode == null ? null : jdTownCode.trim();
    }

    public String getJdCity() {
        return jdCity;
    }

    public void setJdCity(String jdCity) {
        this.jdCity = jdCity == null ? null : jdCity.trim();
    }

    public String getJdDistrict() {
        return jdDistrict;
    }

    public void setJdDistrict(String jdDistrict) {
        this.jdDistrict = jdDistrict == null ? null : jdDistrict.trim();
    }

    public String getJdProvince() {
        return jdProvince;
    }

    public void setJdProvince(String jdProvince) {
        this.jdProvince = jdProvince == null ? null : jdProvince.trim();
    }

    public String getJdTown() {
        return jdTown;
    }

    public void setJdTown(String jdTown) {
        this.jdTown = jdTown == null ? null : jdTown.trim();
    }

    public String getSkus() {
        return skus;
    }

    public void setSkus(String skus) {
        this.skus = skus;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSupplierOrderStatus() {
        return supplierOrderStatus;
    }

    public void setSupplierOrderStatus(String supplierOrderStatus) {
        this.supplierOrderStatus = supplierOrderStatus;
    }

    public String getOldSupplierOrderStatus() {
        return oldSupplierOrderStatus;
    }

    public void setOldSupplierOrderStatus(String oldSupplierOrderStatus) {
        this.oldSupplierOrderStatus = oldSupplierOrderStatus;
    }
}