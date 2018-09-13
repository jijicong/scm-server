package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

/**
 * 出库单(sku级)取消
 */
@Setter
@Getter
public class ScmAfterSaleOrderCancelRequest extends ScmWarehouseRequestBase {

	private static final long serialVersionUID = -836448235773709037L;

	private String outboundOrderCode;

    private String skuCode;

}
