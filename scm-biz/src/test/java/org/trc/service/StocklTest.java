package org.trc.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.impl.warehouseNotice.WarehouseNoticeDetailsService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserChannelSellService;
import org.trc.util.lock.StockLock;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class StocklTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private StockLock stockLock;
    @Autowired
    private WarehouseNoticeDetailsService detail;


    @Test
    public void Stock()throws  Exception{
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            String identifier = stockLock.Lock("XXXXX");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                    stockLock.releaseLock("XXXXX",identifier);
                }
            }).start();
        }
        System.in.read();
    }
    
    @Test
    public void airStockTest () {
    	WarehouseNoticeDetails record = new WarehouseNoticeDetails();
    	record.setId(24l);
    	record.setSkuName("henhao.6666");
		detail.updateByPrimaryKeySelective(record);
    }

}
