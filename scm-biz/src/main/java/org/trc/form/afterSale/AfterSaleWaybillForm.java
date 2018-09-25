package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

/**
 * 用于接收物流单号的form
 */
@Getter
@Setter
public class AfterSaleWaybillForm {

    /**
     * 售后单号
     */
    @QueryParam("afterSaleCode")
    private String	afterSaleCode;

    /**
     * 物流公司编码
     */
    @QueryParam("logisticsCorporationCode")
    private String	logisticsCorporationCode;

    /**
     * 物流公司名称
     */
    @QueryParam("logisticsCorporation")
    private String	logisticsCorporation;

    /**
     * 物流单号
     */
    @QueryParam("waybillNumber")
    private String	waybillNumber;

    /**
     * 备注信息
     */
    @QueryParam("memo")
    private String	memo;
}
