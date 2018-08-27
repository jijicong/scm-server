package org.trc.biz.impl.afterSale;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.order.OrderItem;
import org.trc.form.afterSale.AfterSaleOrderItemVO;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.impl.order.OrderItemService;
import org.trc.service.order.IOrderItemService;
import org.trc.util.AssertUtil;


@Service("afterSaleOrderBiz")
public class AfterSaleOrderBiz implements IAfterSaleOrderBiz{

	@Resource
	private IOrderItemService orderItemService;
	@Resource
	private IAfterSaleOrderService afterSaleOrderService;
	@Resource
	private IAfterSaleOrderDetailService afterSaleOrderDetailService;
	
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

}
