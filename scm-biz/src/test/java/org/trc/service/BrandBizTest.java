package org.trc.service;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.category.IBrandBiz;
import org.trc.biz.impower.IAclResourceBiz;
import org.trc.domain.category.Brand;
import org.trc.enums.SupplierOrderStatusEnum;
import org.trc.form.category.BrandForm;
import org.trc.util.Pagenation;
import org.trc.util.TransportClientUtil;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by hzwyz on 2017/5/27 0027.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class BrandBizTest {
    @Autowired
    IBrandBiz iBrandBiz;
    @Autowired
    IAclResourceBiz aclResourceBiz;
    /**
     * 品牌信息分页
     */
    @Test
    public void brandPageTest(){
        BrandForm form = new BrandForm();
        Pagenation<Brand > page = new Pagenation<>();
        try {
            iBrandBiz.brandPage(form, page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test(){
        String name = SupplierOrderStatusEnum.getSupplierOrderStatusEnumByCode("4").getName();

        System.out.println(name);
    }

    public void updateEsBrand(Brand brand) throws Exception{
        TransportClient clientUtil = TransportClientUtil.getTransportClient();

        UpdateRequest updateRequest = new UpdateRequest("item_brand", "item_brand_type", String.valueOf(brand.getId()))
                .doc(jsonBuilder()
                        .startObject()
                        .field("name", brand.getName())
                        .field("alise", brand.getAlise())
                        .field("web_url", brand.getWebUrl())
                        .endObject());
        clientUtil.update(updateRequest).get();

    }

}
