package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.config.IConfigBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.form.config.DictForm;
import org.trc.form.config.DictTypeForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzwdx on 2017/4/24.
 */
@Component
@Path(SupplyConstants.Config.ROOT)
public class ConfigResource {

    @Autowired
    private IConfigBiz configBiz;

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<DictType> dictTypePage(@BeanParam DictTypeForm form, @BeanParam Pagenation<DictType> page) throws Exception {
        return configBiz.dictTypePage(form, page);
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDictTypes(@BeanParam DictTypeForm dictTypeForm) throws Exception {
        return ResultUtil.createSuccessResult("查询字典类型成功", configBiz.queryDictTypes(dictTypeForm));
    }

    @POST
    @Path(SupplyConstants.Config.DictType.DICT_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveDictType(@BeanParam DictType dictType, @Context ContainerRequestContext requestContext) throws Exception {
        configBiz.saveDictType(dictType);
        return ResultUtil.createSuccessResult("保存字典类型成功", "");
    }

    @PUT
    @Path(SupplyConstants.Config.DictType.DICT_TYPE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDictType(@BeanParam DictType dictType) throws Exception {
        configBiz.updateDictType(dictType);
        return ResultUtil.createSuccessResult("修改字典类型成功", "");
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDictTypeById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSuccessResult("查询字典类型成功", configBiz.findDictTypeById(id));
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDictTypeByTypeNo(@QueryParam("typeNo") String typeNo) throws Exception {
        return ResultUtil.createSuccessResult("查询字典类型成功", configBiz.findDictTypeByTypeNo(typeNo));
    }

    @DELETE
    @Path(SupplyConstants.Config.DictType.DICT_TYPE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDictTypeById(@PathParam("id") Long id) throws Exception {
        configBiz.deleteDictTypeById(id);
        return ResultUtil.createSuccessResult("删除字典类型成功", "");
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Dict> dictPage(@BeanParam DictForm form, @BeanParam Pagenation<Dict> page) throws Exception {
        return configBiz.dictPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDicts(@BeanParam DictForm dictForm) throws Exception {
        return ResultUtil.createSuccessResult("查询字典列表成功", configBiz.queryDicts(dictForm));
    }

    @POST
    @Path(SupplyConstants.Config.Dict.DICT)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveDict(@BeanParam Dict dict, @Context ContainerRequestContext requestContext) throws Exception {
        configBiz.saveDict(dict);
        return ResultUtil.createSuccessResult("保存字典成功", "");
    }

    @PUT
    @Path(SupplyConstants.Config.Dict.DICT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDict(@BeanParam Dict dict) throws Exception {
        configBiz.updateDict(dict);
        return ResultUtil.createSuccessResult("修改字典成功", "");
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDictById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSuccessResult("查询字典成功", configBiz.findDictById(id));
    }

    /*@GET
    @Path(SupplyConstants.Config.Dict.DICT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> findDictByTypeNo(@QueryParam("typeNo") String typeNo) throws Exception {
        return ResultUtil.createSuccessResult("查询字典成功", configBiz.findDictsByTypeNo(typeNo));
    }*/

    @DELETE
    @Path(SupplyConstants.Config.Dict.DICT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDictById(@PathParam("id") Long id) throws Exception {
        configBiz.deleteDictById(id);
        return ResultUtil.createSuccessResult("删除字典成功", "");
    }

}
