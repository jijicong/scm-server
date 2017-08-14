package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.order.OutboundOrder;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by sone on 2017/8/10.
 */
@Component
@Path(SupplyConstants.OutboundOrder.ROOT)
public class OutboundOrderResource {

    @Autowired
    private IOutBoundOrderBiz outBoundOrderBiz;
    @Autowired
    private IWarehouseBiz warehouseBiz;

    //出库通知单分页查询
    @GET
    @Path(SupplyConstants.OutboundOrder.OUTBOUND_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public  Pagenation<OutboundOrder> outboundOrderPage(@BeanParam OutBoundOrderForm form, @BeanParam Pagenation<OutboundOrder> page) throws Exception {
        return  outBoundOrderBiz.outboundOrderPage(form, page);
    }

    @GET
    @Path(SupplyConstants.OutboundOrder.WAREHOUSE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Warehouse> findValidWarehouseList() {
        return ResultUtil.createSucssAppResult("查询有效的仓库成功!", warehouseBiz.findWarehouseValid());
    }

}
