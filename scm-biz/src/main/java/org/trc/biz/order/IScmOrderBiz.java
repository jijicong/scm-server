package org.trc.biz.order;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.form.LogisticForm;
import org.trc.form.LogisticNoticeForm;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import javax.ws.rs.container.ContainerRequestContext;
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
    Pagenation<ShopOrder> shopOrderPage(ShopOrderForm form, Pagenation<ShopOrder> page);

    /**
     * 仓库订单分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<WarehouseOrder> warehouseOrderPage(WarehouseOrderForm form, Pagenation<WarehouseOrder> page);

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
    AppResult submitJingDongOrder(String warehouseOrderCode, String jdAddressCode, String jdAddressName, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     *提交粮油订单
     * @param warehouseOrderCode
     * @return
     */
    AppResult submitLiangYouOrder(String warehouseOrderCode);

    /**
     * 渠道订单请求流水
     * @param orderInfo
     * @return
     */
    void saveChannelOrderRequestFlow(String orderInfo, AppResult appResult);

    /**
     * 接收渠道订单信息
     * @param orderInfo
     * @return
     */
    AppResult<String> reciveChannelOrder(String orderInfo);

    /**
     * 查询京东物流信息
     * @param shopOrderCode
     * @return
     */
    AppResult<LogisticNoticeForm> getJDLogistics(String shopOrderCode) throws  Exception;

    /**
     * 获取物流信息
     */
    void fetchLogisticsInfo();


}
