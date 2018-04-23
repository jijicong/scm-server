package org.trc.service;


import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.domain.impower.AclUserAccreditInfo;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class OutboundOrderTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private IOutBoundOrderBiz outBoundOrderBiz;
	
	
    /**
     * 发货单取消
     */
    @Test
    public void testOrderCancel(){
    	outBoundOrderBiz.orderCancel(68L, "取消发货", createAclUserAccreditInfo());
    }
    
    /**
     * 获取物流信息
     * @throws IOException 
     */
    @Test
    public void testUpdateOutboundDetail() throws IOException{
    	outBoundOrderBiz.updateOutboundDetail();
    	System.in.read();
    }
    
    
    
    private AclUserAccreditInfo createAclUserAccreditInfo () {
        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setId(1L);
        info.setChannelId(2L);
        info.setChannelName("小泰乐活");
        info.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        info.setPhone("15757195796");
        info.setName("admin");
        info.setUserType("mixtureUser");
        info.setChannelCode("QD002");
        info.setRemark("admin");
        info.setIsValid("1");
        info.setIsDeleted("0");
        info.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        return info;
    }
    
    
}
