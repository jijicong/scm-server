package org.trc.service;


import com.alibaba.fastjson.JSON;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.impl.warehouseNotice.WarehouseNoticeBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
//@Transactional
public class WarehouseNoticeTest extends AbstractJUnit4SpringContextTests {
    @Test
    public void TestBean(){
        String json = "[{\"actualQty\":11,\"batchCode\":\"20111111211\",\"batchs\":[{\"actualQty\":11,\"batchCode\":\"20111111211\",\"expireDate\":\"2015-09-12\",\"inventoryType\":\"ZP\",\"produceCode\":\"111231\",\"productDate\":\"2015-09-12\"}],\"expireDate\":\"2015-09-12\",\"extCode\":\"20111111211\",\"inventoryType\":\"ZP\",\"itemCode\":\"20111111211\",\"itemId\":\"20111111211\",\"itemName\":\"20111111211\",\"orderLineNo\":\"20111111211\",\"orderSourceCode\":\"20111111211\",\"ownerCode\":\"20111111211\",\"planQty\":11,\"produceCode\":\"20111111211\",\"productDate\":\"2015-09-12\",\"subSourceCode\":\"20111111211\"}," +
                " {\n" +
                "            \"actualQty\":11,\n" +
                "            \"batchCode\":\"20111111211\",\n" +
                "            \"batchs\":[\n" +
                "                {\n" +
                "                    \"actualQty\":11,\n" +
                "                    \"batchCode\":\"20111111211\",\n" +
                "                    \"expireDate\":\"2015-09-12\",\n" +
                "                    \"inventoryType\":\"ZP\",\n" +
                "                    \"produceCode\":\"111231\",\n" +
                "                    \"productDate\":\"2015-09-12\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"expireDate\":\"2015-09-12\",\n" +
                "            \"extCode\":\"20111111211\",\n" +
                "            \"inventoryType\":\"ZP\",\n" +
                "            \"itemCode\":\"20111111211\",\n" +
                "            \"itemId\":\"20111111211\",\n" +
                "            \"itemName\":\"20111111211\",\n" +
                "            \"orderLineNo\":\"20111111211\",\n" +
                "            \"orderSourceCode\":\"20111111211\",\n" +
                "            \"ownerCode\":\"20111111211\",\n" +
                "            \"planQty\":11,\n" +
                "            \"produceCode\":\"20111111211\",\n" +
                "            \"productDate\":\"2015-09-12\",\n" +
                "            \"subSourceCode\":\"20111111211\"\n" +
                "        }]";
        EntryorderConfirmRequest confirmRequest = new  EntryorderConfirmRequest();

        List<EntryorderConfirmRequest.OrderLine> orderLines = JSON.parseArray(json,EntryorderConfirmRequest.OrderLine.class);
        EntryorderConfirmRequest.EntryOrder entryOrder  = new  EntryorderConfirmRequest.EntryOrder();
        //采购入库单编号
        entryOrder.setEntryOrderCode("CGRKTZ2017032700010");
        //货主编码
        entryOrder.setOwnerCode("YWX001");
        //采购单编号
        entryOrder.setPurchaseOrderCode("CGD2017032300010");
        //仓库编码
        entryOrder.setWarehouseCode("CK00001");
        //订单创建时间
        entryOrder.setOrderCreateTime("2017-11-28");
        //业务类型
        entryOrder.setOrderType("CGRK");
        //要求到货日期(预期到货时间)
        entryOrder.setExpectStartTime("2017-11-29");
        //截止到货日期(最迟预期到货时间)
        entryOrder.setExpectEndTime("2017-11-30");
        //运单号
        entryOrder.setExpressCode("101111100110");
        //供应商编码
        entryOrder.setSupplierCode("GYS000001");
        //供应商名称
        entryOrder.setSupplierName("苹果中国供应商");


        //发件人信息
        EntryorderConfirmRequest.SenderInfo senderInfo = new EntryorderConfirmRequest.SenderInfo();
        //发件人姓名
        senderInfo.setName("小米");
        //发件方手机
        senderInfo.setMobile("15012345678");
        //发件方省份
        senderInfo.setProvince("浙江");
        //发件方城市
        senderInfo.setCity("杭州");
        //发件方详细地址
        senderInfo.setDetailAddress("滨江区上峰电商产业园");
        //备注
        senderInfo.setRemark("9257");
        entryOrder.setSenderInfo(senderInfo);
        confirmRequest.setOrderLines(orderLines);
        confirmRequest.setEntryOrder(entryOrder);

        System.out.println(JSON.toJSONString(confirmRequest));
    }
    
    @Autowired
    private IWarehouseNoticeBiz noticeBiz;
    /**
     * 定时任务查询满足条件的入库单，更新入库单状态
     */
    @Test
    public void testEntryOrderDetailQuery () {
    	noticeBiz.updateStock();
    }
    
    
}
