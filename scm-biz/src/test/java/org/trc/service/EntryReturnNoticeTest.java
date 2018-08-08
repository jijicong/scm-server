package org.trc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;
import org.trc.domain.impower.AclUserAccreditInfo;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class EntryReturnNoticeTest {
	
	@Autowired
	private IPurchaseOutboundNoticeBiz biz;
	
	
	@Test
	public void entryReturnNoticeCreate () {
		AclUserAccreditInfo user = new AclUserAccreditInfo();
		user.setUserId("userIdTest");
		String code = "THCKTZ2018080800017";
		biz.noticeOut(code, user);
	}
	
	
	
}
