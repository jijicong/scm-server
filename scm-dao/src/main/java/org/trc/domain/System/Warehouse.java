package org.trc.domain.System;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Warehouse manage
 * Created by sone on 2017/5/4.
 */
public class Warehouse extends BaseDO{

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @NotEmpty
    @Length(max = 32, message = "仓库编码字母和数字不能超过32个,汉字不能超过16个")
    private String code;
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "仓库名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("warehouseTypeCode")
    @NotEmpty
    @Length(max = 32, message = "仓库类型字母和数字不能超过32个,汉字不能超过16个")
    private String warehouseTypeCode;   //仓库类型 1.保税仓 2.海外仓 3.普通仓

    @FormParam("isCustomsClearance")
    private Integer isCustomsClearance; //是否支持清关 0.不支持 1. 支持  null.无   只用保税仓，才会有清关 ，其它为null

    @FormParam("province")
    @Length(max = 32, message = "仓库所在的省份字母和数字不能超过32个,汉字不能超过16个")
    private String province;
    @FormParam("city")
    @Length(max = 32, message = "仓库所在的城市字母和数字不能超过32个,汉字不能超过16个")
    private String city;
    @FormParam("area")
    @Length(max = 32, message = "仓库所在的地区字母和数字不能超过32个,汉字不能超过16个")
    private String area; //地区包括市下面的县和地区
    @FormParam("address")
    @Length(max = 256, message = "仓库所在的详细地址字母和数字不能超过256个,汉字不能超过128个")
    private String address;
    @FormParam("remark")
    @Length(max = 1024, message = "仓库的备注信息字母和数字不能超过1024个,汉字不能超过512个")
    private String remark;

    public Integer getIsCustomsClearance() {
        return isCustomsClearance;
    }

    public void setIsCustomsClearance(Integer isCustomsClearance) {
        this.isCustomsClearance = isCustomsClearance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarehouseTypeCode() {
        return warehouseTypeCode;
    }

    public void setWarehouseTypeCode(String warehouseTypeCode) {
        this.warehouseTypeCode = warehouseTypeCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
