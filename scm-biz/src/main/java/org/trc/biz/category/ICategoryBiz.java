package org.trc.biz.category;

import org.trc.domain.category.Category;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.TreeNode;

import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public interface ICategoryBiz {
    public List<Category> queryClassify(CategoryForm classifyForm) throws Exception;
    /**
     * 修改
     * @param category
     * @param id
     * @return
     * @throws Exception
     */
    public int updateCategory(Category category, Long id) throws Exception;

    /**
     * 保存
     */
    public int saveClassify(Category category) throws Exception;
    /**
     * 根据主键删除
     */
    public int deleteCategory(Long id) throws  Exception;

    /**
     * 获取分了树节点
     * @param categoryForm
     * @return
     * @throws Exception
     */
    public List<TreeNode> getTreeNode(CategoryForm categoryForm) throws Exception;

    /**
     * 测试
     */
    public int queryCount() throws Exception;

}
