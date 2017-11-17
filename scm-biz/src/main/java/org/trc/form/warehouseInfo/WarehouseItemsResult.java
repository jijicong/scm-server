package org.trc.form.warehouseInfo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import java.util.Date;

/**
 * Created by wangyz on 2017/11/17.
 */
public class WarehouseItemsResult {

    //sku编码
    private String skuCode;

    //商品名称
    private String itemName;

    //商品规格描述
    private String specNatureInfo;

    //商品状态
    private String isValid; //是否有效:0-否,1-是

    //仓库商品ID
    private String warehouseItemId;

    // 通知仓库状态:0-待通知,1-通知中,2-通知成功,3-通知失败,4-取消通知
    private String noticeStatus;

    // 更新时间
    private String updateTime;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getWarehouseItemId() {
        return warehouseItemId;
    }

    public void setWarehouseItemId(String warehouseItemId) {
        this.warehouseItemId = warehouseItemId;
    }

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
