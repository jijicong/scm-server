package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseInfo.ILogisticsCorporationBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.LogisticsCorporation;
import org.trc.enums.ValidEnum;
import org.trc.form.warehouseInfo.LogisticsCorporationForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzcyn on 2018/5/3.
 */
@Component
@Path(SupplyConstants.LogisticsCorporation.ROOT)
public class LogisticsCorporationResource {

    @Autowired
    ILogisticsCorporationBiz logisticsCorporationBiz;

    private Logger logger = LoggerFactory.getLogger(LogisticsCorporationResource.class);

    @GET
    @Path(SupplyConstants.LogisticsCorporation.LOGISTICS_CORPORATION_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<LogisticsCorporation> queryList(@BeanParam LogisticsCorporationForm query, @BeanParam Pagenation<LogisticsCorporation> page) {
        logger.info("开始分页查询物流公司信息，请求参数分别为：query="+ JSON.toJSONString(query)+",page="+JSON.toJSONString(page));
        return logisticsCorporationBiz.selectLogisticsCorporationByPage(query, page);
    }

    @POST
    @Path(SupplyConstants.LogisticsCorporation.LOGISTICS_CORPORATION)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveLogisticsCorporation(@BeanParam LogisticsCorporation logisticsCorporation, @Context ContainerRequestContext requestContext) {
        logger.info("开始保存物流公司信息到数据库===》"+"上传参数分别为：logisticsCorporation="+JSON.toJSONString(logisticsCorporation));
        return logisticsCorporationBiz.saveLogisticsCorporation(logisticsCorporation, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }

    @PUT
    @Path(SupplyConstants.LogisticsCorporation.LOGISTICS_CORPORATION+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLogisticsCorporation(@BeanParam LogisticsCorporation logisticsCorporation, @Context ContainerRequestContext requestContext){
        logisticsCorporationBiz.updateLogisticsCorporation(logisticsCorporation, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("修改物流公司信息成功","");
    }

    @PUT
    @Path(SupplyConstants.LogisticsCorporation.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLogisticsCorporationState(@BeanParam LogisticsCorporation logisticsCorporation, @Context ContainerRequestContext requestContext){
        logisticsCorporationBiz.updateLogisticsCorporationState(logisticsCorporation, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult(ValidEnum.VALID.getCode().equals(logisticsCorporation.getIsValid()) ? "停用成功!":"启用成功!","");
    }
}
