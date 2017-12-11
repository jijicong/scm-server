package org.trc.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.impl.order.ScmOrderBiz;
import org.trc.biz.impl.outbound.OutBoundOrderBiz;
import org.trc.domain.order.WarehouseOrder;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class OutBoundNoticeChannelTest {

	
	@Autowired
	OutBoundOrderBiz biz;
	@Autowired
	ScmOrderBiz scmBiz;
//	private 
	@Test
	public void testLogisticsNotice () throws Exception {
		String reqStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><packages><package><expressCode>Y12323</expressCode><logisticsCode>AMAZON</logisticsCode><logisticsName>亚马逊</logisticsName><items><item><quantity>7</quantity><itemCode>111</itemCode></item><item><quantity>7</quantity><itemCode>110</itemCode></item></items></package></packages><deliveryOrder><deliveryOrderCode>FHTZ2017111112345</deliveryOrderCode><warehouseCode></warehouseCode><orderType>JYCK</orderType><operateTime>2017-11-11 11:11:11</operateTime></deliveryOrder></request>";
	
		biz.updateOutboundDetail(reqStr);
	}
	
	@Test
	public void testOutBoundOrderNotice () throws Exception {
		String reqStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><packages><package><expressCode>Y12323</expressCode><logisticsCode>AMAZON</logisticsCode><logisticsName>亚马逊</logisticsName><items><item><quantity>7</quantity><itemCode>111</itemCode></item><item><quantity>7</quantity><itemCode>110</itemCode></item></items></package></packages><deliveryOrder><deliveryOrderCode>FHTZ2017111112345</deliveryOrderCode><warehouseCode></warehouseCode><orderType>JYCK</orderType><operateTime>2017-11-11 11:11:11</operateTime></deliveryOrder></request>";
		
		Set<String> shopCodes = new HashSet<>();
		shopCodes.add("44444xxxxxxxxx000012");
//		shopCodes.add(e);
		List<WarehouseOrder> warehouseOrderList = new ArrayList<>();
		WarehouseOrder warehouse = new WarehouseOrder();
		WarehouseOrder warehouse_two = new WarehouseOrder();
		warehouse.setWarehouseOrderCode("CKDD1201712070000576");
		warehouse.setShopOrderCode("44444xxxxxxxxx000012");
		warehouse.setPlatformOrderCode("1612141048495092");
		warehouse_two.setWarehouseOrderCode("CKDD1201712080000577");
		warehouse_two.setShopOrderCode("44444xxxxxxxxx000012");
		warehouse_two.setPlatformOrderCode("1612141048495092");
		warehouseOrderList.add(warehouse);
		warehouseOrderList.add(warehouse_two);
		//scmBiz.notifyChannelSelfPurchaseSubmitOrderResult(shopCodes, warehouseOrderList);
	}
}
