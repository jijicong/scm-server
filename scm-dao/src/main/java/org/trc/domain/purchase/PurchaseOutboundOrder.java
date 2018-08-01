package org.trc.domain.purchase;

import io.swagger.annotations.ApiModelProperty;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.math.BigDecimal;
import java.util.List;

@Table(name = "purchase_outbound_order")
public class PurchaseOutboundOrder extends BaseDO {

    private static final long serialVersionUID = 6937654773349480215L;
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    private Long id;

    /**
     * 采购退货单编号
     */
    @ApiModelProperty("采购退货单编号")
    @Column(name = "purchase_outbound_order_code")
    @FormParam("purchaseOutboundOrderCode")
    private String purchaseOutboundOrderCode;

    /**
     * 采购渠道ID
     */
    @ApiModelProperty("采购渠道ID")
    @Column(name = "channel_id")
    @FormParam("channelId")
    private Long channelId;

    /**
     * 采购渠道编号
     */
    @ApiModelProperty("采购渠道编号")
    @Column(name = "channel_code")
    @FormParam("channelCode")
    private String channelCode;

    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    @Column(name = "supplier_id")
    @FormParam("supplierId")
    private Long supplierId;

    /**
     * 供应商编号
     */
    @ApiModelProperty("供应商编号")
    @Column(name = "supplier_code")
    @FormParam("supplierCode")
    private String supplierCode;

    /**
     * 采购合同ID
     */
    @ApiModelProperty("采购合同ID")
    @Column(name = "contract_id")
    @FormParam("contractId")
    private Long contractId;

    /**
     * 退货仓库ID
     */
    @ApiModelProperty("退货仓库ID")
    @Column(name = "warehouse_id")
    @FormParam("warehouseId")
    private String warehouseId;

    @ApiModelProperty("purchasePersonId")
    @Column(name = "purchase_person_id")
    @FormParam("purchasePersonId")
    private String purchasePersonId;

    /**
     * 退货仓库编号
     */
    @ApiModelProperty("退货仓库编号")
    @Column(name = "warehouse_code")
    @FormParam("warehouseCode")
    private String warehouseCode;

    /**
     * 状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废
     */
    @ApiModelProperty("状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废")
    @FormParam("status")
    private String status;

    /**
     * 对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    @ApiModelProperty("对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他")
    @Column(name = "outbound_status")
    @FormParam("outboundStatus")
    private String outboundStatus;

    /**
     * 退货总金额,单位/分
     */
    @ApiModelProperty("退货总金额,单位/分")
    @Column(name = "total_fee")
    @FormParam("totalFee")
    private BigDecimal totalFee;

    /**
     * 退货收货人
     */
    @ApiModelProperty("退货收货人")
    @FormParam("receiver")
    private String receiver;

    /**
     * 仓库信息主键
     */
    @ApiModelProperty("仓库信息主键")
    @Column(name = "warehouse_info_id")
    @FormParam("warehouseInfoId")
    private Long warehouseInfoId;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    @Column(name = "warehouse_name")
    @FormParam("warehouseName")
    private String warehouseName;

    /**
     * 退货人手机号
     */
    @ApiModelProperty("退货人手机号")
    @Column(name = "receiver_number")
    @FormParam("receiverNumber")
    private String receiverNumber;

    /**
     * 退货类型1-正品，2-残品
     */
    @ApiModelProperty("退货类型1-正品，2-残品")
    @Column(name = "return_order_type")
    @FormParam("returnOrderType")
    private String returnOrderType;

    /**
     * 提货方式1-到仓自提，2-京东配送，3-其他物流
     */
    @ApiModelProperty("提货方式1-到仓自提，2-京东配送，3-其他物流")
    @Column(name = "pick_type")
    @FormParam("pickType")
    private String pickType;

    /**
     * 退货说明
     */
    @ApiModelProperty("退货说明")
    @Column(name = "return_policy")
    @FormParam("returnPolicy")
    private String returnPolicy;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @FormParam("remark")
    private String remark;

    /**
     * 退货省份
     */
    @ApiModelProperty("退货省份")
    @Column(name = "receiver_province")
    @FormParam("receiverProvince")
    private String receiverProvince;

    /**
     * 退货地区
     */
    @ApiModelProperty("退货地区")
    @Column(name = "receiver_area")
    @FormParam("receiverArea")
    private String receiverArea;

    /**
     * 退货城市
     */
    @ApiModelProperty("退货城市")
    @Column(name = "receiver_city")
    @FormParam("receiverCity")
    private String receiverCity;

    /**
     * 退货详细地址
     */
    @ApiModelProperty("退货详细地址")
    @Column(name = "receiver_address")
    @FormParam("receiverAddress")
    private String receiverAddress;

    /**
     * 审核状态：1-提交审核,2-审核驳回,3-审核通过,
     */
    @ApiModelProperty("审核状态：1-提交审核,2-审核驳回,3-审核通过")
    @Column(name = "audit_status")
    @FormParam("auditStatus")
    private String auditStatus;

    /**
     * 审核意见
     */
    @ApiModelProperty("审核意见")
    @Column(name = "audit_opinion")
    @FormParam("auditOpinion")
    private String auditOpinion;

    /**
     * 提交审核说明
     */
    @ApiModelProperty("提交审核说明")
    @Column(name = "audit_description")
    @FormParam("auditDescription")
    private String auditDescription;

    /**
     * 审核人
     */
    @ApiModelProperty("提交审核说明")
    @Column(name = "audit_operator")
    @FormParam("auditOperator")
    private String auditOperator;


    @ApiModelProperty("供应商名称")
    @Transient
    @FormParam("supplierName")
    private String supplierName;

    @ApiModelProperty("采购退货单退货商品详情")
    @Transient
    @FormParam("purchaseOutboundDetailList")
    private List<PurchaseOutboundDetail> purchaseOutboundDetailList;

	public List<PurchaseOutboundDetail> getPurchaseOutboundDetailList() {
        return purchaseOutboundDetailList;
    }

    public void setPurchaseOutboundDetailList(List<PurchaseOutboundDetail> purchaseOutboundDetailList) {
        this.purchaseOutboundDetailList = purchaseOutboundDetailList;
    }

    ///**
    // * 是否有效:0-无效,1-有效
    //@Column(name = "is_valid")
    //private String isValid;
    //
    //*//**
    // * 是否删除:0-否,1-是
    // *//*
    //@Column(name = "is_deleted")
    //private String isDeleted;
    //
    //*//**
    // * 创建人
    // *//*
    //@Column(name = "create_operator")
    //private String createOperator;
    //
    //*//**
    // * 创建时间,格式yyyy-mm-dd hh:mi:ss
    // *//*
    //@Column(name = "create_time")
    //private Date createTime;
    //
    //*//**
    // * 最后更新时间,格式yyyy-mm-dd hh:mi:ss
    // *//*
    //@Column(name = "update_time")
    //private Date updateTime;*/

    /**
     * 获取ID
     *
     * @return id - ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取采购退货单编号
     *
     * @return purchase_outbound_order_code - 采购退货单编号
     */
    public String getPurchaseOutboundOrderCode() {
        return purchaseOutboundOrderCode;
    }

    /**
     * 设置采购退货单编号
     *
     * @param purchaseOutboundOrderCode 采购退货单编号
     */
    public void setPurchaseOutboundOrderCode(String purchaseOutboundOrderCode) {
        this.purchaseOutboundOrderCode = purchaseOutboundOrderCode;
    }

    /**
     * 获取采购渠道ID
     *
     * @return channel_id - 采购渠道ID
     */
    public Long getChannelId() {
        return channelId;
    }

    /**
     * 设置采购渠道ID
     *
     * @param channelId 采购渠道ID
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    /**
     * 获取采购渠道编号
     *
     * @return channel_code - 采购渠道编号
     */
    public String getChannelCode() {
        return channelCode;
    }

    /**
     * 设置采购渠道编号
     *
     * @param channelCode 采购渠道编号
     */
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    /**
     * 获取供应商ID
     *
     * @return supplier_id - 供应商ID
     */
    public Long getSupplierId() {
        return supplierId;
    }

    /**
     * 设置供应商ID
     *
     * @param supplierId 供应商ID
     */
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * 获取供应商编号
     *
     * @return supplier_code - 供应商编号
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * 设置供应商编号
     *
     * @param supplierCode 供应商编号
     */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * 获取采购合同ID
     *
     * @return contract_id - 采购合同ID
     */
    public Long getContractId() {
        return contractId;
    }

    /**
     * 设置采购合同ID
     *
     * @param contractId 采购合同ID
     */
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    /**
     * 获取退货仓库ID
     *
     * @return warehouse_id - 退货仓库ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * 设置退货仓库ID
     *
     * @param warehouseId 退货仓库ID
     */
    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * @return purchase_person_id
     */
    public String getPurchasePersonId() {
        return purchasePersonId;
    }

    /**
     * @param purchasePersonId
     */
    public void setPurchasePersonId(String purchasePersonId) {
        this.purchasePersonId = purchasePersonId;
    }

    /**
     * 获取退货仓库编号
     *
     * @return warehouse_code - 退货仓库编号
     */
    public String getWarehouseCode() {
        return warehouseCode;
    }

    /**
     * 设置退货仓库编号
     *
     * @param warehouseCode 退货仓库编号
     */
    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    /**
     * 获取状态:0-暂存,1-审核驳回,2-审核通过,3-提交审核,4-全部收货,5-收货异常,6-冻结,7-作废,8-出库通知
     *
     * @return status - 状态:0-暂存,1-审核驳回,2-审核通过,3-提交审核,4-全部收货,5-收货异常,6-冻结,7-作废,8-出库通知
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态:0-暂存,1-审核驳回,2-审核通过,3-提交审核,4-全部收货,5-收货异常,6-冻结,7-作废,8-出库通知
     *
     * @param status 状态:0-暂存,1-审核驳回,2-审核通过,3-提交审核,4-全部收货,5-收货异常,6-冻结,7-作废,8-出库通知
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     *
     * @return outbound_status - 对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    public String getOutboundStatus() {
        return outboundStatus;
    }

    /**
     * 设置对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     *
     * @param outboundStatus 对应出库单状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    public void setOutboundStatus(String outboundStatus) {
        this.outboundStatus = outboundStatus;
    }

    /**
     * 获取退货总金额,单位/分
     *
     * @return total_fee - 退货总金额,单位/分
     */
    public BigDecimal getTotalFee() {
        return totalFee;
    }

    /**
     * 设置退货总金额,单位/分
     *
     * @param totalFee 退货总金额,单位/分
     */
    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * 获取退货收货人
     *
     * @return receiver - 退货收货人
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * 设置退货收货人
     *
     * @param receiver 退货收货人
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取仓库信息主键
     *
     * @return warehouse_info_id - 仓库信息主键
     */
    public Long getWarehouseInfoId() {
        return warehouseInfoId;
    }

    /**
     * 设置仓库信息主键
     *
     * @param warehouseInfoId 仓库信息主键
     */
    public void setWarehouseInfoId(Long warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }

    /**
     * 获取仓库名称
     *
     * @return warehouse_name - 仓库名称
     */
    public String getWarehouseName() {
        return warehouseName;
    }

    /**
     * 设置仓库名称
     *
     * @param warehouseName 仓库名称
     */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    /**
     * 获取退货人手机号
     *
     * @return receiver_number - 退货人手机号
     */
    public String getReceiverNumber() {
        return receiverNumber;
    }

    /**
     * 设置退货人手机号
     *
     * @param receiverNumber 退货人手机号
     */
    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    /**
     * 获取退货类型1-正品，2-残品
     *
     * @return return_order_type - 退货类型1-正品，2-残品
     */
    public String getReturnOrderType() {
        return returnOrderType;
    }

    /**
     * 设置退货类型1-正品，2-残品
     *
     * @param returnOrderType 退货类型1-正品，2-残品
     */
    public void setReturnOrderType(String returnOrderType) {
        this.returnOrderType = returnOrderType;
    }

    /**
     * 获取提货方式1-到仓自提，2-京东配送，3-其他物流
     *
     * @return pick_type - 提货方式1-到仓自提，2-京东配送，3-其他物流
     */
    public String getPickType() {
        return pickType;
    }

    /**
     * 设置提货方式1-到仓自提，2-京东配送，3-其他物流
     *
     * @param pickType 提货方式1-到仓自提，2-京东配送，3-其他物流
     */
    public void setPickType(String pickType) {
        this.pickType = pickType;
    }

    /**
     * 获取退货说明
     *
     * @return return_policy - 退货说明
     */
    public String getReturnPolicy() {
        return returnPolicy;
    }

    /**
     * 设置退货说明
     *
     * @param returnPolicy 退货说明
     */
    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取退货省份
     *
     * @return receiver_province - 退货省份
     */
    public String getReceiverProvince() {
        return receiverProvince;
    }

    /**
     * 设置退货省份
     *
     * @param receiverProvince 退货省份
     */
    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    /**
     * 获取退货城市
     *
     * @return receiver_city - 退货城市
     */
    public String getReceiverCity() {
        return receiverCity;
    }

    /**
     * 设置退货城市
     *
     * @param receiverCity 退货城市
     */
    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    /**
     * 获取退货详细地址
     *
     * @return receiver_address - 退货详细地址
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * 设置退货详细地址
     *
     * @param receiverAddress 退货详细地址
     */
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getReceiverArea() {
        return receiverArea;
    }

    public void setReceiverArea(String receiverArea) {
        this.receiverArea = receiverArea;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription;
    }

    public String getAuditOperator() {
        return auditOperator;
    }

    public void setAuditOperator(String auditOperator) {
        this.auditOperator = auditOperator;
    }

///**
    // * 获取是否有效:0-无效,1-有效
    // *
    // * @return is_valid - 是否有效:0-无效,1-有效
    // */
    //public String getIsValid() {
    //    return isValid;
    //}
    //
    ///**
    // * 设置是否有效:0-无效,1-有效
    // *
    // * @param isValid 是否有效:0-无效,1-有效
    // */
    //public void setIsValid(String isValid) {
    //    this.isValid = isValid;
    //}
    //
    ///**
    // * 获取是否删除:0-否,1-是
    // *
    // * @return is_deleted - 是否删除:0-否,1-是
    // */
    //public String getIsDeleted() {
    //    return isDeleted;
    //}
    //
    ///**
    // * 设置是否删除:0-否,1-是
    // *
    // * @param isDeleted 是否删除:0-否,1-是
    // */
    //public void setIsDeleted(String isDeleted) {
    //    this.isDeleted = isDeleted;
    //}
    //
    ///**
    // * 获取创建人
    // *
    // * @return create_operator - 创建人
    // */
    //public String getCreateOperator() {
    //    return createOperator;
    //}
    //
    ///**
    // * 设置创建人
    // *
    // * @param createOperator 创建人
    // */
    //public void setCreateOperator(String createOperator) {
    //    this.createOperator = createOperator;
    //}
    //
    ///**
    // * 获取创建时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @return create_time - 创建时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public Date getCreateTime() {
    //    return createTime;
    //}
    //
    ///**
    // * 设置创建时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @param createTime 创建时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public void setCreateTime(Date createTime) {
    //    this.createTime = createTime;
    //}
    //
    ///**
    // * 获取最后更新时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @return update_time - 最后更新时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public Date getUpdateTime() {
    //    return updateTime;
    //}
    //
    ///**
    // * 设置最后更新时间,格式yyyy-mm-dd hh:mi:ss
    // *
    // * @param updateTime 最后更新时间,格式yyyy-mm-dd hh:mi:ss
    // */
    //public void setUpdateTime(Date updateTime) {
    //    this.updateTime = updateTime;
    //}
}