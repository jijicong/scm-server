package org.trc.domain.supplier;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class Supplier extends BaseDO{

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("supplierCode")
    //@NotEmpty
    @Length(max = 32, message = "供应商编码长度不能超过32个")
    private String supplierCode;
    @FormParam("supplierName")
    @NotEmpty
    @Length(max = 64, message = "供应商名称长度不能超过64个")
    private String supplierName;
    @FormParam("supplierKindCode")
    @NotEmpty
    @Length(max = 32, message = "供应商性质编码长度不能超过32个")
    private String supplierKindCode;
    @FormParam("supplierTypeCode")
    @NotEmpty
    @Length(max = 32, message = "供应商类型长度不能超过32个")
    private String supplierTypeCode;
    @FormParam("phone")
    @NotEmpty
    @Length(max = 16, message = "联系电话长度不能超过16个")
    private String phone;
    @FormParam("mobile")
    @Length(max = 16, message = "手机号码长度不能超过16个")
    private String mobile;
    @FormParam("weixin")
    @Length(max = 32, message = "微信号长度不能超过32个")
    private String weixin;
    @FormParam("qq")
    @Length(max = 32, message = "QQ号长度不能超过32个")
    private String qq;
    @FormParam("dingding")
    @Length(max = 32, message = "钉钉号长度不能超过32个")
    private String dingding;
    @FormParam("country")
    @Length(max = 32, message = "国家编码长度不能超过32个")
    private String country;
    @FormParam("province")
    @Length(max = 32, message = "省编码长度不能超过32个")
    private String province;
    @FormParam("city")
    @Length(max = 32, message = "城市市编码长度不能超过32个")
    private String city;
    @FormParam("Area")
    @Length(max = 32, message = "区编码长度不能超过32个")
    private String area;
    @FormParam("address")
    @NotEmpty
    @Length(max = 256, message = "详细地址长度不能超过32个")
    private String address;
    @FormParam("certificateTypeId")
    //@NotEmpty
    @Length(max = 32, message = "证件类型编码长度不能超过32个")
    private String certificateTypeId;
    @FormParam("remark")
    @Length(max = 1024, message = "备注长度不能超过1024个")
    private String remark;
    @FormParam("contact")
    @NotEmpty
    @Length(max = 64, message = "联系人长度不能超过64个")
    private String contact;
    @Transient
    @FormParam("channel")
    @NotEmpty
    private String channel;
    @FormParam("supplierInterfaceId")
    @Length(max = 32, message = "供应商接口ID长度不能超过32个")
    private String supplierInterfaceId;

    @Transient
    private String channelName;//供应商渠道名称
    @Transient
    private String brandName;//供应商品牌名称
    @Transient
    private String highContact;//供应商品牌名称

    public String getHighContact() {
        return highContact;
    }

    public void setHighContact(String highContact) {
        this.highContact = highContact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode == null ? null : supplierCode.trim();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public String getSupplierKindCode() {
        return supplierKindCode;
    }

    public void setSupplierKindCode(String supplierKindCode) {
        this.supplierKindCode = supplierKindCode == null ? null : supplierKindCode.trim();
    }

    public String getSupplierTypeCode() {
        return supplierTypeCode;
    }

    public void setSupplierTypeCode(String supplierTypeCode) {
        this.supplierTypeCode = supplierTypeCode == null ? null : supplierTypeCode.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin == null ? null : weixin.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getDingding() {
        return dingding;
    }

    public void setDingding(String dingding) {
        this.dingding = dingding == null ? null : dingding.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getCertificateTypeId() {
        return certificateTypeId;
    }

    public void setCertificateTypeId(String certificateTypeId) {
        this.certificateTypeId = certificateTypeId == null ? null : certificateTypeId.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSupplierInterfaceId() {
        return supplierInterfaceId;
    }

    public void setSupplierInterfaceId(String supplierInterfaceId) {
        this.supplierInterfaceId = supplierInterfaceId;
    }
}