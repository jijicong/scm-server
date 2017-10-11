package org.trc.biz.impl.trc.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.goods.Skus;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import java.util.List;

/**
 * Created by hzwdx on 2017/8/31.
 */
public class Skus2 extends Skus{

//    @FormParam("name")
//    @NotEmpty
//    @Length(max = 128, message = "商品名称长度不能超过128个")
//    private String name;
    @FormParam("categoryId")
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @FormParam("brandId")
    @NotNull(message = "品牌不能为空")
    private Long brandId;
    @FormParam("tradeType")
    @NotEmpty
    @Length(max = 32, message = "贸易类型长度不能超过32个")
    private String tradeType;
    @FormParam("itemNo")
    @Length(max = 32, message = "商品货号长度不能超过32个")
    private String itemNo;
    @FormParam("producer")
    @Length(max = 128, message = "生产商长度不能超过32个")
    private String producer;
    @Transient
    private String categoryName;//分类名称
    @Transient
    private String brandName;//供应商名称
    /**
     * SKU相关属性
     */
    @javax.persistence.Transient
    private List<SkusProperty> propertys;

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

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public List<SkusProperty> getPropertys() {
        return propertys;
    }

    public void setPropertys(List<SkusProperty> propertys) {
        this.propertys = propertys;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getBrandName() {
        return brandName;
    }

    @Override
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
