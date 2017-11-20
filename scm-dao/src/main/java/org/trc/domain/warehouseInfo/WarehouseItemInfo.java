package org.trc.domain.warehouseInfo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzcyn on 2017/11/16.
 * @author hzcyn
 */
public class WarehouseItemInfo implements Serializable{

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 仓库信息id
     */
    private Long warehouseInfoId;
    //sku编码
    private String skuCode;
    //仓库商品id
    private String warehouseItemId;
    //商品货号
    private String itemNo;
    //商品名称
    private String itemName;
    //条形码
    private String barCode;
    //商品规格描述
    private String specNatureInfo;
    //商品类型:“ZC”，即正常商品
    private String itemType;
    // 通知仓库状态:0-待通知,1-通知中,2-通知成功,3-通知失败,4-取消通知
    private Integer noticeStatus;
    //创建时间,格式yyyy-mm-dd hh:mi:ss
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    // 更新时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
    //是否删除  0--未删除 1--已删除
    private Integer isDelete;
    //失败原因
    private String exceptionReason;
    //商品状态:1-停用,2-启用
    private Integer isValid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(Long warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getWarehouseItemId() {
        return warehouseItemId;
    }

    public void setWarehouseItemId(String warehouseItemId) {
        this.warehouseItemId = warehouseItemId;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(Integer noticeStatus) {
        this.noticeStatus = noticeStatus;
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

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "WarehouseItemInfo{" +
                "id=" + id +
                ", warehouseInfoId=" + warehouseInfoId +
                ", skuCode='" + skuCode + '\'' +
                ", warehouseItemId='" + warehouseItemId + '\'' +
                ", itemNo='" + itemNo + '\'' +
                ", itemName='" + itemName + '\'' +
                ", barCode='" + barCode + '\'' +
                ", specNatureInfo='" + specNatureInfo + '\'' +
                ", itemType='" + itemType + '\'' +
                ", noticeStatus=" + noticeStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", exceptionReason='" + exceptionReason + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
