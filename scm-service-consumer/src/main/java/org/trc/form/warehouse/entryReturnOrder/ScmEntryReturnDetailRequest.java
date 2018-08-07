package org.trc.form.warehouse.entryReturnOrder;

import org.trc.form.warehouse.ScmWarehouseRequestBase;

public class ScmEntryReturnDetailRequest extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6262985704088810498L;
	
	/**
     * wms采购退货出库单号
     */
    private String wmsEntryReturnNoticeCode;

	public String getWmsEntryReturnNoticeCode() {
		return wmsEntryReturnNoticeCode;
	}

	public void setWmsEntryReturnNoticeCode(String wmsEntryReturnNoticeCode) {
		this.wmsEntryReturnNoticeCode = wmsEntryReturnNoticeCode;
	}

}
