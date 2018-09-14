package org.trc.service;


import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.service.afterSale.IAfterSaleOrderService;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class AftersSaleCancelTest {

	@Autowired
	private IAfterSaleOrderService afterSaleOrderService;
	
	@Test
	public void deliveryCancel () {
//		Map<String, Object> result = afterSaleOrderService.deliveryCancel("ZY2018091400003806", "SP0201807090000803");
		Map<String, Object> result = afterSaleOrderService.deliveryCancel("1701111457082438", "SP0201708140000157");
		System.out.println(result.get("flg"));
		System.out.println(result.get("msg"));
	}

}
