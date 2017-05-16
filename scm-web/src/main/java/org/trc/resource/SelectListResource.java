package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.config.IConfigBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.enums.ClearanceEnum;
import org.trc.enums.ValidEnum;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    //仓库类型字典类型编码
    private static final String WAREHOUSE_TYPE="warehouseType";
    //角色类型字典类型编码
    private static final String ROLE_TYPE="roleType";
    //用户类型字典类型编码
    private static final String USER_TYPE="userType";

    @GET
    @Path(SupplyConstants.Config.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(){
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
    //清关
    @GET
    @Path(SupplyConstants.Config.SelectList.IS_CUSTOM_CLEARANCE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryisClearanceList(){
        return ResultUtil.createSucssAppResult("成功", ClearanceEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.WAREHOUSE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> warehouseType() throws Exception{
        return ResultUtil.createSucssAppResult("查询仓库类型成功", configBiz.findDictsByTypeNo(WAREHOUSE_TYPE));
    }
    @GET
    @Path(SupplyConstants.SelectList.PROVINCE_CITY)
    @Produces(MediaType.APPLICATION_JSON)
    public List<AreaTreeNode> findProvinceCity() throws Exception{

        /**
         * 1.查询所有的省市信息
         * 2.使用json对象转化
         * 3.返回给前台
         */
        return configBiz.findProvinceCity();

    }
    @GET
    @Path(SupplyConstants.SelectList.ROLE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> roleType() throws Exception{
        return ResultUtil.createSucssAppResult("查询角色类型成功", configBiz.findDictsByTypeNo(ROLE_TYPE));
    }
    @GET
    @Path(SupplyConstants.SelectList.USER_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> userType() throws Exception{
        return ResultUtil.createSucssAppResult("查询用户类型成功", configBiz.findDictsByTypeNo(USER_TYPE));
    }

}
