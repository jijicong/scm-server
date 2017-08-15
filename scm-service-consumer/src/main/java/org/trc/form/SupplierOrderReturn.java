package org.trc.form;

import java.util.List;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class SupplierOrderReturn {

    //供应商订单号
    private String supplyOrderCode;
    //订单状态:1-成功,0-失败
    private String state;
    //错误信息
    private String message;
    //skus信息列表
    private List<SkuInfo> skus;

    public String getSupplyOrderCode() {
        return supplyOrderCode;
    }

    public void setSupplyOrderCode(String supplyOrderCode) {
        this.supplyOrderCode = supplyOrderCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<SkuInfo> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuInfo> skus) {
        this.skus = skus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
