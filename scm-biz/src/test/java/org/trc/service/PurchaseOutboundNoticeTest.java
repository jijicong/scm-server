package org.trc.service;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class PurchaseOutboundNoticeTest {
	
    @Autowired
    private IPurchaseOutboundNoticeBiz noticeBiz;


    @Test
    public void retryOrderTest() throws IOException {
    	noticeBiz.retryCancelOrder();
    	System.in.read();
    }



}

