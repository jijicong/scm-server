package org.trc.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserChannelSellService;
import org.trc.util.lock.StockLock;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
//@Transactional
public class StocklTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IAclUserChannelSellService aclUserChannelSellService;

    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private IChannelSellChannelService channelSellChannelService;
    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IAclUserAccreditInfoBiz userAccreditInfoBiz;
    @Autowired
    private StockLock stockLock;


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

}
