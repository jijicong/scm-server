package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.ConfigBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.enums.ValidEnum;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by hzwdx on 2017/4/24.
 */
@Component
@Path("config")
public class ConfigResource {

    public static final String ENCODING = "UTF-8";

    @Autowired
    private ConfigBiz configBiz;

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<DictType> dictTypePage(@BeanParam DictTypeForm form, @BeanParam Pagenation<DictType> page) throws Exception{
        return configBiz.dictTypePage(form, page);
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<DictType>> queryDictTypes(@BeanParam DictTypeForm dictTypeForm) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典类型列表成功", configBiz.queryDictTypes(dictTypeForm));
    }

    @POST
    @Path(SupplyConstants.Config.DictType.DICT_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveDictType(@BeanParam DictType dictType) throws Exception{
        return ResultUtil.createSucssAppResult("保存字典类型成功", configBiz.saveDictType(dictType));
    }

    @PUT
    @Path(SupplyConstants.Config.DictType.DICT_TYPE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateDictType(@BeanParam  DictType dictType, @PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("修改字典类型成功", configBiz.updateDictType(dictType,id));
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<DictType> findDictTypeById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeById(id));
    }

    @GET
    @Path(SupplyConstants.Config.DictType.DICT_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<DictType> findDictTypeByTypeNo(@QueryParam("typeNo") String typeNo) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典类型成功", configBiz.findDictTypeByTypeNo(typeNo));
    }

    @DELETE
    @Path(SupplyConstants.Config.DictType.DICT_TYPE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictTypeById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("删除字典类型成功", configBiz.deleteDictTypeById(id));
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Dict> dictPage(@BeanParam DictForm form, @BeanParam Pagenation<Dict> page) throws Exception{
        return configBiz.dictPage(form, page);
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<Dict>> queryDicts(@BeanParam DictForm dictForm) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典列表成功", configBiz.queryDicts(dictForm));
    }

    @POST
    @Path(SupplyConstants.Config.Dict.DICT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveDict(@BeanParam Dict dict) throws Exception{
        return ResultUtil.createSucssAppResult("保存字典成功", configBiz.saveDict(dict));
    }

    @PUT
    @Path(SupplyConstants.Config.Dict.DICT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateDict(@BeanParam Dict dict,@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("修改字典成功", configBiz.updateDict(dict,id));
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> findDictById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典成功", configBiz.findDictById(id));
    }

    @GET
    @Path(SupplyConstants.Config.Dict.DICT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> findDictByTypeNo(@QueryParam("typeNo") String typeNo) throws Exception{
        return ResultUtil.createSucssAppResult("查询字典成功", configBiz.findDictsByTypeNo(typeNo));
    }

    @DELETE
    @Path(SupplyConstants.Config.Dict.DICT+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult deleteDictById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("删除字典成功", configBiz.deleteDictById(id));
    }

    @GET
    @Path(SupplyConstants.Config.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(HttpServletRequest request){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }

}
