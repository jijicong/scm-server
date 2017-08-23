package org.trc.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.trc.ITrcBiz;
import org.trc.domain.category.Property;
import org.trc.form.trcForm.PropertyFormForTrc;
import org.trc.util.Pagenation;

/**
 * Created by sone on 2017/8/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class TrcTest {

    @Autowired
    private ITrcBiz trcBiz;


    @Test
    public void testProperty(){

        //PropertyFormForTrc queryModel, Pagenation<Property> page
        PropertyFormForTrc propertyFormForTrc = new PropertyFormForTrc();
        propertyFormForTrc.setFlag("1");
        Pagenation<Property> page = new Pagenation<Property>();
        page.setStart(0);
        page.setPageSize(10);
        page.setPageNo(0);
        try{
            Object obj = trcBiz.propertyPage(propertyFormForTrc,page);
            String json = JSON.toJSONString(obj);
            String msg = "msg";
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
