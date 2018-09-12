package org.trc.service.impl.AfterSale;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.enums.OrderCancelResultEnum;
import org.trc.exception.OutboundOrderException;
import org.trc.mapper.outbound.IOutboundDetailMapper;
import org.trc.mapper.outbound.IOutboundOrderMapper;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.BaseService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

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
	
	private Logger logger = LoggerFactory.getLogger(AfterSaleOrderService.class);
		
	@Override
	public Map<String, Object> deliveryCancel(String scmShopOrderCode, String skuCode) throws Exception{
        /**
         * 参数校验
         */
		checkParam(scmShopOrderCode, skuCode);
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
        	throw new IllegalArgumentException("发货单不能为空并且发货单个数必须为1!");
        }
        
        String outboundOrderCode = detailList.get(0).getOutboundOrderCode();
        OutboundOrder targetOrder = orderList.stream().filter(order ->
        	outboundOrderCode.equals(order.getOutboundOrderCode())).findAny().orElse(null);

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
//		if (skuList == null || skuList.size() != 1) {
//			throw new IllegalArgumentException("商品列表不能为空并且长度必须为1!");
//		}
		
	}
	
	
	
	
}
