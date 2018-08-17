package org.trc.form.warehouse.entryReturnOrder;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmEntryReturnDetailResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3987016822941369552L;

	/**
     * 退货出库通知单编号
     */
	private String outboundNoticeCode;

	/**
     * 仓库返回退货出库通知单编号
     */
	private String wmsEntryReturnNoticeCode;
	
	/**
	 * 退货出库通知单当前状态(京东枚举值：0.新建；100.初始；200.完成；300.取消中；400.已取消；500.取消失败)
	 */
	private String status;
	
    /**
     * 退货出库通知单商品明细
     */
    private List<ScmEntryReturnDetailItem> itemList;
}
