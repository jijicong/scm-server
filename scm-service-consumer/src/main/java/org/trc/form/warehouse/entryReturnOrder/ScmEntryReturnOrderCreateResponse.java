package org.trc.form.warehouse.entryReturnOrder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmEntryReturnOrderCreateResponse implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 8021812420844523870L;
	/**
     * 仓库返回退货出库通知单编号
     */
	private String wmsEntryReturnNoticeCode;
	
    
}
