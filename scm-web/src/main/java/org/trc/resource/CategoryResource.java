package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.CategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.dict.Dict;
import org.trc.enums.BrandSourceEnum;
import org.trc.enums.ValidEnum;
import org.trc.form.BrandForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * Created by hzqph on 2017/5/2.
 */
@Component
@Path("category")
public class CategoryResource {
    @Autowired
    private CategoryBiz categoryBiz;

    @GET
    @Path(SupplyConstants.Category.Brand.Brand_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Brand> brandPage(@BeanParam BrandForm form,@BeanParam Pagenation<Brand> page) throws Exception {
        return categoryBiz.brandPage(form,page);
    }

    @GET
    @Path(SupplyConstants.Config.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(HttpServletRequest request){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }

    /**
     * TODO 后期用户模块加入之后，需要对最后更新人和创建人做处理
     * @param brand
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.Category.Brand.Brand)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveBrand(@BeanParam Brand brand) throws Exception{
        brand.setSource(BrandSourceEnum.SCM.getCode());
        brand.setBrandCode(UUID.randomUUID().toString().replaceAll("-", ""));
        brand.setLastEditOperator("小明");
        brand.setCreateOperator("小明");
        return ResultUtil.createSucssAppResult("保存品牌成功",categoryBiz.saveBrand(brand));
    }

    @GET
    @Path(SupplyConstants.Category.Brand.Brand+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Brand> findBrandById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询品牌成功", categoryBiz.findBrandById(id));
    }

    @PUT
    @Path(SupplyConstants.Category.Brand.Brand+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateBrand(@BeanParam Brand brand,@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("更新品牌成功", categoryBiz.updateBrand(brand,id));
    }

    @POST
    @Path(SupplyConstants.Category.Brand.Brand_Status)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateBrandStatus(@BeanParam Brand brand)throws Exception{
        return ResultUtil.createSucssAppResult("更新品牌状态成功",categoryBiz.updateBrandStatus(brand));
    }
}
