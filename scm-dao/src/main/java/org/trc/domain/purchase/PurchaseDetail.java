package org.trc.domain.purchase;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 采购明细信息
 * Created by sone on 2017/5/25.
 */
public class PurchaseDetail extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("purchaseId")
    @NotEmpty
    private Long purchaseId;
    @FormParam("purchaseOrderCode")
    @NotEmpty
    @Length(max = 32, message = "采购单编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;
    @FormParam("itemName")
    @NotEmpty
    @Length(max = 32, message = "采购的商品名称字母和数字不能超过32个,汉字不能超过16个")
    private String itemName;
    @FormParam("skuCode")
    @NotEmpty
    @Length(max = 32, message = "商品的sku名称字母和数字不能超过32个,汉字不能超过16个")
    private String skuCode;
    @FormParam("brandCode")
    @Length(max = 32, message = "商品的品牌编码字母和数字不能超过32个,汉字不能超过16个")
    private String brandCode;
    @FormParam("brandName")
    @Length(max = 256, message = "商品的品牌名称字母和数字不能超过256个,汉字不能超过128个")
    private String brandName;
    @FormParam("categoryCode")
    @Length(max = 32, message = "商品的所属分类编码字母和数字不能超过32个,汉字不能超过16个")
    private String  categoryCode;
    @FormParam("categoryName")
    @Length(max = 128, message = "商品的所属分类的名称字母和数字不能超过128个,汉字不能超过64个")
    private String  categoryName;
    @FormParam("skuCode")
    @Length(max = 64, message = "商品的所有分类字母和数字不能超过64个,汉字不能超过32个")
    private String allCategory;//所有分类
    @FormParam("purchasePrice")
    private Long purchasePrice;//采购单价
    @FormParam("purchasingQuantity")
    private Long purchasingQuantity;//采购总数量
    @FormParam("totalPurchaseAmount")
    private Long totalPurchaseAmount;//采购总金额

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAllCategory() {
        return allCategory;
    }

    public void setAllCategory(String allCategory) {
        this.allCategory = allCategory;
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

    public Long getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(Long totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }
}

