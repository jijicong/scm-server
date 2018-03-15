package org.trc.form.JDModel;

/**
 * Created by hzwdx on 2017/6/21.
 */
public class SkuDO {

    /**
     * 供应商SKU编码
     */
    private String supplySku;
    /**
     * external主键
     */
    private  Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplySku() {
        return supplySku;
    }

    public void setSupplySku(String supplySku) {
        this.supplySku = supplySku;
    }
}
