package org.trc.form;

/**
 * Created by hzwdx on 2017/8/29.
 */
public class SupplierOrderBase {

    /**
     * 是否正式提交订单:1-是,0-否
     */
    private String submitOrderStatus;

    public String getSubmitOrderStatus() {
        return submitOrderStatus;
    }

    public void setSubmitOrderStatus(String submitOrderStatus) {
        this.submitOrderStatus = submitOrderStatus;
    }
}
