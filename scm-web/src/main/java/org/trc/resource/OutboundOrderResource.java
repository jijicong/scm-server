package org.trc.resource;

import org.apache.logging.log4j.core.config.Scheduled;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.BaseDO;
import org.trc.domain.System.Warehouse;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by sone on 2017/8/10.
 */
@Component
@Path(SupplyConstants.OutboundOrder.ROOT)
public class OutboundOrderResource {

    @Autowired
    private IWarehouseBiz warehouseBiz;

    //出库通知单分页查询
    @GET
    @Path(SupplyConstants.OutboundOrder.OUTBOUND_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult warehousePage(){
        return null;
    }

    @GET
    @Path(SupplyConstants.OutboundOrder.WAREHOUSE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Warehouse> findValidWarehouseList (){
        return ResultUtil.createSucssAppResult("查询有效的仓库成功!",warehouseBiz.findWarehouseValid());
    }

}
