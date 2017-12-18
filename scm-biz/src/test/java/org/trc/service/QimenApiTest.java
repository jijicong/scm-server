package org.trc.service;

import java.util.ArrayList;
import java.util.List;

import com.qimen.api.request.ItemsSynchronizeRequest;
import com.qimen.api.response.ItemsSynchronizeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.util.AppResult;

import com.alibaba.fastjson.JSON;
import com.qimen.api.request.OrderPendingRequest;
import com.qimen.api.request.ReturnorderCreateRequest;
import com.qimen.api.request.ReturnorderCreateRequest.OrderLine;
import com.qimen.api.request.ReturnorderCreateRequest.SenderInfo;
import com.qimen.api.request.StockoutCreateRequest;
import com.qimen.api.response.OrderPendingResponse;
import com.qimen.api.response.ReturnorderCreateResponse;
import com.qimen.api.response.StockoutCreateResponse;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class QimenApiTest {
    @Autowired
    IQimenService qimenService;
    
    /**
     * 退货单创建
     */
    @Test
    public void testReturnOrderCreate() {
    	ReturnorderCreateRequest req = new ReturnorderCreateRequest();
    	ReturnorderCreateRequest.ReturnOrder retOrder = new ReturnorderCreateRequest.ReturnOrder();
    	retOrder.setReturnOrderCode("rkd001");
    	retOrder.setWarehouseCode("warehouse001");
    	retOrder.setPreDeliveryOrderCode("pre001");
    	retOrder.setLogisticsCode("SF");
    	retOrder.setPreDeliveryOrderId("predelivery001");
    	
    	SenderInfo sender = new ReturnorderCreateRequest.SenderInfo();
    	sender.setName("jordon");
    	sender.setMobile("13214567869");
    	sender.setProvince("浙江");
    	sender.setCity("温州	");
    	sender.setDetailAddress("人民路220号");
    	
    	retOrder.setSenderInfo(sender);
    	req.setReturnOrder(retOrder);
    	
    	List<OrderLine> list = new ArrayList<>();
    	OrderLine line = new OrderLine();
    	line.setOwnerCode("ower001");
    	line.setItemCode("it001");
    	line.setPlanQty(12L);
    	line.setItemId("ite001");
    	list.add(line);
    	req.setOrderLines(list);
    	//retOrder.setPreDeliveryOrderCode(preDeliveryOrderCode);
		AppResult<ReturnorderCreateResponse> ret = qimenService.returnOrderCreate(req);
		System.out.println(JSON.toJSONString(ret));
//		return {"appcode":"200","databuffer":"退货单创建成功","result":{"flag":"success","code":"200","success":true,"returnOrderId":"WMS-rkd001","message":"退货单创建成功","body":"<?xml version=\"1.0\" encoding=\"utf-8\"?><response>   <flag>success</flag>    <code>200</code>    <message>退货单创建成功</message>    <returnOrderId>WMS-rkd001</returnOrderId> </response>"}}
		
    }
    
    /**
     * 出库单创建
     */
    @Test
    public void testStockoutCreate() {
    	StockoutCreateRequest req = new StockoutCreateRequest();
    	StockoutCreateRequest.DeliveryOrder order = new StockoutCreateRequest.DeliveryOrder();
    	order.setDeliveryOrderCode("deli001");
    	order.setOrderType("PTCK");
    	order.setWarehouseCode("ck001");
    	order.setCreateTime("2016-09-09 12:00:00");
    	
    	StockoutCreateRequest.SenderInfo sender = new StockoutCreateRequest.SenderInfo();
    	sender.setName("jordon");
    	sender.setMobile("13214567869");
    	sender.setProvince("浙江");
    	sender.setCity("温州	");
    	sender.setDetailAddress("人民路220号");
    	
    	StockoutCreateRequest.ReceiverInfo receiver = new StockoutCreateRequest.ReceiverInfo();
    	receiver.setName("cctv");
    	receiver.setMobile("13214567169");
    	receiver.setProvince("新疆");
    	receiver.setCity("阿克苏");
    	receiver.setDetailAddress("葡萄路220号");
    	
    	order.setSenderInfo(sender);
    	order.setReceiverInfo(receiver);
    	req.setDeliveryOrder(order);
    	
      	List<StockoutCreateRequest.OrderLine> list = new ArrayList<>();
      	StockoutCreateRequest.OrderLine line = new StockoutCreateRequest.OrderLine();
    	line.setOwnerCode("ower001");
    	line.setItemCode("it001");
    	line.setPlanQty("12");
    	line.setItemId("ite001");
    	list.add(line);
    	req.setOrderLines(list);
    	
		AppResult<StockoutCreateResponse> ret = qimenService.stockoutCreate(req);
		System.out.println(JSON.toJSONString(ret));
		//return {"appcode":"200","databuffer":"出库单创建成功","result":{"flag":"success","code":"200","createTime":"2017-12-13 17:10:29","success":true,"message":"出库单创建成功","body":"<?xml version=\"1.0\" encoding=\"utf-8\"?><response>  <flag>success</flag>  <code>200</code>  <message>出库单创建成功</message>  <deliveryOrderId>WMS-deli001</deliveryOrderId>  <createTime>2017-12-13 17:10:29</createTime></response>","deliveryOrderId":"WMS-deli001"}}
    	
    }
    

	/**
	 * 单据挂起（恢复）接口
	 */
	@Test
	public void orderPending() {
		OrderPendingRequest req = new OrderPendingRequest();
		req.setActionType("pending");
		req.setWarehouseCode("ck111");
		req.setOrderCode("order111");
		req.setOrderId("orderId111");
		req.setOrderType("JYCK");
		AppResult<OrderPendingResponse> ret = qimenService.orderPending(req);
		System.out.println(JSON.toJSONString(ret));
		//return {"appcode":"200","databuffer":"订单pending成功","result":{"flag":"success","code":"200","success":true,"message":"订单pending成功","body":"<?xml version=\"1.0\" encoding=\"utf-8\"?><response>    <flag>success</flag>    <code>200</code>    <message>订单pending成功</message></response>"}}
		
	}


	/**
	 * 商品同步接口
	 */
	@Test
	public void itemsSync(){
		ItemsSynchronizeRequest req = new ItemsSynchronizeRequest();

		List<ItemsSynchronizeRequest.Item> itemList = new ArrayList<>();
		ItemsSynchronizeRequest.Item item = new ItemsSynchronizeRequest.Item();
		item.setItemCode("skuCode");
		item.setGoodsCode("itemId");
		item.setItemName("itemName");
		item.setBarCode("barCode");
		item.setSkuProperty("specNatureInfo");
		item.setItemType("itemType");
		itemList.add(item);

		req.setItems(itemList);
		req.setOwnerCode("ownerCode");
		req.setWarehouseCode("warehouseCode");
		req.setActionType("add");

		AppResult<ItemsSynchronizeResponse> ret = qimenService.itemsSync(req);
		System.out.println(JSON.toJSONString(ret));
	}

}
