package org.trc.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.AppResult;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class WarehouseApiServiceTest {

    @Autowired
    private IWarehouseApiService warehouseApiService;

    @Test
    public void orderCancelTest(){
        ScmOrderCancelRequest orderCancelRequest = new ScmOrderCancelRequest();
        orderCancelRequest.setOrderCode("11111");
//        AppResult<String> appResult = warehouseApiService.orderCancel(orderCancelRequest);
//        System.out.println(JSON.toJSONString(appResult));
    }

}
