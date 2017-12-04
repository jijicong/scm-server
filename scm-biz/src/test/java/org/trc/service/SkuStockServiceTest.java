package org.trc.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.form.goods.RequsetUpdateStock;
import org.trc.service.goods.ISkuStockService;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class SkuStockServiceTest {
	@Autowired
	ISkuStockService skuStockService;
	
	/**
	 * 困存更新公共方法
	 * @throws Exception
	 */
	@Test
	public void testUpdateStock () throws Exception {
		List<RequsetUpdateStock> stockList = new ArrayList<RequsetUpdateStock>();
		RequsetUpdateStock stock = new RequsetUpdateStock();
		stock.setNum(100l);
		stock.setChannelCode("YWX001");
		stock.setSkuCode("SP0201707240000002");
		stock.setWarehouseCode("CK00009");
		//stock.setUpdateType(StockUpdateTypeEnum.MINUS);
		stock.setStockType("realInventory");
		stockList.add(stock);
		skuStockService.updateSkuStock(stockList);
	}
}
