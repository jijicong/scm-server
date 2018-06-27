package org.trc.form.warehouseInfo;

import jdk.nashorn.internal.objects.annotations.Setter;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzcyn on 2018/5/3.
 */
public class LogisticsCorporationForm extends QueryModel {
    //物流公司名称
    @QueryParam("logisticsCorporationName")
    private String logisticsCorporationName;

    //物流公司编号
    @QueryParam("logisticsCode")
    private String logisticsCode;

    //物流公司名称
    @QueryParam("logisticsCorporationType")
    private String logisticsCorporationType;

    public String getLogisticsCorporationName() {
        return logisticsCorporationName;
    }

    public void setLogisticsCorporationName(String logisticsCorporationName) {
        this.logisticsCorporationName = logisticsCorporationName;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsCorporationType() {
        return logisticsCorporationType;
    }

    public void setLogisticsCorporationType(String logisticsCorporationType) {
        this.logisticsCorporationType = logisticsCorporationType;
    }
}
