package org.trc.form.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by sone on 2017/5/27.
 */
public class ItemForm extends QueryModel{
    /**
     * 商品名称
     */
    @QueryParam("name")
    @Length(max = 128)
    private String name;
    /**
     * 商品SKU-CODE
     */
    @QueryParam("skuCode")
    @Length(max = 32)
    private String skuCode;
    /**
     * 商品SKU-CODE
     */
    @QueryParam("BrandName")
    @Length(max = 256)
    private String BrandName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }
}
