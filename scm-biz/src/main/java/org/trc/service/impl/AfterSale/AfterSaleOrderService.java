package org.trc.service.impl.AfterSale;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.OutboundOrderException;
import org.trc.form.afterSale.AfterSaleNoticeWmsForm;
import org.trc.form.afterSale.AfterSaleNoticeWmsResultVO;
import org.trc.form.order.OutboundForm;
import org.trc.form.warehouse.ScmDeliveryOrderCreateResponse;
import org.trc.mapper.outbound.IOutboundDetailMapper;
import org.trc.mapper.outbound.IOutboundOrderMapper;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResponseAck;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


/**
 * <p>
 * 售后主表 服务类
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
@Service("afterSaleOrderService")
public class AfterSaleOrderService extends BaseService<AfterSaleOrder, String> implements IAfterSaleOrderService {

	@Autowired
	private IOutboundOrderMapper orderMapper;
	@Autowired
	private IOutboundDetailMapper orderDetailMapper;
	@Autowired
	private IOutBoundOrderService orderService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IWarehouseExtService warehouseExtService;
    @Autowired
    private IOutboundDetailService detailService;
    @Autowired
    private IScmOrderBiz scmOrderBiz;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;

	private Logger logger = LoggerFactory.getLogger(AfterSaleOrderService.class);

	public static final String CANCEL_SUCCESS = "1";
	public static final String CANCELLING = "2";
	public static final String CANCEL_FAILED = "3";

	@Override
	public Map<String, Object> deliveryCancel(String scmShopOrderCode, String skuCode) {
        /**
         * 参数校验
         */
		checkParam(scmShopOrderCode, skuCode);

		OutboundOrder targetOrder = getOutboundOrder(scmShopOrderCode, skuCode);

        Map<String, String> cancelResult = null;
        Map<String, Object> returnMap = new HashMap<>();
        try {
        	cancelResult = orderService.deliveryCancel(targetOrder, skuCode);
            /**
             * 取消成功 or 取消中 返回给前端都表示成功
             */
        	String resultMsg = null;
        	if (OrderCancelResultEnum.CANCEL_SUCC.code.equals(cancelResult.get("flg"))
        			|| OrderCancelResultEnum.CANCELLING.code.equals(cancelResult.get("flg"))) {
            	returnMap.put("flg", true);
            	resultMsg = "取消成功";
        	} else {
            	returnMap.put("flg", false);
            	returnMap.put("msg", cancelResult.get("msg"));
            	resultMsg = "取消失败，" + cancelResult.get("msg");
        	}
        	// 日志记录
        	logInfoService.recordLog(targetOrder, String.valueOf(targetOrder.getId()), "系统",
        			"售后单取消发货", "取消结果:" + resultMsg, null);
        	
        } catch (OutboundOrderException oex) {
        	
        	returnMap.put("flg", false);
        	returnMap.put("msg", oex.getMessage());
        	logger.error("系统订单号:{}, 取消发货失败", scmShopOrderCode, oex);
        	
        } catch (Exception ex) {
        	
        	returnMap.put("flg", false);
        	returnMap.put("msg", "系统内部错误");
        	logger.error("系统订单号:{}, 取消发货异常", scmShopOrderCode, ex);
        } 
        return returnMap;
	}

	private void checkParam(String scmShopOrderCode, String skuCode) {

		if (StringUtils.isBlank(scmShopOrderCode)) {
			throw new IllegalArgumentException("系统订单号不能为空!");
		}

		if (StringUtils.isBlank(skuCode)) {
			throw new IllegalArgumentException("商品编码不能为空!");
		}

	}

	/**
	 * 根据系统订单号和商品编号获取发货单号
	 * @param scmShopOrderCode
	 * @param skuCode
	 * @return
	 */
	private OutboundOrder getOutboundOrder (String scmShopOrderCode, String skuCode) {
	       /**
         * 根据系统订单号获取发货单号
         */
        OutboundOrder orderRecord = new OutboundOrder();
        orderRecord.setScmShopOrderCode(scmShopOrderCode);
        List<OutboundOrder> orderList = orderMapper.select(orderRecord);
        AssertUtil.notNull(orderList, "根据系统订单号" + scmShopOrderCode + "查询发货单为空!");

       // String skuCode = skuList.get(0); // 取消目前只支持一个退货单一个商品操作
        List<String> orderCodeList = orderList.stream().map(OutboundOrder :: getOutboundOrderCode).collect(toList());
        Example example = new Example(OutboundDetail.class);
        Example.Criteria cra = example.createCriteria();
        cra.andEqualTo("skuCode", skuCode);
        cra.andIn("outboundOrderCode", orderCodeList);

        List<OutboundDetail> detailList = orderDetailMapper.selectByExample(example);
        /**
         * 暂时只考虑一个商品只能在一个发货单里面
         */
        if (detailList == null || detailList.size() != 1) {
        	throw new IllegalArgumentException("单个商品在发货单详情中不能为空并且个数必须为1!");
        }

        String outboundOrderCode = detailList.get(0).getOutboundOrderCode();
        return orderList.stream().filter(order ->
        	outboundOrderCode.equals(order.getOutboundOrderCode())).findAny().orElse(null);

	}

	@Override
	public List<AfterSaleNoticeWmsResultVO> deliveryCancelResult(List<AfterSaleNoticeWmsForm> form) {
		
		// 返回结果
		List<AfterSaleNoticeWmsResultVO> retList = new ArrayList<>();

		for (AfterSaleNoticeWmsForm item : form) {
			String scmShopOrderCode = item.getScmShopOrderCode();
			String skuCode = item.getSkuCode();

			if (StringUtils.isBlank(scmShopOrderCode) || StringUtils.isBlank(skuCode)) {
				continue;
			}
			OutboundOrder order = getOutboundOrder(scmShopOrderCode, skuCode);
	        WarehouseTypeEnum warehouseType = warehouseExtService.getWarehouseType(order.getWarehouseCode());

	        if (WarehouseTypeEnum.Zy == warehouseType) {
	        	/**
	        	 * 自营仓-直接返回
	        	 */
	        	retList.add(generateVo(item, CANCEL_SUCCESS, null));

	        } else if (WarehouseTypeEnum.Jingdong == warehouseType) {
	        	/**
	        	 * 京东仓
	        	 * 1.取消中 - 返回取消中的状态
	        	 * 2.已取消 - 需要重新发货
	        	 */
	        	if (StringUtils.equals(order.getStatus(), OutboundOrderStatusEnum.ON_CANCELED.getCode())) {// 1.取消中

	        		retList.add(generateVo(item, CANCELLING, null));

	        	} else if (StringUtils.equals(order.getStatus(), OutboundOrderStatusEnum.CANCELED.getCode())) {// 2.已取消
	        		try {

		                Example example = new Example(OutboundDetail.class);
		                Example.Criteria criteria = example.createCriteria();
		                criteria.andEqualTo("outboundOrderCode", order.getOutboundOrderCode());
		                criteria.andNotEqualTo("skuCode", skuCode);// 取消的商品过滤
		                List<OutboundDetail> outboundDetails = detailService.selectByExample(example);
			        	/**
			        	 * 如果发货单只有一个商品，取消后就不需要重新发货
			        	 */
		                if (CollectionUtils.isEmpty(outboundDetails)) {
		                	retList.add(generateVo(item, CANCEL_SUCCESS, null));
		                	continue;
		                }

		                WarehouseInfo warehouse = warehouseInfoService.selectByPrimaryKey(order.getWarehouseId());

		                //设置发货通知单参数
		                Map<String, OutboundForm> outboundMap = new HashMap<>();
		                OutboundForm outboundForm = new OutboundForm();
		                outboundForm.setOutboundOrder(order);
		                outboundForm.setOutboundDetailList(outboundDetails);
		                outboundMap.put(order.getOutboundOrderCode(), outboundForm);

		                AppResult<List<ScmDeliveryOrderCreateResponse>> result = scmOrderBiz.deliveryOrderCreate(outboundMap, false);

	                    List<ScmDeliveryOrderCreateResponse> responses = (List<ScmDeliveryOrderCreateResponse>) result.getResult();

	                    if (StringUtils.equals(ResponseAck.SUCCESS_CODE, responses.get(0).getCode())) {

	                    	retList.add(generateVo(item, CANCEL_SUCCESS, null));

	                        updateOutboundDetailState(order, OutboundDetailStatusEnum.WAITING.getCode(), responses.get(0).getWmsOrderCode(), skuCode);
	                        logInfoService.recordLog(order, order.getId().toString(),
	                        		warehouse.getWarehouseName(), "售后取消-仓库接收成功", "收货取消发货-重新发货",null);

	                    } else {
	        	        	/**
	        	        	 * 仓库接受失败
	        	        	 */
	                    	retList.add(generateVo(item, CANCEL_FAILED, responses.get(0).getMessage()));

	                    	scmOrderBiz.outboundOrderSubmitResultNoticeChannel(order.getShopOrderCode());
	                    	updateOutboundDetailState(order, OutboundDetailStatusEnum.RECEIVE_FAIL .getCode(), null, skuCode);
	                        logInfoService.recordLog(order, order.getId().toString(),
	                        		warehouse.getWarehouseName(), "售后取消-仓库接收失败", responses.get(0).getMessage(), null);

	                    }
		                //更新订单信息
		                orderService.updateItemOrderSupplierOrderStatus(order.getOutboundOrderCode(), order.getWarehouseOrderCode());
	        		} catch (Exception e) {

	        			retList.add(generateVo(item, CANCEL_FAILED, e.getMessage()));
	        			logger.error("scmShopOrderCode:{}, outboundOrderCode:{}, skuCode: {}, 售后取消发货异常",
	        					scmShopOrderCode, order.getOutboundOrderCode(), skuCode, e);

	        		}

	        	} else if (StringUtils.equals(order.getStatus(), OutboundOrderStatusEnum.WAITING.getCode())) {
	        		/**
	        		 * 1.发货单在取消失败时，会重置为等待发货状态;
	        		 * 2.这里因为发货单在执行定时任务时设置取消失败的原因，
	        		 * 	  故此处暂时没办法返回取消失败的原因 ;
	        		 */
	        		retList.add(generateVo(item, CANCEL_FAILED, null));
	        	}

	        }

		}
		return retList;
	}

	private AfterSaleNoticeWmsResultVO generateVo (AfterSaleNoticeWmsForm item, String flg, String msg) {
		AfterSaleNoticeWmsResultVO vo = new AfterSaleNoticeWmsResultVO();
    	vo.setAfterSaleCode(item.getAfterSaleCode());
    	vo.setFlg(flg);
    	vo.setScmShopOrderCode(item.getScmShopOrderCode());
    	vo.setSkuCode(item.getSkuCode());
    	vo.setMsg(msg);
		return vo;
	}

	private void updateOutboundDetailState(OutboundOrder order, String state, String deliveryOrderCode, String skuCode) {

        Example example = new Example(OutboundDetail.class);
        Example.Criteria cra = example.createCriteria();
        cra.andEqualTo("outboundOrderCode", order.getOutboundOrderCode());
        cra.andNotEqualTo("skuCode", skuCode);// 取消的商品过滤
        OutboundDetail detail = new OutboundDetail();
        detail.setStatus(state);

        detailService.updateByExampleSelective(detail, example);

        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setId(order.getId());
        outboundOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        //找出发货通知单编号下所有记录，更新出库通知单状态
        List<OutboundDetail> list = detailService.selectByExample(example);
        outboundOrder.setStatus(orderService.getOutboundOrderStatusByDetail(list));

        outboundOrder.setWmsOrderCode(deliveryOrderCode);
        orderService.updateByPrimaryKeySelective(outboundOrder);

	}
	
}
