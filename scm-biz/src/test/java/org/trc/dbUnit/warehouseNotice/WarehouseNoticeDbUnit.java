package org.trc.dbUnit.warehouseNotice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.impl.warehouseNotice.WarehouseNoticeBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.service.BaseTest;
import org.trc.service.IQimenService;
import org.trc.service.impl.purchase.WarehouseNoticeService;
import org.trc.util.AppResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.response.EntryorderCreateResponse;

public class WarehouseNoticeDbUnit extends BaseTest {
	
	@Autowired
	private IWarehouseNoticeBiz warehouseNoticeBiz;
	@Autowired
	private WarehouseNoticeService warehouseNoticeService;
	
    // private static final String WAREHOUSE_NOTICE = "warehouse_notice";
	static ClassLoader loader = Thread.currentThread().getContextClassLoader();
	/**
	 * 入库单通知收货-成功
	 * @throws Exception 
	 */
	@Test
	public void receiptAdvice_success () throws Exception {
		/**  入库通知成功  **/
		mockQimenEntryOrderCreate();
		warehouseNoticeBiz.receiptAdvice(createWarehouseNotice(), createAclUserAccreditInfo());
		
		/**
		 * 采购单状态修改为 已通知
		 **/
        ReplacementDataSet expResult = createDataSet(loader.getResourceAsStream("warehouseNotice/expPurchaseOrder.xml"));
        expResult.addReplacementObject("null", null);
        assertDataSet("purchase_order","select * from purchase_order where id = 365",expResult,conn);
        
        /**
         * 更新入库单为 待仓库反馈状态 
         **/
        ReplacementDataSet expResult1 = createDataSet(loader.getResourceAsStream("warehouseNotice/expWarehouseNoticeData.xml"));
        expResult1.addReplacementObject("null", null);
        assertDataSet("warehouse_notice","select * from warehouse_notice where id = 35",expResult1,conn);
        
		/**
		 * 更新入库明细表中的商品为 待仓库反馈状态
		 **/
        ReplacementDataSet expResult3 = createDataSet(loader.getResourceAsStream("warehouseNotice/expWarehouseNoticeDetailsData.xml"));
        expResult3.addReplacementObject("null", null);
        assertDataSet("warehouse_notice_details",
        		"select * from warehouse_notice_details where warehouse_notice_code = 'CGRKTZ2017120500166'",expResult3,conn);
       
        /**
         * 更新相应sku的在途库存数
         **/
        ReplacementDataSet expResult4 = createDataSet(loader.getResourceAsStream("warehouseNotice/expSkuStock.xml"));
        expResult4.addReplacementObject("null", null);
        assertDataSet("sku_stock","select * from sku_stock where id = 8",expResult4,conn);
		
	}
	
	/**
	 * 入库单通知收货-查询采购单失败(采购单状态不合法)
	 * @throws Exception 
	 */
	@Test
	public void receiptAdvice_purchaseOrderStatusErr () throws Exception {
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void mockQimenEntryOrderCreate() {
		
		IQimenService qimenService = mock(IQimenService.class);
		warehouseNoticeBiz.setQimenService(qimenService);
		AppResult ret = new AppResult();
		ret.setAppcode("200");
		ret.setDatabuffer("入库单创建成功");
		String body = "{\"flag\":\"success\",\"code\":\"200\",\"success\":true,\"entryOrderId\":\"WMS-CGRKTZ2017120500166\",\"message\":\"入库单创建成功\",\"body\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?><response>   <flag>success</flag>    <code>200</code>    <message>入库货单创建成功</message>    <entryOrderId>dbtest001</entryOrderId> </response>\"}";
		ret.setResult(JSON.parseObject(body));
		when(qimenService.entryOrderCreate(any(EntryorderCreateRequest.class))).thenReturn(ret);	
	}

	private WarehouseNotice createWarehouseNotice () {
		WarehouseNotice notice = new WarehouseNotice();
		notice.setId(35L);
		WarehouseNotice one = warehouseNoticeService.selectOne(notice);
		return one;
	}
	
    private AclUserAccreditInfo createAclUserAccreditInfo () {
        AclUserAccreditInfo info = new AclUserAccreditInfo();
        info.setId(1L);
        info.setChannelId(2L);
        info.setChannelName("小泰乐活");
        info.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        info.setPhone("15757195796");
        info.setName("admin");
        info.setUserType("mixtureUser");
        info.setChannelCode("QD002");
        info.setRemark("admin");
        info.setIsValid("1");
        info.setIsDeleted("0");
        info.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        return info;
    }
    
    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("purchase_order");
        exportData(tableNameList, "src/test/resources/warehouseNotice/expPurchaseOrder.xml");
    }
    
}
