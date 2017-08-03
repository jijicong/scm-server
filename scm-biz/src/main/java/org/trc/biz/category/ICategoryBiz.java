package org.trc.biz.category;

import org.trc.domain.category.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.TreeNode;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryBiz {

    /**
     * 分类分页查询
     *
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Category> categoryPage(CategoryForm queryModel, Pagenation<Category> page) throws Exception;

    /**
     * 获取分类树节点
     *
     * @return
     * @throws Exception
     */
    List<TreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception;

    /**
     * 修改
     *
     * @param category
     * @return
     * @throws Exception
     */
    void updateCategory(Category category, boolean isSave, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 保存
     */
    void saveCategory(Category category, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;


    /**
     * 查询分类编码是否存在
     *
     * @param categoryCategory
     * @return
     * @throws Exception
     */
    int checkCategoryCode(Long id, String categoryCategory) throws Exception;

    /**
     * 是否为叶子节点
     */
    int isLeaf(Long id) throws Exception;

    /**
     * 更新排序
     */
    void updateSort(String sortDate) throws Exception;

    void updateState(Category category, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    void updateIsLeaf(Category category) throws Exception;

    /**
     * 查询分类品牌列表
     *
     * @param categoryBrandForm
     * @return
     * @throws Exception
     */
    List<CategoryBrandExt> queryCategoryBrands(CategoryBrandForm categoryBrandForm) throws Exception;

    /**
     * 根据ID查询
     */
    Long queryPathId(Long id) throws Exception;

    List<String> queryCategoryNamePath(Long id) throws Exception;

    /**
     * 根据categoryId查询CategoryBrand
     *
     * @param categoryId
     * @return
     */
    List<CategoryBrand> queryBrands(Long categoryId) throws Exception;

    /**
     * 根据categoryId查询CategoryProperty
     *
     * @param categoryId
     * @return
     */
    List<CategoryProperty> queryProperties(Long categoryId) throws Exception;

    /**
     * 关联分类与品牌
     *
     * @param categoryId
     * @param brandIds
     * @throws Exception
     */
    void linkCategoryBrands(Long categoryId, String brandIds, String delRecord, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;


    /**
     * 查询已关联的分类属性
     *
     * @param id
     * @return
     * @throws Exception
     */
    List<CategoryProperty> queryCategoryProperty(Long id) throws Exception;


    /**
     * 更新分类属性
     */
    void linkCategoryProperties(Long categoryId, String jsonDate, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * @param categoryId
     * @return
     * @throws Exception
     */
    String getCategoryName(Long categoryId) throws Exception;

    /**
     * 检验启停yong
     */
    Integer checkCategoryIsValid(Long categoryId) throws Exception;

    /**
     * @param categoryForm
     * @return
     * @throws Exception
     */
    List<Category> queryCategorys(@BeanParam CategoryForm categoryForm) throws Exception;

    Pagenation<Brand> brandListCategory(BrandForm queryModel, Pagenation<Brand> page) throws Exception;
}
