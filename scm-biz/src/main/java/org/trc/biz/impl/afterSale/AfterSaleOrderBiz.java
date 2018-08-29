package org.trc.biz.impl.afterSale;

import com.qimen.api.response.WarehouseinfoQueryResponse.WarehouseInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.LogisticsCompany;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.ShopOrder;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderDetailTypeEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderStatusEnum;
import org.trc.enums.ValidEnum;
import org.trc.form.afterSale.AfterSaleDetailVO;
import org.trc.form.afterSale.AfterSaleOrderAddDO;
import org.trc.form.afterSale.AfterSaleOrderItemVO;
import org.trc.service.System.ILogisticsCompanyService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.GuidUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service("afterSaleOrderBiz")
public class AfterSaleOrderBiz implements IAfterSaleOrderBiz{

	@Resource
	private IOrderItemService orderItemService;
	@Resource
	private IAfterSaleOrderService afterSaleOrderService;
	@Resource
	private IAfterSaleOrderDetailService afterSaleOrderDetailService;
	@Resource
	private IShopOrderService shopOrderService;
	@Autowired
    private ISerialUtilService serialUtilService;
	@Resource
	private ILogisticsCompanyService logisticsCompanyService;
	@Resource
	private IWarehouseInfoService warehouseInfoService;
	
	private static final String AFTER_SALE_ORDER_DETAIL_ID="AFTERD-";
	private static final String AFTER_SALE_ORDER_ID="AFTER-";
	
	@Override
	public List<AfterSaleOrderItemVO> selectAfterSaleInfo(String shopOrderCode) throws Exception{
		//根据订单号查询子订单信息
		OrderItem selectOrderItem = new OrderItem();
		selectOrderItem.setShopOrderCode(shopOrderCode);
		List<OrderItem> orderItemList=orderItemService.select(selectOrderItem);
		AssertUtil.notNull(orderItemList, "没有该订单的数据!");
		
		List<AfterSaleOrderItemVO> afterSaleOrderItemVOList=new ArrayList<>();
		for(OrderItem orderItem:orderItemList) {
			AfterSaleOrderItemVO vo=new AfterSaleOrderItemVO();
			BeanUtils.copyProperties(orderItem, vo);
			//下单的数量-退货数量
			int orderNum=orderItem.getNum();
			int refundNum=getAlreadyRefundNum(orderItem);
			vo.setCanRefundNum(orderNum-refundNum);
			afterSaleOrderItemVOList.add(vo);
		}
		return afterSaleOrderItemVOList;
	}

	/**
	 * 订单退货数量
	 * @param orderItem
	 * @return
	 */
	private int getAlreadyRefundNum(OrderItem orderItem) {
		AfterSaleOrderDetail afterSaleOrderDetailSelect=new AfterSaleOrderDetail();
		afterSaleOrderDetailSelect.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleOrderDetailSelect.setSkuCode(orderItem.getSkuCode());
		
		int num=0;
		List<AfterSaleOrderDetail> afterSaleOrderDetailsList=afterSaleOrderDetailService.select(afterSaleOrderDetailSelect);
		if(afterSaleOrderDetailsList!=null) {
			for(AfterSaleOrderDetail afterSaleOrderDetail:afterSaleOrderDetailsList) {
				num=num+afterSaleOrderDetail.getSkuNum();
			}
			
		}
		return num;
	}

	@Override
	public void addAfterSaleOrder(AfterSaleOrderAddDO afterSaleOrderAddDO,AclUserAccreditInfo aclUserAccreditInfo) {
		String shopOrderCode=afterSaleOrderAddDO.getShopOrderCode();
		ShopOrder shopOrderselect=new ShopOrder();
		shopOrderselect.setShopOrderCode(shopOrderCode);
		ShopOrder shopOrder=shopOrderService.selectOne(shopOrderselect);
		AssertUtil.notNull(shopOrder, "根据该订单号查询到的订单为空!");
		
		String afterSaleCode = serialUtilService.generateCode(SupplyConstants.Serial.AFTER_SALE_LENGTH, 
        		SupplyConstants.Serial.AFTER_SALE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		
		AfterSaleOrder afterSaleOrder=new AfterSaleOrder();
		String afterSaleOrderId=GuidUtil.getNextUid(AFTER_SALE_ORDER_ID);
		afterSaleOrder.setId(afterSaleOrderId);
		afterSaleOrder.setAfterSaleCode(afterSaleCode);
		afterSaleOrder.setShopOrderCode(shopOrderCode);
		afterSaleOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleOrder.setPicture(afterSaleOrderAddDO.getPicture());
		afterSaleOrder.setMemo(afterSaleOrderAddDO.getMemo());
		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderAddDO.getLogistics_corporation_code());
		afterSaleOrder.setLogisticsCorporation(afterSaleOrderAddDO.getLogistics_corporation());
		afterSaleOrder.setExpressNumber(afterSaleOrderAddDO.getExpress_number());
		afterSaleOrder.setWmsCode(afterSaleOrderAddDO.getWms_code());
		afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setCreateOperator(aclUserAccreditInfo.getName());
		afterSaleOrder.setUpdateOperator(aclUserAccreditInfo.getName());
		afterSaleOrder.setUpdateTime(new Date());
		
		List<AfterSaleOrderDetail> details=afterSaleOrderAddDO.getAfterSaleOrderDetailList();
		AssertUtil.notEmpty(details, "售后单子订单为空!");
		List<AfterSaleOrderDetail> detailList=new ArrayList<>();
		for(AfterSaleOrderDetail afterSaleOrderDetailDO:details) {
			String skuCode=afterSaleOrderDetailDO.getSkuCode();
			int skuNum=afterSaleOrderDetailDO.getSkuNum();
			BigDecimal skuMoney=afterSaleOrderDetailDO.getSkuMoney();
			
			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setShopOrderCode(shopOrderCode);
			orderItemSelect.setSkuCode(skuCode);
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			
			AfterSaleOrderDetail afterSaleOrderDetail=new AfterSaleOrderDetail();
			String afterSaleOrderDetailId=GuidUtil.getNextUid(AFTER_SALE_ORDER_DETAIL_ID);
			afterSaleOrderDetail.setId(afterSaleOrderDetailId);
			afterSaleOrderDetail.setShopOrderCode(shopOrderCode);
			afterSaleOrderDetail.setOrderItemCode(orderItem.getOrderItemCode());
			afterSaleOrderDetail.setSkuCode(skuCode);
			afterSaleOrderDetail.setSkuMoney(skuMoney);
			if (orderItem.getSkuCode().startsWith("SP0")) {
				afterSaleOrderDetail.setSkuType(AfterSaleOrderDetailTypeEnum.STATUS_0.getCode());
			}else {
				afterSaleOrderDetail.setSkuType(AfterSaleOrderDetailTypeEnum.STATUS_1.getCode());
			}
			afterSaleOrderDetail.setCreateTime(new Date());
			afterSaleOrderDetail.setUpdateTime(new Date());
			detailList.add(afterSaleOrderDetail);
		}
		
		afterSaleOrderService.insert(afterSaleOrder);
		afterSaleOrderDetailService.insertList(detailList);
	}

	@Override
	public List<LogisticsCompany> selectLogisticsCompany() {
		LogisticsCompany logisticsCompany=new LogisticsCompany();
		logisticsCompany.setIsValid(ValidEnum.VALID.getCode());
		return logisticsCompanyService.select(logisticsCompany);
	}

	@Override
	public List<WarehouseInfo> selectWarehouse() {
		WarehouseInfo warehouseInfo=new WarehouseInfo();
		//warehouseInfo.set
		return null;
	}

	@Override
	public AfterSaleDetailVO queryAfterSaleOrderDetail(Long id) {
		AssertUtil.notNull(id, "查询售后单详情参数id不能为空");


		return null;
	}

}
