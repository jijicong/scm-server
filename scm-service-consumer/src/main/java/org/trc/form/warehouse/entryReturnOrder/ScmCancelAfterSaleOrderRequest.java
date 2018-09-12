package org.trc.form.warehouse.entryReturnOrder;

import lombok.Getter;
import lombok.Setter;
import org.trc.form.warehouse.ScmWarehouseRequestBase;

/**
 * 出库单(sku级)取消
 */
@Setter
@Getter
public class ScmCancelAfterSaleOrderRequest extends ScmWarehouseRequestBase {

    /**
     * 售后单号
     */
	private String afterSaleCode;

}
