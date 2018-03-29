package org.trc.dbUnit.outbound;

import com.alibaba.fastjson.JSON;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.OrderCancelRequest;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OutboundOrder;
import org.trc.form.warehouse.ScmDeliveryOrderDetailRequest;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.form.warehouse.ScmOrderCancelResponse;
import org.trc.service.BaseTest;
import org.trc.service.IQimenService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.AppResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hzcyn on 2018/1/15.
 */
public class OutboundDbUnit extends BaseTest {

    @Autowired
    private IOutBoundOrderBiz outBoundOrderBiz;
    @Autowired
    private IOutBoundOrderService outBoundOrderService;

    static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    /**
     * 数据准备
     * @param arr  测试数据源文件名数组
     * @throws Exception
     */
    private void preTest (String... arr) throws Exception {
        execSql(conn,"delete from outbound_order"); // 出库单
        execSql(conn,"delete from outbound_detail"); // 出库单详情
        execSql(conn,"delete from outbound_detail_logistics"); // 出库单物流
        execSql(conn,"delete from sku_stock"); // 库存表
        execSql(conn,"delete from order_item"); // 订单明细信息表

        if (null != arr && arr.length > 0) {
            for (int i = 0; i < 4; i++) {
                prepareData(conn, "outbound/pre/" + arr[i] + ".xml");
            }
        }
    }

    private void resultCompare (String caseString) throws Exception {
        /**
         * 更新发货通知单明细为已取消
         **/
        ReplacementDataSet expResult = createDataSet(loader.getResourceAsStream("outbound/exp/" +
                caseString + "/expOutboundDetail.xml"));
        expResult.addReplacementObject("null", null);
        assertDataSet("outbound_detail","select * from outbound_detail where id = 29",expResult,conn);

        /**
         * 更新发货通知单为已取消
         **/
        ReplacementDataSet expResult1 = createDataSet(loader.getResourceAsStream("outbound/exp/" +
                caseString + "/expOutboundOrder.xml"));
        expResult1.addReplacementObject("null", null);
        assertDataSet("outbound_order","select * from outbound_order where id = 29",expResult1,conn);

        /**
         * 更新冻结库存
         **/
        ReplacementDataSet expResult3 = createDataSet(loader.getResourceAsStream("outbound/exp/" +
                caseString + "/expSkuStock.xml"));
        expResult3.addReplacementObject("null", null);
        assertDataSet("sku_stock","select * from sku_stock where id = 58",expResult3,conn);

        /**
         * 更新订单信息为已取消
         **/
        ReplacementDataSet expResult4 = createDataSet(loader.getResourceAsStream("outbound/exp/" +
                caseString + "/expOrderItem.xml"));
        expResult4.addReplacementObject("null", null);
        assertDataSet("order_item","select * from order_item where id = 1816",expResult4,conn);
    }

    /**
     * case 1
     * 取消发货
     * @throws Exception
     */
    @Test
    public void receiptAdvice_success () throws Exception {
        preTest("case1/preOutboundOrder","case1/preOutboundDetail","case1/preSkuStock","case1/preOrderItem");
        mockQimene(true);
        outBoundOrderBiz.orderCancel(29L, "取消发货", createAclUserAccreditInfo());
        resultCompare("case1");
    }

    public void orderCancel()throws Exception{

    }

    private void mockQimene(Boolean isSucc) {
        IWarehouseApiService qimenService = mock(IWarehouseApiService.class);
        outBoundOrderBiz.setQimenService(qimenService);
        AppResult ret = new AppResult();
        if (isSucc) {
            ret.setAppcode("200");
            ret.setDatabuffer("操作成功");
        } else {
            ret.setAppcode("0");
            ret.setDatabuffer("mock测试，操作失败");
        }
        String body = "{\"flag\":\"success\",\"code\":\"200\",\"success\":true,\"entryOrderId\":\"WMS-CGRKTZ2017120500166\",\"message\":\"操作成功\",\"body\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?><response>   <flag>success</flag>    <code>200</code>    <message>订单取消成功</message> </response>\"}";
        ret.setResult(JSON.parseObject(body));
        when(qimenService.orderCancel(any(ScmOrderCancelRequest.class))).thenReturn(ret);
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

    private OutboundOrder createOutboundOrder () {
        return outBoundOrderService.selectByPrimaryKey(54L);
    }

    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("order_item");
        exportData(tableNameList, "src/test/resources/outbound/preOutboundOrder.xml");
    }
}
