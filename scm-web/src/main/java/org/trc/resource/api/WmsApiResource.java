package org.trc.resource.api;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.form.wms.WmsAllocateOutInRequest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 自营仓库
 * Created by hzcyn on 2018/5/22.
 */
@Component
@Path("api/wmsApi")
public class WmsApiResource {

    private Logger logger = LoggerFactory.getLogger(WmsApiResource.class);

    @Autowired
    private IAllocateOutOrderBiz allocateOutOrderBiz;
    @Autowired
    private IAllocateInOrderBiz allocateInOrderBiz;
    @Autowired
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @Autowired
    private IOutBoundOrderBiz outBoundOrderBiz;

    @POST
    @Path("allocateOutOrder")
    @Produces(MediaType.APPLICATION_JSON)
    public Response outFinishCallBack(@FormParam("request") String request) throws Exception {
        WmsAllocateOutInRequest req = JSON.parseObject(request, WmsAllocateOutInRequest.class);
        return allocateOutOrderBiz.outFinishCallBack(req);
    }

    @POST
    @Path("allocateInOrder")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inFinishCallBack(@FormParam("request") String request) throws Exception {
        WmsAllocateOutInRequest req = JSON.parseObject(request, WmsAllocateOutInRequest.class);
        return allocateInOrderBiz.inFinishCallBack(req);
    }

    //(采购管理)入库通知单
    @POST
    @Path("warehouseNotice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseNoticeFinishCallBack(@FormParam("request") String request) throws Exception {
        WmsInNoticeRequest req = JSON.parseObject(request, WmsInNoticeRequest.class);
        return warehouseNoticeBiz.inFinishCallBack(req);

    }

    @POST
    @Path("orderOutResultNotice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderOutResultNotice(@FormParam("request") String request) throws Exception {
        OutboumdWmsDeliverResponseForm req = JSON.parseObject(request, OutboumdWmsDeliverResponseForm.class);
        outBoundOrderBiz.orderOutResultNotice(req);
        return ResultUtil.createSuccessResult("通知成功", "");
    }


}
