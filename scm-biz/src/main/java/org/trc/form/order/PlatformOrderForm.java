package org.trc.form.order;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/6/29.
 */
public class PlatformOrderForm extends QueryModel {

    // 平台订单编码
    @QueryParam("platformOrderCode")
    @Length(max = 32, message = "平台订单编码长度不能超过32个")
    private String platformOrderCode;

    public String getPlatformOrderCode() {
        return platformOrderCode;
    }

    public void setPlatformOrderCode(String platformOrderCode) {
        this.platformOrderCode = platformOrderCode;
    }
}
