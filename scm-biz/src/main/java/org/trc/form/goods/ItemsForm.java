package org.trc.form.goods;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/5/24.
 */
public class ItemsForm extends QueryModel {

    //商品SPU编号
    @QueryParam("spuCode")
    @Length(max = 32, message = "商品SPU编号长度不能超过32个")
    private String spuCode;
    //商品SKU编号
    @QueryParam("skuCode")
    @Length(max = 32, message = "商品SKU编号长度不能超过32个")
    private String skuCode;
    //商品名称
    @QueryParam("name")
    @Length(max = 200, message = "商品名称长度不能超过200个")
    private String name;
    //商品所属分类ID
    @QueryParam("categoryId")
    private Long categoryId;
    //商品所属品牌ID
    @QueryParam("brandId")
    private Long brandId;

    //商品二维码
    @QueryParam("barCode")
    private String barCode;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
