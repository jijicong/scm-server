package org.trc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;

/**
 * Created by hzcyn on 2018/6/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:config/resource-context.xml"})
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class AllocateOrderTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private IAllocateOutOrderBiz allocateOutOrderBiz;

    @Test
    public void updateAllocateOutDetail(){
        allocateOutOrderBiz.updateAllocateOutDetail();
    }
}
