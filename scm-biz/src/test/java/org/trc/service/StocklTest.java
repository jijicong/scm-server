package org.trc.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.service.impl.warehouseNotice.WarehouseNoticeDetailsService;
import org.trc.util.lock.RedisLock;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class StocklTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private RedisLock redisLock;
    @Autowired
    private WarehouseNoticeDetailsService detail;


    @Test
    public void Stock()throws  Exception{
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            String identifier = redisLock.Lock("XXXXX", 200, 1000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                    redisLock.releaseLock("XXXXX",identifier);
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
