package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hzcyn on 2017/12/1.
 */
public class OutboundDetailLogistics implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long outboundDetailId;

    private String logisticsCorporation;

    private String logisticsCode;

    private String waybillNumber;

    private Long itemNum;

    private String logisticsStatus;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date deliverTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOutboundDetailId() {
        return outboundDetailId;
    }

    public void setOutboundDetailId(Long outboundDetailId) {
        this.outboundDetailId = outboundDetailId;
    }

    public String getLogisticsCorporation() {
        return logisticsCorporation;
    }

    public void setLogisticsCorporation(String logisticsCorporation) {
        this.logisticsCorporation = logisticsCorporation;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public Long getItemNum() {
        return itemNum;
    }

    public void setItemNum(Long itemNum) {
        this.itemNum = itemNum;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public Date getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Date deliverTime) {
        this.deliverTime = deliverTime;
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
}
