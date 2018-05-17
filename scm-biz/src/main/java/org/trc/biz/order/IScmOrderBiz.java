package org.trc.biz.order;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.ExceptionOrder;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.SupplierOrderInfo;
import org.trc.domain.order.WarehouseOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.LogisticNoticeForm2;
import org.trc.form.order.*;
import org.trc.form.warehouse.ScmDeliveryOrderCreateRequest;
import org.trc.form.warehouse.ScmDeliveryOrderCreateResponse;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.service.IJDService;
import org.trc.service.ITrcService;
import org.trc.service.util.IRealIpService;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/6/26.
 */
public interface IScmOrderBiz {

    /**
     * 订单分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<ShopOrder> shopOrderPage(ShopOrderForm form, Pagenation<ShopOrder> page, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 仓库订单分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<WarehouseOrder> warehouseOrderPage(WarehouseOrderForm form, Pagenation<WarehouseOrder> page, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     *查询商铺订单列表
     * @param form
     * @return
     */
    List<ShopOrder> queryShopOrders(ShopOrderForm form);

    /**
     *仓库订单编码
     * @param warehouseOrderCode
     * @return
     */
    WarehouseOrder queryWarehouseOrdersDetail(String warehouseOrderCode);

    /**
     * 平台订单列表查询
     * @param form
     * @return
     */
    List<PlatformOrder> queryPlatformOrders(PlatformOrderForm form);

    /**
     * 提交京东订单
     * @param warehouseOrderCode
     * @param jdAddressCode
     * @param jdAddressName
     * @return
     */
    ResponseAck submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     *提交粮油订单
     * @param warehouseOrderCode
     * @return
     */
    ResponseAck submitLiangYouOrder(String warehouseOrderCode);

    /**
     *提交粮油订单
     * @param warehouseOrders
     * @return
     */
    void submitLiangYouOrders(List<WarehouseOrder> warehouseOrders);

    /**
     * 渠道订单请求流水
     * @param orderInfo
     * @return
     */
    void saveChannelOrderRequestFlow(String orderInfo, ResponseAck responseAck);

    /**
     * 接收渠道订单信息
     * @param orderInfo
     * @return
     */
    ResponseAck<Map<String, Object>> reciveChannelOrder(String orderInfo) throws Exception;

    /**
     * 查询京东物流信息
     * @param channelCode
     * @param shopOrderCode
     * @return
     * @throws Exception
     */
    ResponseAck<LogisticNoticeForm2> getJDLogistics(String channelCode, String shopOrderCode) throws  Exception;

    /**
     * 获取物流信息
     */
    void fetchLogisticsInfo();

    Response exportSupplierOrder(WarehouseOrderForm queryModel,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     *取消操作
     * @param form
     * @param aclUserAccreditInfo
     */
    String cancelHandler(SupplierOrderCancelForm form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 供应商取消订单
     * @param orderInfo
     * @return
     */
    ResponseAck<String> supplierCancelOrder(String orderInfo);

    public void setIjdService(IJDService ijdService);

    /**
     * 异常订单信息分页查询
     * @param form
     * @param page
     * @param aclUserAccreditInfo
     * @return
     * @throws Exception
     */
    Pagenation<ExceptionOrder> exceptionOrderPage(ExceptionOrderForm form, Pagenation<ExceptionOrder> page, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     *根据异常订单编码查询拆单异常订单详情
     * @param exceptionOrderCode
     * @return
     */
    ExceptionOrder queryExceptionOrdersDetail(String exceptionOrderCode);

    public void setiRealIpService(IRealIpService iRealIpService);

    public void setTrcService(ITrcService trcService);

    /**
     * 处理订单
     * @param warehouseOrders
     * @return
     */
    ResponseAck handlerOrder(List<WarehouseOrder> warehouseOrders) throws Exception;


    /**
     *
     * @param warehouseOrderCode
     * @return
     */
    ResponseAck outboundConfirmNotice(String warehouseOrderCode);


    /**
     * 京东订单拆分子订单通知
     * @param orderInfo
     * @return
     */
    ResponseAck jdOrderSplitNotice(String orderInfo);

    /**
     * 订单下单结果通知
     * @param orderInfo
     * @return
     */
    ResponseAck orderSubmitResultNotice(String orderInfo);

    /**
     * 提交自采订单
     * @param warehouseOrders
     * @param skuWarehouseMap
     * @return
     */
    ResponseAck submitSelfPurchaseOrder(List<WarehouseOrder> warehouseOrders, Map<String, List<SkuWarehouseDO>> skuWarehouseMap);

    //WarehouseOrder updateWarehouseOrderSupplierOrderStatus(String string);
    
    void handlerOrderLogisticsInfo(SupplierOrderInfo supplierOrderInfo);

    /**
     * 创建发货单
     * @param outboundMap, 里面的key是采购单编码,OutboundForm是采购单对象和采购单明细列表
     * @return
     */
    AppResult<List<ScmDeliveryOrderCreateResponse>> deliveryOrderCreate(Map<String, OutboundForm> outboundMap);

    /**
     * 发货通知单下单结果通知渠道
     * @param shopOrderCode 店铺订单号
     */
    void outboundOrderSubmitResultNoticeChannel(String shopOrderCode);

    /**
     * 订单导入
     * @param uploadedInputStream
     * @param fileDetail
     */
    Response importOrder(InputStream uploadedInputStream, FormDataContentDisposition fileDetail);

    /**
     * 下载错误订单
     * @param orderCode
     * @return
     */
    Response downloadErrorOrder(String orderCode);


}
