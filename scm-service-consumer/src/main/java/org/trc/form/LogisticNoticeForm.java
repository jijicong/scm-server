package org.trc.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class LogisticNoticeForm extends TrcParam{

    //店铺订单编码
    private String shopOrderCode;
    //信息类型:0-物流单号,1-配送信息
    private String type;
    //物流信息
    private List<Logistic> logistics;

    public String getShopOrderCode() {
        return shopOrderCode;
    }

    public void setShopOrderCode(String shopOrderCode) {
        this.shopOrderCode = shopOrderCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Logistic> getLogistics() {
        return logistics;
    }

    public void setLogistics(List<Logistic> logistics) {
        this.logistics = logistics;
    }
}
