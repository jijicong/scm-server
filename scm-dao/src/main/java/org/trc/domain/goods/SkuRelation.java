package org.trc.domain.goods;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;
import java.io.Serializable;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:01
 */
@Table(name = "sku_relation")
public class SkuRelation implements Serializable {

    private static final long serialVersionUID = 8130558214880972290L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("spuCode")
    private String spuCode;//商品编号

    @FormParam("skuCode")
    private String skuCode;//供应链sku编号

    @FormParam("supplierSkuCode")
    private String supplierSkuCode;//供应商sku_code

    @FormParam("supplierCode")
    private String supplierCode;//供应商编码

    @FormParam("channelSkuCode")
    private String channelSkuCode;//渠道方sku_code

    @FormParam("channelCode")
    private String channelCode;//渠道方编码

    private String isValid;//是否启用

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSupplierSkuCode() {
        return supplierSkuCode;
    }

    public void setSupplierSkuCode(String supplierSkuCode) {
        this.supplierSkuCode = supplierSkuCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getChannelSkuCode() {
        return channelSkuCode;
    }

    public void setChannelSkuCode(String channelSkuCode) {
        this.channelSkuCode = channelSkuCode;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
