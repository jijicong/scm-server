package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
public class TairanAfterSaleOrderDO {
	
	/**
	 * 请求流水号,每个售后申请唯一,用来做幂等
	 */
	@QueryParam("requestNo")
	private String requestNo;
	
	/**
	 * 店铺订单号
	 */
	@QueryParam("shopOrderCode")
	private String shopOrderCode;
	
	/**
	 * 退货场景：0实体店退货，1线上商城退货
	 */
	@QueryParam("returnScene")
	private int returnScene;
	
	/**
	 * 售后类型：0取消发货，1退货
	 */
	@QueryParam("afterSaleType")
	private int afterSaleType;
	
	/**
	 * 售后上传图片路径url,多个图片路径用逗号分隔
	 */
	@QueryParam("picture")
	private String picture;
	
	/**
	 * 入库仓库仓库编码
	 */
	@QueryParam("returnWarehouseCode")
	private String returnWarehouseCode;
	
	/**
	 * 快递公司编码
	 */
	@QueryParam("logisticsCorporationCode")
	private String logisticsCorporationCode;
	
	/**
	 * 快递公司名称
	 */
	@QueryParam("logisticsCorporation")
	private String logisticsCorporation;
	
	/**
	 * 物流单号
	 */
	@QueryParam("waybillNumber")
	private String waybillNumber;
	
	/**
	 * 备注
	 */
	@QueryParam("memo")
	private String memo;
	
	/**
	 * 售后单sku详情信息列表
	 */
	@QueryParam("afterSaleOrderDetailList")
	private List<TaiRanAfterSaleOrderDetail> afterSaleOrderDetailList;

    /**
     * 渠道编码
     */
    @QueryParam("channelCode")
    private String channelCode;

    /**
     * 销售渠道
     */
    @QueryParam("sellCode")
    private String sellCode;

}
