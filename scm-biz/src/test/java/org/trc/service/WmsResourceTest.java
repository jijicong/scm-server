package org.trc.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.impl.impower.AclUserAccreditInfoBiz;
import org.trc.biz.impower.IAclWmsUserAccreditInfoBiz;
import org.trc.biz.impower.IWmsResourceBiz;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.UserAccreditInfoException;

import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class WmsResourceTest {
    @Autowired
    private IWmsResourceBiz wmsResourceBiz;
    @Autowired
    private IAclWmsUserAccreditInfoBiz aclWmsUserAccreditInfoBiz;

    private Logger LOGGER = LoggerFactory.getLogger(WmsResourceTest.class);

    @Test
    public void wmsResource(){
        System.out.println(JSON.toJSONString(wmsResourceBiz.queryWmsResource()));
    }

    @Test
    public void wmsResource1(){
        String s= "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
        System.out.println(Pattern.matches(s, "15757195796"));
    }


    @Test
    public void boom(){

        LOGGER.info("boom资源正在准备...倒计时10秒");
        for (int i = 10; i >0 ; i--) {
            LOGGER.info("距离boom还有"+i+"秒");
        }
        LOGGER.info("boom资源准备完毕!");
        LOGGER.info("boom======>>>>>>");


    }
}

