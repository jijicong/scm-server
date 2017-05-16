package org.trc.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Category;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        return ResultUtil.createSucssAppResult("成功", categoryBiz.getNodes(null, true));

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

        AssertUtil.notBlank(category.getCategoryCode(), "添加分类时categoryCode参数不能为空");
        AssertUtil.notNull(category.getSort(), "添加分类时sort参数不能为空");
        AssertUtil.notNull(category.getIsValid(), "添加分类时isValid参数不能为空");
        category.setCreateOperator("test");
        category.setSource("scm");
        category.setIsLeaf("1");
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
        if (category.getLevel()!=1&&categoryBiz.isLeaf(category.getParentId())!=0){
            categoryBiz.updateIsLeaf(category);
        }
        return ResultUtil.createSucssAppResult("增加分类成功", "");
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.Category.Classify.CLASSIFY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateClassify(@BeanParam Category category) throws Exception {
        if (categoryBiz.isLeaf(category.getId()) == 0) {
            category.setIsLeaf("1");
        } else {
            category.setIsLeaf("0");
        }
        categoryBiz.updateCategory(category);
        return ResultUtil.createSucssAppResult("修改分类成功", "");
    }

    /**
     * 查询分类是否存在
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

    /**
     * 排序，批量修改
     * @param sortDate
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.Category.Classify.CLASSIFY_SORT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult upDateSort(String sortDate) throws Exception {
        AssertUtil.notBlank(sortDate,"排序信息为空");
       JSONArray sortArray = JSON.parseArray(sortDate);
        List<Category> categoryList = new ArrayList<>();
        for (int i = 0; i < sortArray.size(); i++) {
            JSONObject sortObject = sortArray.getJSONObject(i);
            Category category = new Category();
            category.setId(Long.parseLong(sortObject.getString("id")));
            category.setSort(Integer.parseInt(sortObject.getString("sort")));
            categoryList.add(category);
        }
        categoryBiz.updateSort(categoryList);
        return ResultUtil.createSucssAppResult("更新排序成功", "");
    }

    @PUT
    @Path(SupplyConstants.Category.Classify.UPDATE_STATE +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateCategoryState(@BeanParam Category category) throws Exception{
        categoryBiz.updateState(category);
        return ResultUtil.createSucssAppResult("状态修改成功","");
    }
}
