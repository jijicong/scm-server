package org.trc.domain.warehouseNotice;

import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;
import org.trc.custom.MoneySerializer;
import org.trc.custom.SimpleDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by sone on 2017/7/11.
 */
@Table(name = "warehouse_notice_details")
public class WarehouseNoticeDetails implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'入库通知单编号',
    @ApiModelProperty("入库通知单编号")
    @Column(name = "warehouse_notice_code")
    private String warehouseNoticeCode;
    //'商品名称',
    @Column(name = "sku_name")
    private String skuName;
    //'sku编码',
    @Column(name = "sku_code")
    private String skuCode;
    //'品牌',
    @Column(name = "brand_id")
    private Long brandId;
    //品牌名称
    @Transient
    private String brandName;
    //'分类',
    @Column(name = "category_id")
    private Long categoryId;
    @Transient
    private String allCategoryName;
    //'采购价,单位/分',
    @ApiModelProperty("含税单价")
    @Column(name = "purchase_price")
    private Long purchasePrice;
    //采购价格转化成元
    @Transient
    private BigDecimal purchasePriceT;
    //'采购数量',
    @ApiModelProperty("采购数量")
    @Column(name = "purchasing_quantity")
    private Long purchasingQuantity;
    //'实际入库数量',
    @ApiModelProperty("实际入库数量")
    @Column(name = "actual_storage_quantity")
    private Long actualStorageQuantity;
    //'创建时间,格式yyyy-mm-dd hh:mi:ss',
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    //'入库时间,格式yyyy-mm-dd hh:mi:ss',
    @ApiModelProperty("实际入库时间")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date storageTime;
    
    /**
     * scm2.0新增字段 
     **/
    //条形码
    @Column(name = "bar_code")
    private String barCode;
    //规格
    @Column(name = "spec_info")
    private String specInfo;
    //批次号
    @Column(name = "batch_no")
    private String batchNo;
    //生产编码
    @Column(name = "production_code")
    private String productionCode;
    //生产日期
    @JsonSerialize(using = SimpleDateSerializer.class)
    private Date productionDate;
    //截止保质日期
    @JsonSerialize(using = SimpleDateSerializer.class)
    private Date expiredDate;	
    //理论保质期限（天）
    @Column(name = "expired_day")
    private Integer expiredDay;
    //采购总金额, 单位/分
    @JsonSerialize(using = MoneySerializer.class)
    private Long purchaseAmount;
    //收货状态
    @Column(name = "status")
    private Integer status;
    //正品入库数量
    @ApiModelProperty("正品入库数量")
    @Column(name = "normal_storage_quantity")
    private Long normalStorageQuantity;
    //残次品入库数量
    @ApiModelProperty("残次品入库数量")
    @Column(name = "defective_storage_quantity")
    private Long defectiveStorageQuantity;
    //货主编码
    @Column(name = "owner_code")
    private String ownerCode;
    //第三方仓库商品ID
    @Column(name = "item_id")
    private String itemId;
    //实际入库时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date actualInstockTime;	
    // 入库异常原因
    @Column(name = "instock_exception")
    private String instockException;

    //库存表对应的
    @Column(name = "sku_stock_id")
    private Long skuStockId;

    @Transient
    @ApiModelProperty("采购单号")
    private String purchaseOrderCode;

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public Date getActualInstockTime() {
		return actualInstockTime;
	}

	public void setActualInstockTime(Date actualInstockTime) {
		this.actualInstockTime = actualInstockTime;
	}

	public String getInstockException() {
		return instockException;
	}

	public void setInstockException(String instockException) {
		this.instockException = instockException;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getSpecInfo() {
		return specInfo;
	}

	public void setSpecInfo(String specInfo) {
		this.specInfo = specInfo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getProductionCode() {
		return productionCode;
	}

	public void setProductionCode(String productionCode) {
		this.productionCode = productionCode;
	}

	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Integer getExpiredDay() {
		return expiredDay;
	}

	public void setExpiredDay(Integer expiredDay) {
		this.expiredDay = expiredDay;
	}

	public Long getPurchaseAmount() {
		return purchaseAmount;
	}

	public void setPurchaseAmount(Long purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getNormalStorageQuantity() {
		return normalStorageQuantity;
	}

	public void setNormalStorageQuantity(Long normalStorageQuantity) {
		this.normalStorageQuantity = normalStorageQuantity;
	}

	public Long getDefectiveStorageQuantity() {
		return defectiveStorageQuantity;
	}

	public void setDefectiveStorageQuantity(Long defectiveStorageQuantity) {
		this.defectiveStorageQuantity = defectiveStorageQuantity;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPurchasePriceT() {
        return purchasePriceT;
    }

    public void setPurchasePriceT(BigDecimal purchasePriceT) {
        this.purchasePriceT = purchasePriceT;
    }

    public String getAllCategoryName() {
        return allCategoryName;
    }

    public void setAllCategoryName(String allCategoryName) {
        this.allCategoryName = allCategoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getWarehouseNoticeCode() {
        return warehouseNoticeCode;
    }

    public void setWarehouseNoticeCode(String warehouseNoticeCode) {
        this.warehouseNoticeCode = warehouseNoticeCode;
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Long purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getPurchasingQuantity() {
        return purchasingQuantity;
    }

    public void setPurchasingQuantity(Long purchasingQuantity) {
        this.purchasingQuantity = purchasingQuantity;
    }

    public Long getActualStorageQuantity() {
        return actualStorageQuantity;
    }

    public void setActualStorageQuantity(Long actualStorageQuantity) {
        this.actualStorageQuantity = actualStorageQuantity;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Date storageTime) {
        this.storageTime = storageTime;
    }

    public Long getSkuStockId() {
        return skuStockId;
    }

    public void setSkuStockId(Long skuStockId) {
        this.skuStockId = skuStockId;
    }
}
