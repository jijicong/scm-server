package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Category;
import org.trc.form.category.CategoryForm;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;

/**
 * Created by hzszy on 2017/5/10.
 */
@Component
@Path(SupplyConstants.Category.ROOT)
public class CategoryResource {
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 查询树
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CLASSIFY_TREE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> classifyTree() throws Exception {

        return ResultUtil.createSucssAppResult("成功", categoryBiz.getTreeNode());

    }

    /**
     * 新增分类
     *
     * @param category
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.Category.Classify.CLASSIFY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveClassify(@BeanParam Category category) throws Exception {

        category.setCreateOperator("test");
        category.setCreateTime(Calendar.getInstance().getTime());
        category.setUpdateTime(Calendar.getInstance().getTime());
        categoryBiz.saveClassify(category);
        if (category.getLevel() == 1) {
            category.setFullPathId(category.getId().toString());
        } else if (category.getLevel() == 2) {
            category.setFullPathId(category.getParentId() + "|" + category.getId());
        } else {
            category.setFullPathId(category.getFullPathId() + "|" + category.getId());
        }
        categoryBiz.updateCategory(category);
        return ResultUtil.createSucssAppResult("增加分类成功", "");
    }

    /**
     * 修改分类
     * @param category
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.Category.Classify.CLASSIFY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateClassify(@BeanParam Category category) throws Exception {
        categoryBiz.updateCategory(category);
        return ResultUtil.createSucssAppResult("修改分类成功", "");
    }

    /**
     * 查询分类是否存在  （还在修改）
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CLASSIFY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findCategoryByCategoryCode(@QueryParam("categoryCode") String categoryCode) throws Exception {

        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        String string = categoryBiz.findCategoryByCategoryCode(categoryCode) == null ? null : "1";
        return ResultUtil.createSucssAppResult("查询分类项为空", string);

    }
}
