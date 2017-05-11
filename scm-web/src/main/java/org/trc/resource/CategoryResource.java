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
    @GET
    @Path(SupplyConstants.Category.Classify.CLASSIFY_TREE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> classifyTree(@BeanParam CategoryForm categoryForm) throws Exception {
        return ResultUtil.createSucssAppResult("成功", categoryBiz.getTreeNode(categoryForm));
    }

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
        return ResultUtil.createSucssAppResult("增加分类成功", categoryBiz.updateCategory(category, category.getId()));
    }

    @PUT
    @Path(SupplyConstants.Category.Classify.CLASSIFY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateClassify(@BeanParam Category category, @PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("修改分类成功", categoryBiz.updateCategory(category, id));
    }
}
