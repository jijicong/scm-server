package org.trc.service.impl.AfterSale;


import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.ItemNoticeStateEnum;
import org.trc.enums.ItemTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.mapper.outbound.IOutboundDetailMapper;
import org.trc.mapper.outbound.IOutboundOrderMapper;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.impl.BaseService;
import org.trc.util.AssertUtil;

import tk.mybatis.mapper.entity.Example;


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
	
	@Override
	public boolean deliveryCancel(String scmShopOrderCode, List<String> skuList) {
        /**
         * 根据系统订单号获取发货单号
         **/
        OutboundOrder orderRecord = new OutboundOrder();
        orderRecord.setScmShopOrderCode(scmShopOrderCode);
        List<OutboundOrder> orderList = orderMapper.select(orderRecord);
        AssertUtil.notNull(orderList, "根据系统订单号" + scmShopOrderCode + "查询发货单为空!");
        
        String skuCode = skuList.get(0); // 取消目前只支持一个退货单一个商品操作
        List<String> orderCodeList = orderList.stream().map(OutboundOrder :: <String>getOutboundOrderCode).collect(toList());
        OutboundDetail detailRecord = new OutboundDetail();
        detailRecord.setSkuCode(skuCode);
        
        Example example = new Example(OutboundDetail.class);
        Example.Criteria cra = example.createCriteria();
        cra.andEqualTo("skuCode", skuCode);
        cra.andIn("outboundOrderCode", orderCodeList);
        
        List<OutboundDetail> selectByExample = orderDetailMapper.selectByExample(example);
        
        
		return false;
	}
	
	
	
	
}
