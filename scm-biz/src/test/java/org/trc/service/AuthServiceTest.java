package org.trc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impl.ConfigBiz;
import org.trc.domain.score.Auth;
import org.trc.domain.score.DictType;
import org.trc.service.impl.AuthService;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 *
 * Created by george on 2017/3/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
//@Transactional
public class AuthServiceTest extends AbstractJUnit4SpringContextTests {

    @Resource
    private AuthService authService;

    @Test
    public void test(){
        Auth auth = new Auth();
        auth.setCreateTime(Calendar.getInstance().getTime());
        auth.setChannelCode("test");
        auth.setContactsUser("15669003888");
        auth.setExchangeCurrency("TCOIN");
        auth.setPhone("15669003888");
        auth.setShopId(1l);
        auth.setUserId("2231qweasdqwed");
        authService.insert(auth);
        System.out.println(auth.getId());
    }


}
