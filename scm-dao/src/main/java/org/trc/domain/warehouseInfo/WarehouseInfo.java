package org.trc.domain.warehouseInfo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * 仓储管理-仓库信息管理
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfo extends BaseDO {
    //主键
    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //仓库名称
    @FormParam("warehouseName")
    @Length(max = 64, message = "入库通知的编码字母和数字不能超过64个,汉字不能超过32个")
    private String warehouseName;

    //仓库类型
    @FormParam("type")
    private String type;

    @FormParam("isThroughQimen")

    private Integer isThroughQimen; // 是否通过奇门对接：0-不通过 1-通过

    @FormParam("isCustomsClearance")
    private Integer isCustomsClearance; //是否支持清关 0.不支持 1. 支持  null.无   只用保税仓，才会有清关 ，其它为null

    @FormParam("senderPhoneNumber")
    @Length(max = 16, message = "运单发件人手机号长度不能超过16个")
    private String senderPhoneNumber; // 运单发件人手机号

    @FormParam("warehouseContactNumber")
    @Length(max = 16, message = "仓库联系方式长度不能超过16个")
    private String warehouseContactNumber;// 仓库联系方式

    @FormParam("warehouseContact")
    @Length(max = 30, message = "仓库联系人字母和数字不能超过30个,汉字不能超过15个")
    private String warehouseContact; //仓库联系人

    //奇门仓库编码
    @FormParam("qimenWarehouseCode")
    private String qimenWarehouseCode;

    //sku数量
    @FormParam("skuNum")
    private Integer skuNum;

    //货主ID
    @FormParam("channelCode")
    private String channelCode;

    //仓库货主ID
    @FormParam("warehouseOwnerId")
    @Length(max = 50, message = "仓库货主ID的编码字母和数字不能超过50个,汉字不能超过25个")
    private String warehouseOwnerId;

    //货主名称
    @FormParam("ownerName")
    @Length(max = 50, message = "货主名称的编码字母和数字不能超过50个,汉字不能超过25个")
    private String ownerName;

    //货主仓库状态 0--待通知 1--通知成功 2--通知失败
    @FormParam("ownerWarehouseState")
    private String ownerWarehouseState;

    //备注
    @FormParam("code")
    private String code;

    //备注
    @FormParam("remark")
    @Length(max = 100, message = "仓库货主ID的编码字母和数字不能超过100个,汉字不能超过50个")
    private String remark;

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
    @FormParam("warehouseRemark")
    @Length(max = 1024, message = "仓库的备注信息字母和数字不能超过1024个,汉字不能超过512个")
    private String warehouseRemark;
    @Transient
    private String allAreaName;

    @FormParam("isNoticeSuccess")
    private Integer isNoticeSuccess;

    @FormParam("isNoticeWarehouseItems")
    private String isNoticeWarehouseItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQimenWarehouseCode() {
        return qimenWarehouseCode;
    }

    public void setQimenWarehouseCode(String qimenWarehouseCode) {
        this.qimenWarehouseCode = qimenWarehouseCode;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getWarehouseOwnerId() {
        return warehouseOwnerId;
    }

    public void setWarehouseOwnerId(String warehouseOwnerId) {
        this.warehouseOwnerId = warehouseOwnerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerWarehouseState() {
        return ownerWarehouseState;
    }

    public void setOwnerWarehouseState(String ownerWarehouseState) {
        this.ownerWarehouseState = ownerWarehouseState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIsCustomsClearance() {
        return isCustomsClearance;
    }

    public void setIsCustomsClearance(Integer isCustomsClearance) {
        this.isCustomsClearance = isCustomsClearance;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getWarehouseContactNumber() {
        return warehouseContactNumber;
    }

    public void setWarehouseContactNumber(String warehouseContactNumber) {
        this.warehouseContactNumber = warehouseContactNumber;
    }

    public String getWarehouseContact() {
        return warehouseContact;
    }

    public void setWarehouseContact(String warehouseContact) {
        this.warehouseContact = warehouseContact;
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

    public String getWarehouseRemark() {
        return warehouseRemark;
    }

    public void setWarehouseRemark(String warehouseRemark) {
        this.warehouseRemark = warehouseRemark;
    }

    public String getAllAreaName() {
        return allAreaName;
    }

    public void setAllAreaName(String allAreaName) {
        this.allAreaName = allAreaName;
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

    public Integer getIsThroughQimen() {
        return isThroughQimen;
    }

    public void setIsThroughQimen(Integer isThroughQimen) {
        this.isThroughQimen = isThroughQimen;
    }
}
