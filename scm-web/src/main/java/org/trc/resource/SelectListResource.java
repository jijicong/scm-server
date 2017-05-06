package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.impl.config.ConfigBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.enums.ValidEnum;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 下拉列表资源
 * Created by hzwdx on 2017/5/6.
 */
@Component
@Path(SupplyConstants.SelectList.ROOT)
public class SelectListResource {

    @Autowired
    private IConfigBiz configBiz;

    //供应商性质字典类型编码
    private static final String SUPPLIER_NATURE = "supplierNature";
    //供应商类型字典类型编码
    private static final String SUPPLIER_TYPE = "supplierType";


    @GET
    @Path(SupplyConstants.Config.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(HttpServletRequest request){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.SUPPLIER_NATURE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> supplierNature() throws Exception{
        return ResultUtil.createSucssAppResult("查询供应商性质成功", configBiz.findDictsByTypeNo(SUPPLIER_NATURE));
    }

    @GET
    @Path(SupplyConstants.SelectList.SUPPLIER_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> supplierType() throws Exception{
        return ResultUtil.createSucssAppResult("查询供应商性质成功", configBiz.findDictsByTypeNo(SUPPLIER_TYPE));
    }


}
