package org.trc.form.warehouseInfo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzcyn on 2017/11/16.
 */
public class WarehouseItemInfoForm extends QueryModel {

    @QueryParam("skuCode")
    @Length(max = 32, message = "sku编号长度不能超过32个")
    private String skuCode;
    @QueryParam("itemName")
    @Length(max = 128, message = "商品名称长度不能超过128个")
    private String itemName;
    @QueryParam("noticeStatus")
    @Length(max = 2, message = "通知状态长度不能超过2个")
    private String noticeStatus;

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

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
