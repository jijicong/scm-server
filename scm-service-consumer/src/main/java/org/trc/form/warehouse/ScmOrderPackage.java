package org.trc.form.warehouse;

import java.util.List;

/**
 * Created by hzcyn on 2018/4/11.
 */
public class ScmOrderPackage {

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 商品明细集合
     */
    private List<ScmDeliveryOrderDetailResponseItem> scmDeliveryOrderDetailResponseItems;

    public String getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public List<ScmDeliveryOrderDetailResponseItem> getScmDeliveryOrderDetailResponseItems() {
        return scmDeliveryOrderDetailResponseItems;
    }

    public void setScmDeliveryOrderDetailResponseItems(List<ScmDeliveryOrderDetailResponseItem> scmDeliveryOrderDetailResponseItems) {
        this.scmDeliveryOrderDetailResponseItems = scmDeliveryOrderDetailResponseItems;
    }
}
