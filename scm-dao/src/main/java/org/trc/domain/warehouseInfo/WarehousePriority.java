package org.trc.domain.warehouseInfo;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 仓库管理-仓库匹配优先级管理
 * Created by wangyz on 2017/11/15.
 */
public class WarehousePriority extends ScmDO {
    //主键
    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //仓库编码
    @FormParam("warehouseCode")
    @Length(max = 32, message = "仓库编码不能超过32个")
    private String warehouseCode;

    //wms仓库编码
    @Transient
    private String wmsWarehouseCode;

    @FormParam("priority")
    private Integer priority; // 优先级,从1开始，数字越小优先级越大

    //仓库名称
    @Transient
    private String warehouseName;

    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    private String isValid; //是否有效:0-否,1-是

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWmsWarehouseCode() {
        return wmsWarehouseCode;
    }

    public void setWmsWarehouseCode(String wmsWarehouseCode) {
        this.wmsWarehouseCode = wmsWarehouseCode;
    }
}
