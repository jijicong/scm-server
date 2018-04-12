package org.trc.domain.order;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;
import org.trc.custom.MoneySerializer;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by hzcyn on 2017/12/1.
 */
@Table(name = "outbound_detail")
public class OutboundDetail implements Serializable {
	
	private static final long serialVersionUID = 5954865299939357741L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbound_order_code")
    private String outboundOrderCode;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "actual_amount")
    @JsonSerialize(using = MoneySerializer.class)
    private Long actualAmount;

    @Column(name = "should_sent_item_num")
    private Long shouldSentItemNum;

    @Column(name = "real_sent_item_num")
    private Long realSentItemNum;

    @Column(name = "inventory_type")
    private String inventoryType;//ZP---正品

    @Column(name = "status")
    private String status;

    @Transient
    private List<OutboundDetailLogistics> outboundDetailLogisticsList;

    @Column(name = "create_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    @Column(name = "update_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    @Column(name = "spec_nature_info")
    private String specNatureInfo;

    @Column(name = "warehouse_item_id")
    private String warehouseItemId;

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

    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    public String getWarehouseItemId() {
        return warehouseItemId;
    }

    public void setWarehouseItemId(String warehouseItemId) {
        this.warehouseItemId = warehouseItemId;
    }
}
