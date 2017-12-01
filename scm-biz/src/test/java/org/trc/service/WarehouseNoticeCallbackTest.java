package org.trc.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.config.WarehouseNoticeCallback;
import org.trc.service.config.IWarehouseNoticeCallbackService;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class WarehouseNoticeCallbackTest {
	@Autowired
	IWarehouseNoticeCallbackService callbackService;
	
	@Test
	public void testInsert () {
//		WarehouseNoticeCallback record = new WarehouseNoticeCallback();
//		record.setCreateTime(new Date());
//		record.setRequestCode("001");
//		record.setRequestParams("{test:test}");
//		record.setState(0);
//		record.setWarehouseCode("00001");
//		callbackService.insert(record);
		callbackService.recordCallbackLog("{test:test}", null, "0012", "2121");
	}
}
