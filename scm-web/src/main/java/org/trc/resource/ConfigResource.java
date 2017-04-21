package org.trc.resource;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.ConfigBiz;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
import org.trc.util.AppResult;
import org.trc.util.CommonUtil;
import org.trc.util.ResultUtil;
import org.trc.util.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Component
@Path("/config")
public class ConfigResource {

    @Autowired
    private ConfigBiz configBiz;

    @GET
    @Path("/dictTypePage")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult dictTypePage(@BeanParam DictTypeForm form) throws Exception{
        return ResultUtil.createSucssAppResult("字典类型分页查询成功", configBiz.dictTypePage(form));
    }

    @GET
    @Path("/queryDictTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryDictTypes() throws Exception{
        return ResultUtil.createSucssAppResult("查询字典类型列表成功", configBiz.queryDictTypes());
    }

    @POST
    @Path("/saveDictType")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveDictType(@FormParam("id") String id, @FormParam("code") String code, @FormParam("name") String name,
                                  @FormParam("description") String description, @FormParam("isValid") String isValid) throws Exception {
        JSONObject param = new JSONObject();
        if(StringUtils.isNotEmpty(id))
            param.put("id", id);
        if(StringUtils.isNotEmpty(code))
            param.put("code", code);
        if(StringUtils.isNotEmpty(name))
            param.put("name", name);
        if(StringUtils.isNotEmpty(description))
            param.put("description", description);
        if(StringUtils.isNotEmpty(isValid))
            param.put("isValid", isValid);
        return ResultUtil.createSucssAppResult("保存字典类型成功", configBiz.saveDictType(param));
    }


    @GET
    @Path("/findDictTypeById")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictTypeById(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        ValidateUtil.jsonParamNullCheck(param, "id:主键ID");
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeById(param.getLong("id")));
    }

    @GET
    @Path("/findDictTypeByTypeNo")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictTypeByTypeNo(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        ValidateUtil.jsonParamNullCheck(param, "typeNo:类型编码");
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeByTypeNo(param.getString("typeNo")));
    }

    @GET
    @Path("/deleteDictTypeById")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictTypeById(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        ValidateUtil.jsonParamNullCheck(param, "id:主键ID");
        return ResultUtil.createSucssAppResult("删除字典类型成功", configBiz.deleteDictTypeById(param.getLong("id")));
    }

    @GET
    @Path("/dictPage")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult dictPage(@BeanParam DictForm form) throws Exception{
        return ResultUtil.createSucssAppResult("字典分页查询成功", configBiz.dictPage(form));
    }

    @GET
    @Path("/queryDicts")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryDicts(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        return ResultUtil.createSucssAppResult("查询字典列表成功", configBiz.queryDicts(param));
    }

    @POST
    @Path("/saveDict")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveDict(@FormParam("id") String id, @FormParam("typeNo") String typeNo, @FormParam("name") String name,
                              @FormParam("value") String value, @FormParam("isValid") String isValid) throws Exception{
        JSONObject param = new JSONObject();
        if(StringUtils.isNotEmpty(id))
            param.put("id", id);
        if(StringUtils.isNotEmpty(value))
            param.put("value", value);
        if(StringUtils.isNotEmpty(name))
            param.put("name", name);
        if(StringUtils.isNotEmpty(typeNo))
            param.put("typeNo", typeNo);
        if(StringUtils.isNotEmpty(isValid))
            param.put("isValid", isValid);
        return ResultUtil.createSucssAppResult("保存字典成功", configBiz.saveDict(param));
    }

    @GET
    @Path("/findDictById")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictById(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        ValidateUtil.jsonParamNullCheck(param, "id:主键ID");
        return ResultUtil.createSucssAppResult("查询字典成功", configBiz.findDictById(param.getLong("id")));
    }

    @GET
    @Path("/deleteDictById")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictById(@Context HttpServletRequest request) throws Exception{
        JSONObject param = CommonUtil.getJsonParams(request);
        ValidateUtil.jsonParamNullCheck(param, "id:主键ID");
        return ResultUtil.createSucssAppResult("删除字典成功", configBiz.deleteDictById(param.getLong("id")));
    }

}
