package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.ConfigBiz;
import org.trc.domain.score.Dict;
import org.trc.domain.score.DictType;
import org.trc.enums.ValidEnum;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
import org.trc.util.AppResult;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwdx on 2017/4/24.
 */
@Component
@Path("config")
public class ConfigResource {

    @Autowired
    private ConfigBiz configBiz;

    @GET
    @Path("dictTypePage")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<DictType> dictTypePage(@BeanParam DictTypeForm form, @BeanParam Pagenation<DictType> page,@QueryParam("callback") String callback) throws Exception{
        //return callback+"("+ JSON.toJSONString(configBiz.dictTypePage(form, page))+")";
        //return CommonUtil.getJsonpResult(configBiz.dictTypePage(form, page), callback, "字典类型分页查询成功");
        return configBiz.dictTypePage(form, page);
    }

    @GET
    @Path("dictTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryDictTypes(@BeanParam DictTypeForm dictTypeForm, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.queryDictTypes(dictTypeForm), callback, "查询字典类型列表成功");
        return ResultUtil.createSucssAppResult("查询字典类型列表成功", configBiz.queryDictTypes(dictTypeForm));
    }

    @POST
    @Path("dictType")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveDictType(@BeanParam DictType dictType, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.saveDictType(dictType), callback, "保存字典类型成功");
        return ResultUtil.createSucssAppResult("保存字典类型成功", configBiz.saveDictType(dictType));
    }

    @PUT
    @Path("dictType/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateDictType(@BeanParam  DictTypeForm dictTypeForm, @PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.updateDictType(dictTypeForm,id), callback, "修改字典类型成功");
        return ResultUtil.createSucssAppResult("修改字典类型成功", configBiz.updateDictType(dictTypeForm,id));
    }

    @GET
    @Path("dictType/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictTypeById(@PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.findDictTypeById(id), callback, "查询字典类型成功");
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeById(id));
    }

    @GET
    @Path("dictType")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictTypeByTypeNo(@QueryParam("typeNo") String typeNo, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.findDictTypeByTypeNo(typeNo), callback, "查询字典类型成功");
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeByTypeNo(typeNo));
    }

    @DELETE
    @Path("dictType/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictTypeById(@PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.deleteDictTypeById(id), callback, "删除字典类型成功");
        return ResultUtil.createSucssAppResult("删除字典类型成功", configBiz.deleteDictTypeById(id));
    }

    @GET
    @Path("/dictPage")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Dict> dictPage(@BeanParam DictForm form, @BeanParam Pagenation<Dict> page, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.dictPage(form, page), callback, "字典分页查询成功");
        //return callback+"("+ JSON.toJSONString(configBiz.dictPage(form, page))+")";
        return configBiz.dictPage(form, page);
    }

    @GET
    @Path("/dicts")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryDicts(@BeanParam DictForm dictForm, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.queryDicts(dictForm), callback, "查询字典列表成功");
        return ResultUtil.createSucssAppResult("查询字典列表成功", configBiz.queryDicts(dictForm));
    }

    @POST
    @Path("/dict")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveDict(@BeanParam Dict dict, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.saveDict(dict), callback, "保存字典成功");
        return ResultUtil.createSucssAppResult("保存字典成功", configBiz.saveDict(dict));
    }

    @PUT
    @Path("dict/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateDict(@BeanParam  DictForm dictForm, @PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.updateDict(dictForm,id), callback, "修改字典成功");
        return ResultUtil.createSucssAppResult("修改字典成功", configBiz.updateDict(dictForm,id));
    }

    @GET
    @Path("dict/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findDictById(@PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.findDictById(id), callback, "查询字典成功");
        return ResultUtil.createSucssAppResult("查询字典成功", configBiz.findDictById(id));
    }

    @DELETE
    @Path("dict/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictById(@PathParam("id") Long id, @QueryParam("callback") String callback) throws Exception{
        //return CommonUtil.getJsonpResult(configBiz.deleteDictById(id), callback, "删除字典成功");
        return ResultUtil.createSucssAppResult("删除字典成功", configBiz.deleteDictById(id));
    }

    @GET
    @Path("validList")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryValidList(HttpServletRequest request){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }



}
