package org.trc.biz.order;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.form.LogisticNoticeForm2;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.SupplierOrderCancelForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;

import javax.ws.rs.core.Response;
import java.util.List;

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
    ResponseAck<String> reciveChannelOrder(String orderInfo);

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


}
