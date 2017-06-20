package org.trc.domain.goods;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 15:01
 */
@Table(name = "sku_relation")
public class SkuRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("spuCode")
    private String spuCode;//商品编号

    @FormParam("skuCode")
    private String skuCode;//供应链sku编号

    @FormParam("supplierSkuCode")
    private String supplierSkuCode;//供应商sku_code

    @FormParam("supplierName")
    private String supplierName;//供应商代号

    @FormParam("channelSkuCode")
    private String channelSkuCode;//渠道方sku_code

    @FormParam("channelName")
    private String channelName;//渠道方代号

    @FormParam("isValid")
    private String isValid;//是否有效:0-无效,1-有效

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getChannelSkuCode() {
        return channelSkuCode;
    }

    public void setChannelSkuCode(String channelSkuCode) {
        this.channelSkuCode = channelSkuCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
