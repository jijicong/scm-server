package org.trc.biz.order;

import org.trc.domain.order.PlatformOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.form.order.PlatformOrderForm;
import org.trc.form.order.ShopOrderForm;
import org.trc.form.order.WarehouseOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import javax.ws.rs.FormParam;
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
     * @param jdAddress
     * @return
     */
    AppResult submitJingDongOrder(String warehouseOrderCode, String jdAddress);

}
