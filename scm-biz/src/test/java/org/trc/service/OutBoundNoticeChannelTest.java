package org.trc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.impl.outbound.OutBoundOrderBiz;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class OutBoundNoticeChannelTest {

	
	@Autowired
	OutBoundOrderBiz biz;
//	private 
	@Test
	public void testNotice () throws Exception {
		String reqStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><packages><package><expressCode>Y12323</expressCode><logisticsCode>AMAZON</logisticsCode><logisticsName>亚马逊</logisticsName><items><item><quantity>7</quantity><itemCode>111</itemCode></item><item><quantity>7</quantity><itemCode>110</itemCode></item></items></package></packages><deliveryOrder><deliveryOrderCode>FHTZ2017111112345</deliveryOrderCode><warehouseCode></warehouseCode><orderType>JYCK</orderType><operateTime>2017-11-11 11:11:11</operateTime></deliveryOrder></request>";
	
		biz.updateOutboundDetail(reqStr);
	}
}
