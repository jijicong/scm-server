package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by hzcyn on 2017/12/1.
 */
public class OutboundDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String outboundOrderCode;

    private String skuName;

    private String skuCode;

    private Long actualAmount;

    private Long shouldSentItemNum;

    private Long realSentItemNum;

    private String inventoryType;//ZP---正品

    private String status;

    @Transient
    private List<OutboundDetailLogistics> outboundDetailLogisticsList;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    public List<OutboundDetailLogistics> getOutboundDetailLogisticsList() {
        return outboundDetailLogisticsList;
    }

    public void setOutboundDetailLogisticsList(List<OutboundDetailLogistics> outboundDetailLogisticsList) {
        this.outboundDetailLogisticsList = outboundDetailLogisticsList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(String outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Long actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Long getShouldSentItemNum() {
        return shouldSentItemNum;
    }

    public void setShouldSentItemNum(Long shouldSentItemNum) {
        this.shouldSentItemNum = shouldSentItemNum;
    }

    public Long getRealSentItemNum() {
        return realSentItemNum;
    }

    public void setRealSentItemNum(Long realSentItemNum) {
        this.realSentItemNum = realSentItemNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }
}
