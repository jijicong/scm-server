package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.order.ShopOrder;
import org.trc.form.order.ShopOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Component
@Path(SupplyConstants.Order.ROOT)
public class OrderResource {

    @Autowired
    private IScmOrderBiz scmOrderBiz;

    @GET
    @Path(SupplyConstants.Order.SHOP_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<ShopOrder> shopOrderPage(@BeanParam ShopOrderForm form, @BeanParam Pagenation<ShopOrder> page){
        return scmOrderBiz.shopOrderPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Order.SHOP_ORDER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<ShopOrder>> queryShopOrders(@BeanParam ShopOrderForm form){
        return ResultUtil.createSucssAppResult("根据条件查询店铺订单成功", scmOrderBiz.queryShopOrders(form));
    }

}



