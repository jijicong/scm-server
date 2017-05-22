package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.TreeNode;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;


import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {

    private final static Logger log = LoggerFactory.getLogger(CategoryBiz.class);

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ICategoryBrandService categoryBrandService;

    /**
     * 根据父类id，查找子分类，isRecursive为true，递归查找所有，否则只查一级子分类
     * null, false
     * null, true
     * xx
     *
     * @param parentId
     * @param isRecursive
     * @return
     */
    @Override
    public List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if (null == parentId) {
            criteria.andIsNull("parentId");
        } else {
            criteria.andEqualTo("parentId", parentId);
        }
        example.orderBy("sort").asc();
        List<Category> childCategoryList = categoryService.selectByExample(example);
        List<TreeNode> childNodeList = new ArrayList<>();
        for (Category category : childCategoryList) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(category.getId().toString());
            treeNode.setName(category.getName());
            treeNode.setSort(category.getSort());
            treeNode.setIsValid(category.getIsValid());
            treeNode.setLevel(category.getLevel());
            treeNode.setFullPathId(category.getFullPathId());
            treeNode.setSource(category.getSource());
            treeNode.setCategoryCode(category.getCategoryCode());
            treeNode.setIsLeaf(category.getIsLeaf());
            childNodeList.add(treeNode);
        }
        if (childNodeList.size() == 0) {
            return childNodeList;
        }
        if (isRecursive == true) {
            for (TreeNode childNode : childNodeList) {
                List<TreeNode> nextChildCategoryList = getNodes(Long.parseLong(childNode.getId()), isRecursive);
                if (nextChildCategoryList.size() > 0) {
                    childNode.setChildren(nextChildCategoryList);
                }
            }
        }
        return childNodeList;
    }

    /**
     * 修改分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void updateCategory(Category category) throws Exception {

        AssertUtil.notNull(category.getId(), "修改分类参数ID为空");
        category.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(category);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category), "数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 添加分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void saveClassify(Category category) throws Exception {

        try {
            ParamsUtil.setBaseDO(category);
            categoryService.insert(category);
        } catch (Exception e) {
            String msg = CommonUtil.joinStr("保存分类", JSON.toJSONString(category), "到数据库失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }

    }


    /**
     * 查询分类编码是否存在
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @Override
    public int checkCategoryCode(Long id, String categoryCode) throws Exception {

//        AssertUtil.notBlank(categoryCode, "根据分类编码查询分类的参数categoryCode为空");
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", id);
        criteria.andEqualTo("categoryCode", categoryCode);
        return categoryService.selectByExample(example).size();

    }

    /***
     * 判断是否叶子节点
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int isLeaf(Long id) throws Exception {

        AssertUtil.notNull(id, "根据分类ID查询分类的参数id为空");
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", id);
        return categoryService.selectByExample(example).size();//需要用select（*） count
    }

    /**
     * 更新是否叶子节点
     */
    @Override
    public void updateIsLeaf(Category category) throws Exception {
        AssertUtil.notNull(category.getParentId(), "根据分类ID查询分类父节点的参数id为空");
        Category categoryParent = new Category();
        categoryParent.setId(category.getParentId());
        categoryParent.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        categoryService.updateByPrimaryKeySelective(categoryParent);
    }

    @Override
    public List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws Exception {
        /*if(StringUtils.isBlank(categoryBrandForm.getBrandId()) && StringUtils.isBlank(categoryBrandForm.getCategoryId())){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION,"查询分类相关品牌分类ID和品牌ID不能同时为空");
        }
        Example example = new Example(CategoryBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(categoryBrandForm.getCategoryId())){
            String[] categoryIds = categoryBrandForm.getCategoryId().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("categoryId", Arrays.asList(categoryIds));
        }
        if(StringUtils.isNotBlank(categoryBrandForm.getBrandId())){
            String[] brandIds = categoryBrandForm.getBrandId().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("brandId", Arrays.asList(brandIds));
        }
        return categoryBrandService.selectByExample(example);*/
        AssertUtil.notBlank(categoryBrandForm.getCategoryId(), "查询分类相关品牌分类ID不能为空");
        String[] categoryIds = categoryBrandForm.getCategoryId().split(SupplyConstants.Symbol.COMMA);
        List<Long> categoryList = new ArrayList<Long>();
        for (String categoryId : categoryIds) {
            categoryList.add(Long.parseLong(categoryId));
        }
        return categoryBrandService.queryCategoryBrands(categoryList);
    }

    @Override
    public Long queryPathId(Long id) throws Exception {
        Category category = new Category();
        category.setId(id);
        return categoryService.selectOne(category).getParentId();
    }

    /**
     * 更新排序
     *
     * @param categoryList
     * @throws Exception
     */
    @Override
    public void updateSort(List<Category> categoryList) throws Exception {
        categoryService.updateCategorySort(categoryList);
    }

    /**
     * 分类状态修改
     *
     * @param category
     * @throws Exception
     */
    @Override
    public void updateState(Category category) throws Exception {
        AssertUtil.notNull(category.getId(), "类目管理模块修改分类信息失败，分类信息为空");
        Category updateCategory = new Category();
        updateCategory.setId(category.getId());
        if (category.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateCategory.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateCategory.setIsValid(ValidEnum.VALID.getCode());
        }
        updateCategory.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category), "数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 根据ID查询三级分类的全路径名称，用于品牌，属性的接入
     */
    @Override
    public  List<String> queryCategoryNamePath(Long id) throws Exception{
        List<String> categoryName = new ArrayList<>();
        AssertUtil.notNull(id,"根据ID查询分类参数为空");

        Category categoryLevel3 = new Category();
        categoryLevel3.setId(id);
        categoryLevel3 =   categoryService.selectOne(categoryLevel3);
        categoryName.add(categoryLevel3.getName());

        Category categoryLevel2 = new Category();
        categoryLevel2.setId(categoryLevel3.getParentId());
        categoryLevel2 =   categoryService.selectOne(categoryLevel2);
        categoryName.add(categoryLevel2.getName());


        Category categoryLevel1 = new Category();
        categoryLevel1.setId(categoryLevel2.getParentId());
        categoryLevel1 =   categoryService.selectOne(categoryLevel1);
        categoryName.add(categoryLevel1.getName());
        return categoryName;
    }
}
