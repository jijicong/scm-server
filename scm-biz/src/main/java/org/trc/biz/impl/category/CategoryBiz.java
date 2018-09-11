package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.*;
import org.trc.service.category.*;
import org.trc.service.config.ILogInfoService;
import org.trc.service.supplier.ISupplierCategoryService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import org.trc.util.cache.CategoryCacheEvict;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {

    private Logger log = LoggerFactory.getLogger(CategoryBiz.class);

    private final static String SERIALNAME = "FL";

    private final static Integer LENGTH = 3;

    //分类名称全路径分割符号
    public static final String CATEGORY_NAME_SPLIT_SYMBOL = "-";

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICategoryBrandService categoryBrandService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private IPropertyService propertyService;

    @Autowired
    private ICategoryPropertyService categoryPropertyService;

    @Autowired
    private ISerialUtilService serialUtilService;

    @Autowired
    private ISupplierCategoryService supplierCategoryService;

    @Autowired
    private ITrcBiz trcBiz;

    @Autowired
    private ILogInfoService logInfoService;

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public Pagenation<Category> categoryPage(CategoryForm queryModel, Pagenation<Category> page) throws Exception {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != queryModel.getCategoryId()){
            criteria.andEqualTo("id", queryModel.getCategoryId());
        }
        if (!StringUtils.isBlank(queryModel.getCategoryCode())) {
            criteria.andLike("categoryCode", queryModel.getCategoryCode());
        }
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getSort())) {
            criteria.andEqualTo("sort", queryModel.getSort());
        }
        if (!StringUtils.isBlank(queryModel.getLevel())) {
            criteria.andEqualTo("level", queryModel.getLevel());
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        return categoryService.pagination(example, page, queryModel);
    }


    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public Pagenation<Brand> brandListCategory(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example, criteria, queryModel);
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        return pagenation;
    }

    public void setQueryParam(Example example, Example.Criteria criteria, BrandForm queryModel) {
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        if (!StringUtils.isBlank(queryModel.getStartUpdateTime())) {
            criteria.andGreaterThan("updateTime", queryModel.getStartUpdateTime());
        }
        if (!StringUtils.isBlank(queryModel.getEndUpdateTime())) {
            criteria.andLessThan("updateTime", queryModel.getEndUpdateTime());
        }
        if (!StringUtils.isBlank(queryModel.getAlise())) {
            criteria.andEqualTo("alise", queryModel.getAlise());
        }
        if (!StringUtils.isBlank(queryModel.getBrandCode())) {
            criteria.andEqualTo("brandCode", queryModel.getBrandCode());
        }
        if (!StringUtils.isBlank(queryModel.getPageIds())) {
            Long[] pageIds = StringUtil.splitByComma(queryModel.getPageIds());

            if (pageIds.length > 0) {
//                for (Long id : pageIds) {
//                    criteria.andNotEqualTo("id", id);
//                }
                criteria.andNotIn("id", Arrays.asList(pageIds));
            }
        }
        example.orderBy("updateTime").desc();
    }


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
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception {
        //查询所有分类
            Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
            criteria.andIsNotNull("id");
        example.orderBy("sort").asc();
        example.orderBy("createTime").desc();

        List<Category> childCategoryList = categoryService.selectByExample(example);
        List<TreeNode> childNodeList =delGetNodes(parentId,isRecursive,childCategoryList);
        return childNodeList;
    }

    private List<TreeNode> delGetNodes(Long parentId, boolean isRecursive,List<Category> AllCategoryList) throws Exception {
        List<TreeNode> nodeList = new ArrayList<>();
        if (null == parentId) {
            for (Category category : AllCategoryList) {
                if (category.getParentId()==null){
                    TreeNode treeNode = new TreeNode();
                    treeNode.setId(category.getId().toString());
                    treeNode.setName(category.getName());
                    treeNode.setSort(category.getSort());
                    treeNode.setIsValid(category.getIsValid());
                    treeNode.setLevel(category.getLevel());
                    treeNode.setFullPathId(category.getFullPathId());
                    treeNode.setSource(category.getSource());
                    treeNode.setClassifyDescribe(category.getClassifyDescribe());
                    treeNode.setCategoryCode(category.getCategoryCode());
                    treeNode.setIsLeaf(category.getIsLeaf());
                    nodeList.add(treeNode);
                }
            }
        }else {
            AllCategoryList.stream().
                    filter(category -> StringUtils.equals(String.valueOf(category.getParentId()), String.valueOf(parentId))).
                    forEach(category -> {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setId(category.getId().toString());
                        treeNode.setName(category.getName());
                        treeNode.setSort(category.getSort());
                        treeNode.setIsValid(category.getIsValid());
                        treeNode.setLevel(category.getLevel());
                        treeNode.setFullPathId(category.getFullPathId());
                        treeNode.setSource(category.getSource());
                        treeNode.setClassifyDescribe(category.getClassifyDescribe());
                        treeNode.setCategoryCode(category.getCategoryCode());
                        treeNode.setIsLeaf(category.getIsLeaf());
                        nodeList.add(treeNode);
                    });
        }
        if (AssertUtil.collectionIsEmpty(nodeList)){
            return nodeList;
        }
        if (isRecursive){
            for (TreeNode childNode : nodeList) {
                List<TreeNode> nextChildCategoryList = delGetNodes(Long.parseLong(childNode.getId()), isRecursive,AllCategoryList);
                if (!AssertUtil.collectionIsEmpty(nextChildCategoryList)) {
                    childNode.setChildren(nextChildCategoryList);
                }
            }
        }
        return nodeList;
    }


    /**
     * 修改分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    @CategoryCacheEvict
    public void updateCategory(Category category, boolean isSave, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(category.getId(), "修改分类参数ID为空");
        checkCategoryName(category.getId(),category.getParentId(),category.getName());
        Category oldCategory = new Category();
        oldCategory.setId(category.getId());
        oldCategory = categoryService.selectOne(oldCategory);
        //判断是否叶子节点
        if (isLeaf(category.getId()) == 0) {
            category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        } else {
            category.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        }
        category.setUpdateTime(Calendar.getInstance().getTime());

        String userId = aclUserAccreditInfo.getUserId();

        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
//        category.setCreateOperator(userId);

        int count = categoryService.updateByPrimaryKeySelective(category);
        if (count == 0) {
            String msg = "修改分类" + JSON.toJSONString(category) + "数据库操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        } else {
            if (isSave) {
                noticeCategory(TrcActionTypeEnum.ADD_CATEGORY, category, category, null, null, System.currentTimeMillis());

            } else {
                category = categoryService.selectByPrimaryKey(category.getId());
                String categoryAction = getCategoryAction(category);
                logInfoService.recordLog(category, String.valueOf(category.getId()), userId, String.format(categoryAction + "[%s]", category.getName()), "修改", null);
                noticeCategory(TrcActionTypeEnum.EDIT_CATEGORY, oldCategory, category, null, null, System.currentTimeMillis());

            }
        }
    }

    private String getCategoryAction(Category category) {
        String categoryAction = "";
        if (category.getLevel() != null) {
            switch (category.getLevel()) {
                case 1:
                    categoryAction = CategoryActionEnum.EDIT_ONE_LEVEL.getName();
                    break;
                case 2:
                    categoryAction = CategoryActionEnum.EDIT_TWO_LEVEL.getName();
                    break;
                case 3:
                    categoryAction = CategoryActionEnum.EDIT_THREE_LEVEL.getName();
                    break;
            }
            return categoryAction;
        } else {
            String msg = "获取分类等级失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.SYSTEM_EXCEPTION, msg);
        }

    }

    /**
     * 添加分类
     *
     * @param category
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CategoryCacheEvict
    public void saveCategory(Category category, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        checkSaveCategory(category);
        checkCategoryName(null,category.getParentId(),category.getName());
        category.setCategoryCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        AssertUtil.notNull(category.getCategoryCode(), "分类编码生成失败");
        String categoryAction;

        category.setSource(SourceEnum.SCM.getCode());
        category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        category.setCreateTime(Calendar.getInstance().getTime());
        category.setUpdateTime(Calendar.getInstance().getTime());
        //先保存
        ParamsUtil.setBaseDO(category);
//        String userId = (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
//        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
//        category.setCreateOperator(userId);
        int count = categoryService.insert(category);
        if (count == 0) {
            String msg = "增加分类" + JSON.toJSONString(category) + "数据库操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_SAVE_EXCEPTION, msg);
        } else {
            if (StringUtils.equals(category.getIsValid(), ValidEnum.VALID.getCode()) && category.getParentId() != null) {
                updateLastCategory(category.getParentId());
            }
        }
        //判断新增的分类等级
        if (category.getLevel() == 1) {
            category.setFullPathId(null);
            categoryAction = CategoryActionEnum.ADD_ONE_LEVEL.getName();
        } else if (category.getLevel() == 2) {
            category.setFullPathId(category.getParentId().toString());
            categoryAction = CategoryActionEnum.ADD_TWO_LEVEL.getName();
        } else {
            category.setFullPathId(queryPathId(category.getParentId()) + SupplyConstants.Symbol.FULL_PATH_SPLIT + category.getParentId());
            categoryAction = CategoryActionEnum.ADD_THREE_LEVEL.getName();
        }
        updateCategory(category, true, aclUserAccreditInfo);
        //判断叶子节点,并更新
        if (category.getLevel() != 1 && isLeaf(category.getParentId()) != 0) {
            updateIsLeaf(category);
        }


        logInfoService.recordLog(category, String.valueOf(category.getId()), category.getCreateOperator(), String.format(categoryAction + "[%s]", category.getName()), "新增", null);
    }

    private void checkSaveCategory(Category category) {
        AssertUtil.notNull(category.getSort(), "新增分类参数sort不能为空");
        AssertUtil.notBlank(category.getIsValid(), "新增分类参数isValid不能为空");
        AssertUtil.notNull(category.getLevel(), "新增分类参数level不能为空");
    }

    /**
     * 查询分类编码是否存在
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @Override
    @Deprecated
    public int checkCategoryCode(Long id, String categoryCode) throws Exception {
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
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
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
    @CategoryCacheEvict
    public void updateIsLeaf(Category category) throws Exception {
        AssertUtil.notNull(category.getParentId(), "根据分类ID查询分类父节点的参数id为空");
        Category categoryParent = new Category();
        categoryParent.setId(category.getParentId());
        categoryParent.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        int count = categoryService.updateByPrimaryKeySelective(categoryParent);
        if (count == 0) {
            String msg = "更新分类是否叶子节点" + JSON.toJSONString(category) + "操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws Exception {
        AssertUtil.notBlank(categoryBrandForm.getCategoryId(), "查询分类相关品牌分类ID不能为空");
        String[] categoryIds = categoryBrandForm.getCategoryId().split(SupplyConstants.Symbol.COMMA);
        List<Long> categoryList = new ArrayList<Long>();
        for (String categoryId : categoryIds) {
            categoryList.add(Long.parseLong(categoryId));
        }
        List<CategoryBrandExt> categoryBrandExts = categoryBrandService.queryCategoryBrands(categoryList);
        if (StringUtils.isNotEmpty(categoryBrandForm.getBrandName())&& !CollectionUtils.isEmpty(categoryBrandExts)){
            Iterator<CategoryBrandExt> iterator = categoryBrandExts.iterator();
            while (iterator.hasNext()){
                CategoryBrandExt categoryBrandExt = iterator.next();
                Example example = new Example(Brand.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id",categoryBrandExt.getBrandId());
                criteria.andLike("name","%"+categoryBrandForm.getBrandName()+"%");
                List<Brand> brands = brandService.selectByExample(example);
                if (StringUtils.equals(String.valueOf(brands.size()),ZeroToNineEnum.ZERO.getCode())){
                    iterator.remove();
                }
            }
        }
        if (categoryBrandExts.size()==0){
            return null;
        }
        Collections.sort(categoryBrandExts, new Comparator<CategoryBrandExt>() {
            @Override
            public int compare(CategoryBrandExt o1, CategoryBrandExt o2) {
                return PinyinUtil.compare(o1.getBrandName(), o2.getBrandName());
            }
        });
        return categoryBrandExts;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public Long queryPathId(Long parentId) throws Exception {
        AssertUtil.notNull(parentId, "根据parentId查询父级分类,参数parentId为空");
        Category category = new Category();
        category.setId(parentId);
        return categoryService.selectOne(category).getParentId();
    }

    /**
     * 更新排序
     *
     * @param sortDate
     * @throws Exception
     */
    @Override
    @CategoryCacheEvict
    public void updateSort(String sortDate) throws Exception {
        AssertUtil.notBlank(sortDate, "排序信息为空");
        List<Category> categoryList = JSON.parseArray(sortDate, Category.class);
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = new Category();
            category.setId(categoryList.get(i).getId());
            category = categoryService.selectOne(category);
            AssertUtil.notNull(category, "查找需要更新排序的分类失败");
            if (category.getSort() == categoryList.get(i).getSort()) {
                categoryList.remove(i);
            }
        }
        if (!AssertUtil.collectionIsEmpty(categoryList)) {
            int count = categoryService.updateCategorySort(categoryList);
            if (count == 0) {
                String msg = "修改分类排序操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 分类状态修改
     *
     * @param category
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CategoryCacheEvict
    public void updateState(Category category, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(category.getId(), "类目管理模块修改分类信息失败，分类信息为空");
        Category updateCategory = new Category();
        updateCategory.setId(category.getId());
        Category category1 = categoryService.selectOne(updateCategory);
        Category oldCategory = category1;
        String state = "";
        if (StringUtils.equals(category.getIsValid(), ValidEnum.VALID.getCode())) {
            updateCategory.setIsValid(ValidEnum.NOVALID.getCode());
            state = ValidEnum.NOVALID.getName();
        } else {
            updateCategory.setIsValid(ValidEnum.VALID.getCode());
            state = ValidEnum.VALID.getName();
        }
        if (category1.getParentId() != null && StringUtils.equals(category1.getIsValid(), ValidEnum.NOVALID.getCode())) {
            updateLastCategory(category1.getParentId());
        }
        //校验起停用
        if (StringUtils.equals(updateCategory.getIsValid(), ValidEnum.NOVALID.getCode())) {
            if (checkCategoryIsValid(updateCategory.getId()) == 0) {
                String msg = "修改分类状态" + JSON.toJSONString(updateCategory) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }
        updateCategory.setUpdateTime(Calendar.getInstance().getTime());
        int count = categoryService.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            String msg = "修改分类状态" + JSON.toJSONString(category) + "操作失败";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
        } else {
            updateCategory = categoryService.selectByPrimaryKey(updateCategory.getId());
            noticeCategory(TrcActionTypeEnum.STOP_CATEGORY, oldCategory, updateCategory, null, null, System.currentTimeMillis());

        }
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        //分类状态更新时需要更新分类供应商关系表的is_valid字段
        String categoryAction = getCategoryAction(updateCategory);
        supplierCategoryService.updateSupplierCategoryIsValid(updateCategory.getIsValid(), updateCategory.getId());
        logInfoService.recordLog(updateCategory, String.valueOf(updateCategory.getId()), userId, String.format(categoryAction + "[%s]", updateCategory.getName()), "状态修改为" + state, null);

    }

    /***
     *校验起停用
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public Integer checkCategoryIsValid(Long categoryId) throws Exception {
        //查询到需要改变分类状态的分类
        Category category = new Category();
        category.setId(categoryId);
        category = categoryService.selectOne(category);

        if (category.getLevel() != 3) {
            Example example = new Example(Category.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentId", category.getId());
            List<Category> childCategories = categoryService.selectByExample(example);
            if (childCategories != null && childCategories.size() > 0) {
                for (Category c : childCategories) {
                    if (StringUtils.equals(c.getIsValid(), ValidEnum.VALID.getCode())) {
                        return 0;
                    }
                }
            }

        }
        return 1;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<Category> queryCategorys(CategoryForm categoryForm) throws Exception {
        Category category = new Category();
        BeanUtils.copyProperties(categoryForm, category);
        return categoryService.select(category);
    }


    /**
     * 要启动的分类如果上级分类处于停用状态时,启用分类
     *
     * @param parentId
     * @throws Exception
     */
    @CategoryCacheEvict
    private void updateLastCategory(Long parentId) throws Exception {

        Category category = new Category();
        Category oldCategory;
        category.setId(parentId);
        category = categoryService.selectOne(category);
        oldCategory = category;
        if (StringUtils.equals(category.getIsValid(), ValidEnum.NOVALID.getCode())) {
            category.setIsValid(ValidEnum.VALID.getCode());
            category.setUpdateTime(Calendar.getInstance().getTime());
            int count = categoryService.updateByPrimaryKeySelective(category);
            if (count == 0) {
                String msg = "修改分类状态" + JSON.toJSONString(category) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            } else {
                String categoryAction = getCategoryAction(category);
                logInfoService.recordLog(category, String.valueOf(category.getId()), "admin", String.format(categoryAction + "[%s]", category.getName()), "状态修改为" + ValidEnum.getValidEnumByCode(category.getIsValid()).getName(), null);
                noticeCategory(TrcActionTypeEnum.EDIT_CATEGORY, oldCategory, category, null, null, System.currentTimeMillis());
            }
        }
        if (category.getParentId() != null) {
            updateLastCategory(category.getParentId());
        }
    }

    /**
     * 根据ID查询三级分类的全路径名称，用于品牌，属性的接入
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<String> queryCategoryNamePath(Long id) throws Exception {
        List<String> categoryName = new ArrayList<>();
        AssertUtil.notNull(id, "根据ID查询分类参数为空");

        Category categoryLevel3 = new Category();
        categoryLevel3.setId(id);
        categoryLevel3 = categoryService.selectOne(categoryLevel3);
        categoryName.add(categoryLevel3.getName());

        Category categoryLevel2 = new Category();
        categoryLevel2.setId(categoryLevel3.getParentId());
        categoryLevel2 = categoryService.selectOne(categoryLevel2);
        categoryName.add(categoryLevel2.getName());

        Category categoryLevel1 = new Category();
        categoryLevel1.setId(categoryLevel2.getParentId());
        categoryLevel1 = categoryService.selectOne(categoryLevel1);
        categoryName.add(categoryLevel1.getName());
        return categoryName;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<CategoryBrand> queryBrands(Long categoryId) throws Exception {
        AssertUtil.notNull(categoryId, "根据分类Id查询关联品牌,参数categoryId为空");
        categoryLevel(categoryId);
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(categoryId);
        return categoryBrandService.select(categoryBrand);
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<CategoryProperty> queryProperties(Long categoryId) {
        AssertUtil.notNull(categoryId, "根据分类Id查询关联属性,参数categoryId为空");
        categoryLevel(categoryId);
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(categoryId);
        List<CategoryProperty> queryProperties = new ArrayList<>();
        if (queryProperties.size() > 0) {
            for (CategoryProperty c : queryProperties) {
                c.setIsValid(ValidEnum.getValidEnumByName(c.getIsValid()).getName());
//                c.setValueType();
            }
        }
        return categoryPropertyService.select(categoryProperty);
    }

    private void categoryLevel(Long categoryId) {
        Category linkCategory = new Category();
        linkCategory.setId(categoryId);
        linkCategory = categoryService.selectOne(linkCategory);
        AssertUtil.notNull(linkCategory, "未查询到该分类");
        if (linkCategory.getLevel() != 3) {
            String msg = "操作失败,该分类不属于三级分类";
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_LINK_LEVEL_EXCEPTION, msg);
        }
    }

    /**
     * 数据关联
     *
     * @param categoryId
     * @param brandIds
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CategoryCacheEvict
    public void linkCategoryBrands(Long categoryId, String brandIds, String delRecord, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(categoryId, "分类关联品牌categoryId为空");
        categoryLevel(categoryId);
        //
        List<Long> delBrandIdsList = new ArrayList<>();
        List<CategoryBrand> delCategoryBrands = new ArrayList<>();
        if (StringUtils.isNotBlank(delRecord)) {
            delBrandIdsList.addAll(filterId(delRecord));
            delCategoryBrands = assembleList(delBrandIdsList, categoryId,false);
        }

        //页面上的BrandId
        List<Long> brandIdsList = new ArrayList<>();
        if (StringUtils.isNotBlank(brandIds)) {
            brandIdsList.addAll(filterId(brandIds));
        }
        //组装出页面上CategoryBrand
        List<CategoryBrand> saveCategoryBrands = assembleList(brandIdsList, categoryId,true);

        List<Brand> noValidBrands = new ArrayList<>();
        //页面上的数据
        if ((saveCategoryBrands).size() > 0 && saveCategoryBrands != null) {
            for (CategoryBrand c : saveCategoryBrands) {
                Brand brand = new Brand();
                brand.setId(c.getBrandId());
                brand = brandService.selectOne(brand);
                if (StringUtils.equals(brand.getIsValid(), ValidEnum.NOVALID.getCode())) {
                    noValidBrands.add(brand);
                }
            }
            if (noValidBrands.size() > 0) {
                String msg = "操作失败,存在已被停用的品牌";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }
        //整理页面上新增的数据
        List<CategoryBrand> newCategoryBrands = new ArrayList<>();
        Example example = new Example(CategoryBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        //查询该categoryId已经关联的旧数据
        List<CategoryBrand> oldCategoryBrands = categoryBrandService.selectByExample(example);
        //整理出新增的数据
        if (!AssertUtil.collectionIsEmpty(oldCategoryBrands)) {
            for (CategoryBrand categoryBrand : saveCategoryBrands) {
                boolean flag = false;
                for (CategoryBrand categoryB : oldCategoryBrands) {
                    if (categoryBrand.getBrandId().equals(categoryB.getBrandId())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    newCategoryBrands.add(categoryBrand);
                }
            }
        } else {
            newCategoryBrands = saveCategoryBrands;
        }
        //插入新增的数据
        if (!AssertUtil.collectionIsEmpty(newCategoryBrands)) {
            categoryBrandService.insertList(newCategoryBrands);
        }
        //删除的数据
        if (!AssertUtil.collectionIsEmpty(delBrandIdsList)) {
            categoryBrandService.deleteCategoryBrand(delCategoryBrands);
        }

        if (!AssertUtil.collectionIsEmpty(newCategoryBrands) || !AssertUtil.collectionIsEmpty(delBrandIdsList)) {
            List<CategoryBrand> updateCategoryBrandList = categoryBrandService.selectByExample(example);
            if (!AssertUtil.collectionIsEmpty(updateCategoryBrandList)){
                noticeCategory(TrcActionTypeEnum.EDIT_CATEGORY_BRAND, null, null, updateCategoryBrandList, null, System.currentTimeMillis());
            }
        }

        recordLinkLog(categoryId, aclUserAccreditInfo, "品牌");

    }

    //根据id组装出List<CategoryBrand
    private List<CategoryBrand> assembleList(List<Long> brandIdsList, Long categoryId,boolean check) throws Exception {
        List<Brand> brands = brandService.selectBrandList(brandIdsList);
        Category category = new Category();
        category.setId(categoryId);
        List<String> noValidName = new ArrayList<>();
        category = categoryService.selectOne(category);
        AssertUtil.notNull(category, "根据Id未查询到分类!");
        List<CategoryBrand> categoryBrands = new ArrayList<>();
        for (Brand brand : brands) {
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandCode(brand.getBrandCode());
            categoryBrand.setBrandId(brand.getId());
            categoryBrand.setCategoryId(category.getId());
            if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), brand.getIsValid())) {
                noValidName.add(brand.getName());
            } else {
                categoryBrand.setIsValid(brand.getIsValid());
            }
            categoryBrand.setIsDeleted(brand.getIsDeleted());
            categoryBrand.setCategoryCode(category.getCategoryCode());
            categoryBrand.setCreateTime(Calendar.getInstance().getTime());
            categoryBrands.add(categoryBrand);
        }
        if (!AssertUtil.collectionIsEmpty(noValidName)&&check) {
            throw new CategoryException(ExceptionEnum.CATEGORY_LINK_LEVEL_EXCEPTION, String.format("请先删除已经停用的品牌[%s]", StringUtils.join(noValidName, SupplyConstants.Symbol.COMMA)));
        }
        return categoryBrands;
    }

    /**
     * 查询已经关联的属性
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public List<CategoryProperty> queryCategoryProperty(Long categoryId) throws Exception {
        AssertUtil.notNull(categoryId, "查询分类关联属性categoryId为空");
        categoryLevel(categoryId);
        Example example = new Example(CategoryProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        example.orderBy("propertySort").asc();
        List<CategoryProperty> categoryProperties = categoryPropertyService.selectByExample(example);
        List<Long> propertyIds = new ArrayList<>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            propertyIds.add(categoryProperty.getPropertyId());
        }
        List<Property> propertyList = new ArrayList<Property>();
        if (propertyIds.size() > 0) {
            propertyList = propertyService.queryPropertyList(propertyIds);
        }
        if (propertyList.size() > 0) {
            for (CategoryProperty c : categoryProperties) {
                for (Property p : propertyList) {
                    if (StringUtils.equals(c.getPropertyId().toString(), p.getId().toString())) {
                        c.setName(p.getName());
                        c.setTypeCode(p.getTypeCode());
                        c.setValueType(p.getValueType());
                    }
                }
            }
        }
        return categoryProperties;
    }

    /**
     * 数据关联
     *
     * @param categoryId
     * @param jsonDate
     * @throws Exception
     */
    @Override
    @CategoryCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void linkCategoryProperties(Long categoryId, String jsonDate, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(categoryId, "分类关联属性categoryId为空");
        AssertUtil.notBlank(jsonDate, "分类关联属性表格数据为空");
        categoryLevel(categoryId);
        List<TableDate> tableDates = checkPropertyValid(jsonDate);
        //需要新增的的数据
        List<CategoryProperty> insertProperties = new ArrayList<>();
        //需要排序的数据
        List<CategoryProperty> sortProperties = new ArrayList<>();
        //需要删除的的数据
        List<CategoryProperty> delProperties = new ArrayList<>();
        //未改动的的数据
//        List<CategoryProperty> defaultProperties = new ArrayList<>();
        //将数据分组
        for (TableDate tableDate : tableDates) {
            //新增的数据
            if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), (tableDate.getStatus()))) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setPropertySort(tableDate.getPropertySort());
                categoryProperty.setCategoryId(categoryId);
                categoryProperty.setIsValid(tableDate.getIsValid());
                categoryProperty.setCreateTime(Calendar.getInstance().getTime());
                categoryProperty.setPropertyId(tableDate.getId());
                categoryProperty.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                checkCategoryProperty(categoryProperty);
                insertProperties.add(categoryProperty);
            }
            //删除的数据
            if (StringUtils.equals(ZeroToNineEnum.THREE.getCode(), (tableDate.getStatus()))) {
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getPropertyId());
                categoryProperty.setPropertySort(tableDate.getPropertySort());
                delProperties.add(categoryProperty);
            }

            if (StringUtils.equals(ZeroToNineEnum.TWO.getCode(), (tableDate.getStatus()))) {
                //排序的数据
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setCategoryId(categoryId);
                categoryProperty.setId(tableDate.getId());
                categoryProperty.setPropertyId(tableDate.getPropertyId());
                categoryProperty.setPropertySort(tableDate.getPropertySort());
                sortProperties.add(categoryProperty);
            }
        }
        //整理页面上新增的数据
        List<CategoryProperty> newCategoryProperties = new ArrayList<>();
        Example example = new Example(CategoryProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        if (!AssertUtil.collectionIsEmpty(insertProperties)) {
            //查询该categoryId已经关联的旧数据
            List<CategoryProperty> oldCategoryProperties = categoryPropertyService.selectByExample(example);
            //整理出新增的数据
            if (oldCategoryProperties.size() > 0) {
                for (CategoryProperty categoryProperty : insertProperties) {
                    boolean flag = false;
                    for (CategoryProperty categoryP : oldCategoryProperties) {
                        if (categoryProperty.getPropertyId().equals(categoryP.getPropertyId())) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        newCategoryProperties.add(categoryProperty);
                    }
                }
            } else {
                newCategoryProperties = insertProperties;
            }
            //校验
            List<Property> noValidProperty = new ArrayList<>();
            List<CategoryProperty> pageList = new ArrayList<>();
            pageList.addAll(newCategoryProperties);
            pageList.addAll(sortProperties);
            if (!AssertUtil.collectionIsEmpty(pageList)) {
                for (CategoryProperty c : pageList) {
                    Property property = new Property();
                    property.setId(c.getPropertyId());
                    property = propertyService.selectOne(property);
                    if (StringUtils.equals(property.getIsValid(), ValidEnum.NOVALID.getCode())) {
                        noValidProperty.add(property);
                    }
                }
                if (noValidProperty.size() > 0) {
                    String msg = "操作失败,存在已被停用的属性";
                    log.error(msg);
                    throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
                } else {
                    newCategoryProperties = filterIdCategoryProperty(newCategoryProperties);
                    categoryPropertyService.insertList(newCategoryProperties);
                }

            }
        }

        if (!AssertUtil.collectionIsEmpty(delProperties)) {
            //删除
            int delCount = 0;
            for (CategoryProperty categoryProperty : delProperties) {
                AssertUtil.notNull(categoryProperty.getId(), "根据关联属性id删除关联时,参数Id为空");
                categoryPropertyService.deleteByPrimaryKey(categoryProperty.getId());
                delCount++;
            }
            if (delCount == 0) {
                String msg = "删除分类属性" + JSON.toJSONString(sortProperties) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_PROPERTY_DELETE_EXCEPTION, msg);
            }
        }
        if (!AssertUtil.collectionIsEmpty(sortProperties)) {
            int count = categoryPropertyService.updateCategoryPropertySort(sortProperties);
            count++;
            if (count == 0) {
                String msg = "修改分类属性" + JSON.toJSONString(sortProperties) + "操作失败";
                log.error(msg);
                throw new CategoryException(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION, msg);
            }
        }

        if (!AssertUtil.collectionIsEmpty(newCategoryProperties) || !AssertUtil.collectionIsEmpty(delProperties) || !AssertUtil.collectionIsEmpty(sortProperties)) {
            List<CategoryProperty> updateCategoryProperty = categoryPropertyService.selectByExample(example);
            if(AssertUtil.collectionIsEmpty(updateCategoryProperty)){
                noticeCategory(TrcActionTypeEnum.EDIT_CATEGORY_PROPERTY, null, null, null, updateCategoryProperty, System.currentTimeMillis());
            }
        }
        recordLinkLog(categoryId, aclUserAccreditInfo, "属性");

    }

    /**
     * 检验属性起停用状态
     *
     * @param jsonDate
     * @return
     */
    private List<TableDate> checkPropertyValid(String jsonDate) {
        List<Long> pageIds = new ArrayList<>();
        List<String> noValidName = new ArrayList<>();
        List<TableDate> tableDates = JSONArray.parseArray(jsonDate, TableDate.class);
        for (TableDate tableDate : tableDates) {
            if (!StringUtils.equals(tableDate.getStatus(), ZeroToNineEnum.THREE.getCode())) {
                pageIds.add(tableDate.getPropertyId());
            }
        }
        if (!AssertUtil.collectionIsEmpty(pageIds)) {
            Example exampleIds = new Example(Property.class);
            Example.Criteria criteriaIds = exampleIds.createCriteria();
            criteriaIds.andIn("id", pageIds);
            criteriaIds.andEqualTo("isValid", ZeroToNineEnum.ZERO.getCode());
            List<Property> pagePropertyList = propertyService.selectByExample(exampleIds);
            if (!AssertUtil.collectionIsEmpty(pagePropertyList)) {
                pagePropertyList.stream().
                        forEach(pageProperty -> {
                            noValidName.add(pageProperty.getName());
                        });
                throw new CategoryException(ExceptionEnum.CATEGORY_LINK_LEVEL_EXCEPTION, String.format("请先删除已经停用的属性[%s]", StringUtils.join(noValidName, SupplyConstants.Symbol.COMMA)));
            }
        }
        return tableDates;
    }

    /**
     * 关联日志
     *
     * @param categoryId
     * @param aclUserAccreditInfo
     */
    private void recordLinkLog(Long categoryId, AclUserAccreditInfo aclUserAccreditInfo, String type) {
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        //分类状态更新时需要更新分类供应商关系表的is_valid字段
        Category logCategory = categoryService.selectByPrimaryKey(categoryId);
        String categoryAction = getCategoryAction(logCategory);
        logInfoService.recordLog(logCategory, String.valueOf(logCategory.getId()), userId, String.format(categoryAction + "[%s]", logCategory.getName()), "关联" + type + "修改", null);
    }

    //关联参数属性校验
    private void checkCategoryProperty(CategoryProperty categoryProperty) {
        AssertUtil.notNull(categoryProperty.getCategoryId(), "关联分类属性时,参数categoryId为空");
        AssertUtil.notNull(categoryProperty.getPropertyId(), "关联分类属性时,参数propertyId为空");
        AssertUtil.notNull(categoryProperty.getPropertySort(), "关联分类属性时,参数propertySort为空");
        AssertUtil.notNull(categoryProperty.getIsValid(), "关联分类属性时,参数isValid为空");
        AssertUtil.notNull(categoryProperty.getCreateTime(), "关联分类属性时,参数createTime为空");
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
    public String getCategoryName(Long categoryId) throws Exception {
        List<String> categoryNames = queryCategoryNamePath(categoryId);
        AssertUtil.notEmpty(categoryNames, String.format("根据分类ID[%s]查询分类全路径名称为空", categoryId.toString()));
        String categoryName = "";
        for (String name : categoryNames) {
            categoryName = name + CATEGORY_NAME_SPLIT_SYMBOL + categoryName;
        }
        if (categoryName.length() > 0) {
            categoryName = categoryName.substring(0, categoryName.length() - 1);
        }
        return categoryName;
    }

    /**
     * 过滤重复Id
     */
    private Set filterId(String ids) throws Exception {
        Set<Long> set = new LinkedHashSet<Long>();
        Long[] idsArray = StringUtil.splitByComma(ids);
        for (Long longId : idsArray) {
            set.add(longId);
        }
        return set;
    }

    /**
     * 过滤重复数据
     *
     * @param categoryProperties
     * @return
     */
    private List<CategoryProperty> filterIdCategoryProperty(List<CategoryProperty> categoryProperties) {
        //过滤
        for (int i = 0; i < categoryProperties.size(); i++) {
            for (int j = 0; j < categoryProperties.size(); j++) {
                if (i == j) continue;
                if (categoryProperties.get(i).getPropertyId().equals(categoryProperties.get(j).getPropertyId())) {
                    categoryProperties.remove(j);
                }
            }
        }
        return categoryProperties;
    }

    /**
     * 分类变更通知
     */
    private void noticeCategory(TrcActionTypeEnum action, Category oldCategory, Category category,
                                List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) {
        Runnable runnable = () -> {
            try {
                trcBiz.sendCategory(action, oldCategory, category, categoryBrandList, categoryPropertyList, operateTime);
                log.info("通知Trc分类变更成功");
            } catch (Exception e) {
                log.error("通知Trc分类变更异常" + e.getMessage(), e);
            }
        };


        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    /**
     * 分类名称校验
     * @param parentId
     * @param name
     */
    @Override
    public void checkCategoryName(Long id,Long parentId,String name){
        AssertUtil.notBlank(name,"分类名称不能为空!");
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if (null == parentId){
            criteria.andIsNull("parentId");
        }else {
            criteria.andEqualTo("parentId", parentId);
        }
        List<Category> categoryList = categoryService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(categoryList)) {
            for (Category categoryItem : categoryList) {
                if (StringUtils.equals(name, categoryItem.getName())&&categoryItem.getId().longValue()!=(null==id?0:id.longValue())) {
                    String msg = "分类名称[" + name + "]已存在";
                    log.error(msg);
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
                }
            }
        }
    }
}
