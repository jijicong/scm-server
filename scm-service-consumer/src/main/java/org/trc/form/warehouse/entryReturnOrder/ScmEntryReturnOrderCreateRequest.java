package org.trc.form.warehouse.entryReturnOrder;

import java.util.List;

import org.trc.form.warehouse.ScmWarehouseRequestBase;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmEntryReturnOrderCreateRequest extends ScmWarehouseRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1423030812330798095L;

	/**
     * 退货出库通知单编号
     */
	private String outboundNoticeCode;
	
	/**
	 * 开放平台事业部编号(京东仓库用)
	 */
	private String deptNo;
	
	/**
	 * 提货方式1-到仓自提，2-京东配送，3-其他物流
	 */
	private String pickType;
	
	/**
	 * 退货仓库编号
	 */
	private String warehouseCode;
	
	/**
	 * 退货类型1-正品，2-残品
	 */
	private String returnOrderType;
	
    /**
     * 退货收货人姓名
     */
    private String receiver;
    
    /**
     * 退货人手机号
     */
    private String receiverNumber;
	
    /**
     * 退货省份
     */
    private String receiverProvince;

    /**
     * 退货城市
     */
    private String receiverCity;
    
    /**
     * 退货地区
     */
    private String receiverArea;
	
    /**
     * 退货详细地址
     */
    private String receiverAddress;

    /**
     * 退货出库商品列表
     */
    private List<ScmEntryReturnItem> entryOrderItemList;
    
}
