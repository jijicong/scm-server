package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.IBrandBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.enums.BrandSourceEnum;
import org.trc.enums.ValidEnum;
import org.trc.form.category.BrandForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 * Created by hzqph on 2017/5/2.
 */
@Component
@Path(SupplyConstants.Category.ROOT)

public class BrandResource {

    @Autowired
    private IBrandBiz brandBiz;

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Brand> brandPage(@BeanParam BrandForm form,@BeanParam Pagenation<Brand> page) throws Exception {
        return brandBiz.brandPage(form,page);
    }

    @GET
    @Path(SupplyConstants.Config.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }

    /**
     * TODO 后期用户模块加入之后，需要对最后更新人和创建人做处理
     * @param brand
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.Category.Brand.BRAND)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveBrand(@BeanParam Brand brand) throws Exception{
        brandBiz.saveBrand(brand);
        return ResultUtil.createSucssAppResult("保存品牌成功", "");
    }

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Brand> findBrandById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询品牌成功", brandBiz.findBrandById(id));
    }

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND_LIST_SEARCH +"/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findBrandByName(@PathParam("name") String name) throws Exception{
        List<Brand> list=brandBiz.findBrandsByName(name);
        Integer flag=0;
        if(null==list||list.size()<1){
            flag=null;
        }else{
            flag=1;
        }
        return ResultUtil.createSucssAppResult("查询品牌成功", flag);
    }

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult queryBrands(@BeanParam BrandForm form) throws Exception{
        return ResultUtil.createSucssAppResult("查询品牌列表成功", brandBiz.queryBrands(form));
    }


    @PUT
    @Path(SupplyConstants.Category.Brand.BRAND +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateBrand(@BeanParam Brand brand) throws Exception{
        brandBiz.updateBrand(brand);
        return ResultUtil.createSucssAppResult("更新品牌成功", "");
    }

    @POST
    @Path(SupplyConstants.Category.Brand.BRAND_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateBrandStatus(@BeanParam Brand brand)throws Exception{
        brandBiz.updateBrandStatus(brand);
        return ResultUtil.createSucssAppResult("更新品牌状态成功", "");
    }
}
