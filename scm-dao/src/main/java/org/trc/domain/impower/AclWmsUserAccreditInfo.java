package org.trc.domain.impower;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;
import org.trc.domain.warehouseInfo.WarehouseInfo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.List;

public class AclWmsUserAccreditInfo extends BaseDO{

    /**
     * 主键ID
     */
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @FormParam("userId")
    private String userId;

    //必须已经存在用户中心的id
    @FormParam("phone")
    @NotEmpty(message = "用户手机号不能为空!")
    @Length(max = 64, message = "用户授权电话字母和数字不能超过64个,汉字不能超过32个")
    private String phone;

    @FormParam("name")
    @NotEmpty(message = "用户名称不能为空!")
    @Length(max = 20, message = "用户授权名称字母和数字不能超过20个,汉字不能超过10个")
    private String name;


    @FormParam("remark")
    @Length(max = 400, message = "用户授权名称字母和数字不能超过800个,汉字不能超过200个")
    private String remark;

    @Transient
    @FormParam("warehouseCode")
    private String warehouseCode;
    @Transient
    @FormParam("resourceCode")
    private String resourceCode;
    @Transient
    private String resourceName;
    @Transient
    private String warehouseName;
    @Transient
    private List<WmsResource> wmsResourceList;
    @Transient
    private List<WarehouseInfo> warehouseInfoList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public List<WmsResource> getWmsResourceList() {
        return wmsResourceList;
    }

    public void setWmsResourceList(List<WmsResource> wmsResourceList) {
        this.wmsResourceList = wmsResourceList;
    }

    public List<WarehouseInfo> getWarehouseInfoList() {
        return warehouseInfoList;
    }

    public void setWarehouseInfoList(List<WarehouseInfo> warehouseInfoList) {
        this.warehouseInfoList = warehouseInfoList;
    }
}
