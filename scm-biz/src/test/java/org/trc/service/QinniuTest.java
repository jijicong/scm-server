package org.trc.service;

import com.qiniu.storage.model.FetchRet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.qinniu.IQinniuBiz;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class QinniuTest  extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IQinniuBiz qinniuBiz;

    @Test
    public void testFetch(){
        try {
            String ss = qinniuBiz.fetch("http://www.cnbuyers.cn/data/files/store_31094/goods_83/201609281131237057.jpg", "tttt/201609281131237057.jpg");
            System.out.println(ss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
