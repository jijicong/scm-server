package org.trc;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.goods.SkuStock;
import org.trc.mapper.goods.ISkuStockMapper;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class TestSql {
	@Autowired
	ISkuStockMapper skuStockMapper;
	@Test
	public void test () {
		SkuStock record = new SkuStock();
		record.setSkuCode("SPU2017072500003");
		List<SkuStock> select = skuStockMapper.select(record);
		System.err.println(select.size());
	}
}
