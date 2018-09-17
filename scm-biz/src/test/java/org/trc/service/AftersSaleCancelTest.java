package org.trc.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.domain.order.OutboundDetail;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.afterSale.AfterSaleNoticeWmsForm;
import org.trc.form.afterSale.AfterSaleNoticeWmsResultVO;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.outbound.IOutboundDetailService;

import com.alibaba.fastjson.JSON;

import tk.mybatis.mapper.entity.Example;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class AftersSaleCancelTest {

	@Autowired
	private IAfterSaleOrderService afterSaleOrderService;
	@Autowired
	private IOutboundDetailService outboundDetailService;
	@Autowired
	private IOutBoundOrderBiz outBoundOrderBiz;
	
	@Test
	public void deliveryCancel () {
		// ziying
//		Map<String, Object> result = afterSaleOrderService.deliveryCancel("ZY2018091400003806", "SP0201807090000803");
//		Map<String, Object> result = afterSaleOrderService.deliveryCancel("1701111457082438", "SP0201708140000157");
//		Map<String, Object> result = afterSaleOrderService.deliveryCancel("ZY2018091400058148", "SP0201809060002365");
		// jingdong
		Map<String, Object> result = afterSaleOrderService.deliveryCancel("ZY2018090600058068", "SP0201809060002373");
		System.out.println(result.get("flg"));
		System.out.println(result.get("msg"));
	}
	
	@Test
	public void cancelReuslt () {
		List<AfterSaleNoticeWmsForm> formList = new ArrayList<>();
		AfterSaleNoticeWmsForm item = new AfterSaleNoticeWmsForm();
//		item.setAfterSaleCode();
		item.setScmShopOrderCode("ZY2018090600058068");
		item.setSkuCode("SP0201809060002373");
		formList.add(item);
		List<AfterSaleNoticeWmsResultVO> result = afterSaleOrderService.deliveryCancelResult(formList);
		
		System.out.println(JSON.toJSONString(result));
	}
	
	@Test
	public void cancelTask () throws IOException {
		outBoundOrderBiz.retryCancelOrder();
		//System.in.read();
	}
	@Test
	public void orderDetailTask () throws IOException {
		outBoundOrderBiz.updateOutboundDetail();
		System.in.read();
	}
	
	@Test
	public void test () {
//        OutboundDetail outboundDetail1 = new OutboundDetail();
//        outboundDetail1.setOutboundOrderCode("ZYFHTZ2018090648419");
//        outboundDetail1.setCancelFlg(null);
//        List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail1);
	      Example example = new Example(OutboundDetail.class);
	      Example.Criteria cra = example.createCriteria();
	      cra.andEqualTo("outboundOrderCode", "ZYFHTZ2018090648419");
	      cra.andIsNull("cancelFlg");

	      List<OutboundDetail> outboundDetailList = outboundDetailService.selectByExample(example);
          System.out.println(outboundDetailList.size());
	}
	
	@Test
	public void testNull () {
    	Example exampleOrder = new Example(OutboundDetail.class);
    	Example.Criteria criteriaOrder = exampleOrder.createCriteria();
    	criteriaOrder.andEqualTo("outboundOrderCode", "FHTZ2018010200006");
    	criteriaOrder.andEqualTo("cancelFlg", ZeroToNineEnum.ZERO.getCode());// 取消中
    	List<OutboundDetail> detailList = outboundDetailService.selectByExample(exampleOrder);
    	if (CollectionUtils.isNotEmpty(detailList)) {
    		for (OutboundDetail detail : detailList) {
    			detail.setUpdateTime(Calendar.getInstance().getTime());
    			detail.setCancelFlg(null); // 取消成功
    			outboundDetailService.updateByPrimaryKey(detail);
    		}
    	}
	}
	

}
