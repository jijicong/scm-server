package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/5/26 0026.
 */
public class SellPriceDO {
    //商品编号
    private String skuId;

    //商品数量
    private String num;

    //分类
    private String category;

    //客户购买价格
    private String price;

    //商品名称
    private String name;

    //税收
    private String tax;

    //税费
    private String taxPrice;

    //裸价
    private String nakedPrice;

    //类型
    private String type;

    private String oid;

    private String remoteRegionFreight;

    //京东价格
    private String jdPrice;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(String taxPrice) {
        this.taxPrice = taxPrice;
    }

    public String getNakedPrice() {
        return nakedPrice;
    }

    public void setNakedPrice(String nakedPrice) {
        this.nakedPrice = nakedPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getRemoteRegionFreight() {
        return remoteRegionFreight;
    }

    public void setRemoteRegionFreight(String remoteRegionFreight) {
        this.remoteRegionFreight = remoteRegionFreight;
    }
}
