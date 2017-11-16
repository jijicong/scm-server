package org.trc.domain.warehouseInfo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * 仓储管理-仓库信息管理
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfo {
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

    //奇门仓库编码
    @FormParam("qimenWarehouseCode")
    private String qimenWarehouseCode;

    //sku数量
    @FormParam("skuNum")
    private Integer skuNum;

    //货主ID
    @FormParam("ownerId")
    private String ownerId;

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

    //创建时间
    @FormParam("createTime")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    //更新时间
    @FormParam("updateTime")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    //是否删除 0--未删除 1--已删除
    @FormParam("isDelete")
    private Integer isDelete;

    //备注
    @FormParam("warehouseOwnerId")
    @Length(max = 100, message = "仓库货主ID的编码字母和数字不能超过100个,汉字不能超过50个")
    private String remark;

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
