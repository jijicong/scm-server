package org.trc.domain.System;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Warehouse manage
 * Created by sone on 2017/5/4.
 */
public class Warehouse extends BaseDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @Length(max = 32, message = "仓库编码字母和数字不能超过32个,汉字不能超过16个")
    private String code;
    @FormParam("name")
    @Length(max = 64, message = "仓库名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("warehouseTypeCode")
    @Length(max = 32, message = "仓库类型字母和数字不能超过32个,汉字不能超过16个")
    private String warehouseTypeCode;   //仓库类型 1.保税仓 2.海外仓 3.普通仓

    @FormParam("isCustomsClearance")
    private Integer isCustomsClearance; //是否支持清关 0.不支持 1. 支持  null.无   只用保税仓，才会有清关 ，其它为null

    @FormParam("isThroughQimen")
    private Integer isThroughQimen; // 是否通过奇门对接：0-不通过 1-通过

    @FormParam("qimenWarehouseCode")
    @Length(max = 50, message = "奇门仓库编码字母和数字不能超过50个")
    private String qimenWarehouseCode; // 奇门仓库编码

    @FormParam("warehouseContact")
    @Length(max = 30, message = "仓库联系人字母和数字不能超过30个,汉字不能超过15个")
    private String warehouseContact; //仓库联系人

    @FormParam("warehouseContactNumber")
    @Length(max = 16, message = "仓库联系方式长度不能超过16个")
    private String warehouseContactNumber;// 仓库联系方式

    @FormParam("senderPhoneNumber")
    @Length(max = 16, message = "运单发件人手机号长度不能超过16个")
    private String senderPhoneNumber; // 运单发件人手机号

    @FormParam("isNoticeSuccess")
    private Integer isNoticeSuccess;

    @FormParam("isNoticeWarehouseItems")
    private String isNoticeWarehouseItems;

    public void setQimenWarehouseCode(String qimenWarehouseCode) {
        this.qimenWarehouseCode = qimenWarehouseCode;
    }

    public void setWarehouseContact(String warehouseContact) {
        this.warehouseContact = warehouseContact;
    }

    public void setWarehouseContactNumber(String warehouseContactNumber) {
        this.warehouseContactNumber = warehouseContactNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getQimenWarehouseCode() {

        return qimenWarehouseCode;
    }

    public String getWarehouseContact() {
        return warehouseContact;
    }

    public String getWarehouseContactNumber() {
        return warehouseContactNumber;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

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
    @Transient
    private String allAreaName;

    public Integer getIsCustomsClearance() {
        return isCustomsClearance;
    }

    public void setIsCustomsClearance(Integer isCustomsClearance) {
        this.isCustomsClearance = isCustomsClearance;
    }

    public String getAllAreaName() {
        return allAreaName;
    }

    public void setAllAreaName(String allAreaName) {
        this.allAreaName = allAreaName;
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

    public void setIsThroughQimen(Integer isThroughQimen) {
        this.isThroughQimen = isThroughQimen;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", warehouseTypeCode='" + warehouseTypeCode + '\'' +
                ", isCustomsClearance=" + isCustomsClearance +
                ", isThroughQimen=" + isThroughQimen +
                ", qimenWarehouseCode='" + qimenWarehouseCode + '\'' +
                ", warehouseContact='" + warehouseContact + '\'' +
                ", warehouseContactNumber='" + warehouseContactNumber + '\'' +
                ", senderPhoneNumber='" + senderPhoneNumber + '\'' +
                ", isNoticeSuccess=" + isNoticeSuccess +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", remark='" + remark + '\'' +
                ", allAreaName='" + allAreaName + '\'' +
                '}';
    }

    public Integer getIsThroughQimen() {

        return isThroughQimen;
    }

    public Integer getIsNoticeSuccess() {
        return isNoticeSuccess;
    }

    public void setIsNoticeSuccess(Integer isNoticeSuccess) {
        this.isNoticeSuccess = isNoticeSuccess;
    }

    public String getIsNoticeWarehouseItems() {
        return isNoticeWarehouseItems;
    }

    public void setIsNoticeWarehouseItems(String isNoticeWarehouseItems) {
        this.isNoticeWarehouseItems = isNoticeWarehouseItems;
    }
}
