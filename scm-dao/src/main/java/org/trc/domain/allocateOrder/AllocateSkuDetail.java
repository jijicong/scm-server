package org.trc.domain.allocateOrder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "allocate_sku_detail")
public class AllocateSkuDetail {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 采购单编号
     */
    @Column(name = "allocate_order_code")
    private String allocateOrderCode;

    /**
     * 调拨管理出库详情状态0-等待出库1-出库完成2-出库异常
     */
    @Column(name = "allocate_out_status")
    private String allocateOutStatus;

    /**
     * 调拨管理入库详情状态0-等待入库1-入库完成2-入库异常
     */
    @Column(name = "allocate_in_status")
    private String allocateInStatus;

    @Column(name = "old_out_status")
    private String oldOutStatus;

    @Column(name = "old_in_status")
    private String oldInStatus;

    /**
     * sku名称
     */
    @Column(name = "sku_name")
    private String skuName;

    /**
     * 商品sku编码
     */
    @Column(name = "sku_code")
    private String skuCode;

    /**
     * 规格
     */
    @Column(name = "spec_nature_info")
    private String specNatureInfo;

    /**
     * 条形码
     */
    @Column(name = "bar_code")
    private String barCode;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code")
    private String brandCode;

    /**
     * 品牌名称
     */
    @Column(name = "brand_name")
    private String brandName;

    /**
     * 调拨库存类型:1-正品,2-残次品
     */
    @Column(name = "inventory_type")
    private String inventoryType;

    /**
     * 计划调拨数量
     */
    @Column(name = "plan_allocate_num")
    private Long planAllocateNum;

    /**
     * 实际出库数量
     */
    @Column(name = "real_out_num")
    private Long realOutNum;

    /**
     * 出库状态0-初始,1-等待出库,2-出库完成,3-出库异常
     */
    @Column(name = "out_status")
    private String outStatus;

    /**
     * 实际入库总量
     */
    @Column(name = "real_in_num")
    private Long realInNum;

    /**
     * 正品入库数量
     */
    @Column(name = "nornal_in_num")
    private Long nornalInNum;

    /**
     * 残品入库数量
     */
    @Column(name = "defect_in_num")
    private Long defectInNum;

    /**
     * 入库状态0-初始,1-等待入库,2-入库完成,3-入库异常
     */
    @Column(name = "in_status")
    private String inStatus;
    
    /**
     * 商品货号
     */
    @Column(name = "sku_no")
    private String skuNo;

    @Column(name = "create_operator")
    private String createOperator;

    /**
     * 是否删除:0-否,1-是
     */
    @Column(name = "is_deleted")
    private String isDeleted;

    /**
     * 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
    
    @Transient
    private String  allCategoryName;
    
    /**
     * 实时库存
     */
    @Transient
    private Long inventoryNum;
    
    public Long getInventoryNum() {
		return inventoryNum;
	}

	public void setInventoryNum(Long inventoryNum) {
		this.inventoryNum = inventoryNum;
	}

	public String getAllCategoryName() {
		return allCategoryName;
	}

	public void setAllCategoryName(String allCategoryName) {
		this.allCategoryName = allCategoryName;
	}

	/**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取采购单编号
     *
     * @return allocate_order_code - 采购单编号
     */
    public String getAllocateOrderCode() {
        return allocateOrderCode;
    }

    /**
     * 设置采购单编号
     *
     * @param allocateOrderCode 采购单编号
     */
    public void setAllocateOrderCode(String allocateOrderCode) {
        this.allocateOrderCode = allocateOrderCode;
    }

    /**
     * 获取sku名称
     *
     * @return sku_name - sku名称
     */
    public String getSkuName() {
        return skuName;
    }

    /**
     * 设置sku名称
     *
     * @param skuName sku名称
     */
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    /**
     * 获取商品sku编码
     *
     * @return sku_code - 商品sku编码
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * 设置商品sku编码
     *
     * @param skuCode 商品sku编码
     */
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    /**
     * 获取规格
     *
     * @return spec_nature_info - 规格
     */
    public String getSpecNatureInfo() {
        return specNatureInfo;
    }

    /**
     * 设置规格
     *
     * @param specNatureInfo 规格
     */
    public void setSpecNatureInfo(String specNatureInfo) {
        this.specNatureInfo = specNatureInfo;
    }

    /**
     * 获取条形码
     *
     * @return bar_code - 条形码
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * 设置条形码
     *
     * @param barCode 条形码
     */
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    /**
     * 获取品牌编码
     *
     * @return brand_code - 品牌编码
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * 设置品牌编码
     *
     * @param brandCode 品牌编码
     */
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    /**
     * 获取品牌名称
     *
     * @return brand_name - 品牌名称
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * 设置品牌名称
     *
     * @param brandName 品牌名称
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * 获取调拨库存类型:1-正品,2-残次品
     *
     * @return inventory_type - 调拨库存类型:1-正品,2-残次品
     */
    public String getInventoryType() {
        return inventoryType;
    }

    /**
     * 设置调拨库存类型:1-正品,2-残次品
     *
     * @param inventoryType 调拨库存类型:1-正品,2-残次品
     */
    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    /**
     * 获取计划调拨数量
     *
     * @return plan_allocate_num - 计划调拨数量
     */
    public Long getPlanAllocateNum() {
        return planAllocateNum;
    }

    /**
     * 设置计划调拨数量
     *
     * @param planAllocateNum 计划调拨数量
     */
    public void setPlanAllocateNum(Long planAllocateNum) {
        this.planAllocateNum = planAllocateNum;
    }

    /**
     * 获取实际出库数量
     *
     * @return real_out_num - 实际出库数量
     */
    public Long getRealOutNum() {
        return realOutNum;
    }

    /**
     * 设置实际出库数量
     *
     * @param realOutNum 实际出库数量
     */
    public void setRealOutNum(Long realOutNum) {
        this.realOutNum = realOutNum;
    }

    /**
     * 获取出库状态0-初始,1-等待出库,2-出库完成,3-出库异常
     *
     * @return out_status - 出库状态0-初始,1-等待出库,2-出库完成,3-出库异常
     */
    public String getOutStatus() {
        return outStatus;
    }

    /**
     * 设置出库状态0-初始,1-等待出库,2-出库完成,3-出库异常
     *
     * @param outStatus 出库状态0-初始,1-等待出库,2-出库完成,3-出库异常
     */
    public void setOutStatus(String outStatus) {
        this.outStatus = outStatus;
    }

    /**
     * 获取实际入库总量
     *
     * @return real_in_num - 实际入库总量
     */
    public Long getRealInNum() {
        return realInNum;
    }

    /**
     * 设置实际入库总量
     *
     * @param realInNum 实际入库总量
     */
    public void setRealInNum(Long realInNum) {
        this.realInNum = realInNum;
    }

    /**
     * 获取正品入库数量
     *
     * @return nornal_in_num - 正品入库数量
     */
    public Long getNornalInNum() {
        return nornalInNum;
    }

    /**
     * 设置正品入库数量
     *
     * @param nornalInNum 正品入库数量
     */
    public void setNornalInNum(Long nornalInNum) {
        this.nornalInNum = nornalInNum;
    }

    /**
     * 获取残品入库数量
     *
     * @return defect_in_num - 残品入库数量
     */
    public Long getDefectInNum() {
        return defectInNum;
    }

    /**
     * 设置残品入库数量
     *
     * @param defectInNum 残品入库数量
     */
    public void setDefectInNum(Long defectInNum) {
        this.defectInNum = defectInNum;
    }

    /**
     * 获取入库状态0-初始,1-等待入库,2-入库完成,3-入库异常
     *
     * @return in_status - 入库状态0-初始,1-等待入库,2-入库完成,3-入库异常
     */
    public String getInStatus() {
        return inStatus;
    }

    /**
     * 设置入库状态0-初始,1-等待入库,2-入库完成,3-入库异常
     *
     * @param inStatus 入库状态0-初始,1-等待入库,2-入库完成,3-入库异常
     */
    public void setInStatus(String inStatus) {
        this.inStatus = inStatus;
    }

    /**
     * @return create_operator
     */
    public String getCreateOperator() {
        return createOperator;
    }

    /**
     * @param createOperator
     */
    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    /**
     * 获取是否删除:0-否,1-是
     *
     * @return is_deleted - 是否删除:0-否,1-是
     */
    public String getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除:0-否,1-是
     *
     * @param isDeleted 是否删除:0-否,1-是
     */
    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 获取创建时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @return create_time - 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间,格式yyyy-mm-dd hh:mi:ss
     *
     * @param createTime 创建时间,格式yyyy-mm-dd hh:mi:ss
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAllocateOutStatus() {
        return allocateOutStatus;
    }

    public void setAllocateOutStatus(String allocateOutStatus) {
        this.allocateOutStatus = allocateOutStatus;
    }

    public String getAllocateInStatus() {
        return allocateInStatus;
    }

    public void setAllocateInStatus(String allocateInStatus) {
        this.allocateInStatus = allocateInStatus;
    }
	
	public String getSkuNo() {
		return skuNo;
	}

	public void setSkuNo(String skuNo) {
		this.skuNo = skuNo;
	}

    public String getOldOutStatus() {
        return oldOutStatus;
    }

    public void setOldOutStatus(String oldOutStatus) {
        this.oldOutStatus = oldOutStatus;
    }

    public String getOldInStatus() {
        return oldInStatus;
    }

    public void setOldInStatus(String oldInStatus) {
        this.oldInStatus = oldInStatus;
    }
}