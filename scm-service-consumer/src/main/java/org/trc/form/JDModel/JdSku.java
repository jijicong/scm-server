package org.trc.form.JDModel;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;

/**
 * Created by hzwdx on 2017/7/1.
 */
public class JdSku {
    //商品编号
    private String skuId;
    //商品数
    private Integer num;
    //是否需要附件,默认值为：true
    private Boolean bNeedAnnex;
    //是否需要增品,默认值为不给增品：false
    private Boolean bNeedGift;
    //延保商品skuId列表
    private JSONArray yanbao;
    //用户实付金额
    private BigDecimal payment;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getbNeedAnnex() {
        return bNeedAnnex;
    }

    public void setbNeedAnnex(Boolean bNeedAnnex) {
        this.bNeedAnnex = bNeedAnnex;
    }

    public Boolean getbNeedGift() {
        return bNeedGift;
    }

    public void setbNeedGift(Boolean bNeedGift) {
        this.bNeedGift = bNeedGift;
    }

    public JSONArray getYanbao() {
        return yanbao;
    }

    public void setYanbao(JSONArray yanbao) {
        this.yanbao = yanbao;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }
}
