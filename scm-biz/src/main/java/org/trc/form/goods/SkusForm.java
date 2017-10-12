package org.trc.form.goods;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/1.
 */
public class SkusForm extends QueryModel {

    @QueryParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    //商品SKU编号
    @QueryParam("skuCode")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    //商品名称
    @Length(max = 64, message = "商品名称长度不能超过64个")
    @QueryParam("itemName")
    private String itemName;
    //商品所属分类ID
    @QueryParam("categoryId")
    private Long categoryId;
    //商品所属品牌ID
    @QueryParam("brandId")
    private Long brandId;
    //贸易类型
    @QueryParam("tradeType")
    private String tradeType;
    //sku名称
    @QueryParam("skuName")
    private String skuName;

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

}
