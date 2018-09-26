package org.trc.form.warehouse.entryReturnOrder;

import lombok.Getter;
import lombok.Setter;
import org.trc.form.warehouse.ScmWarehouseRequestBase;

/**
 * 提交售后单物流信息
 */
@Setter
@Getter
public class ScmSubmitAfterSaleOrderLogisticsRequest extends ScmWarehouseRequestBase {

    /**
     * 售后单号
     */
	private String afterSaleCode;

    /**
     * 快递公司编码
     */
    private String logisticsCorporationCode;

    /**
     * 快递公司名称
     */
    private String logisticsCorporation;

    /**
     * 售后单编号
     */
    private String waybillNumber;

}
