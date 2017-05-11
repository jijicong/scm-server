package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.trc.biz.category.ICategoryBiz;
import org.trc.domain.category.Category;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.CategoryForm;
import org.trc.domain.category.TreeNode;
import org.trc.service.category.ICategoryService;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by hzszy on 2017/5/5.
 */
@Service
public class CategoryBiz implements ICategoryBiz{
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(CategoryBiz.class);
    @Resource
    private ICategoryService categoryService;

    @Override
    public List<Category> queryClassify(CategoryForm categoryForm) throws Exception {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("level", ZeroToNineEnum.TWO.getCode());


        return categoryService.selectByExample(example);
    }

    @Override
    public int updateCategory(Category category, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改分类参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        category.setId(id);
        category.setUpdateTime(new Date());
        count = categoryService.updateByPrimaryKeySelective(category);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改分类", JSON.toJSONString(category),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public int deleteCategory(Long id) throws Exception {
        return 0;
    }

    @Override
    public int saveClassify(Category category) throws Exception {
        int count =0;
        if (null !=category.getId()){
            //修改
            category.setUpdateTime(Calendar.getInstance().getTime());
            count = categoryService.updateByPrimaryKeySelective(category);
        }else {
            //add
            ParamsUtil.setBaseDO(category);
            count = categoryService.insert(category);
        }
        if(count == 0){
            String msg = CommonUtil.joinStr("保存分类", JSON.toJSONString(category),"到数据库失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,msg);
        }
        return count;
    }

    @Override
    public List<TreeNode> getTreeNode(CategoryForm categoryForm) throws Exception {
        /**
         * 1、查询根节点
         */
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNull("parentId");
        example.orderBy("sort").asc();
        List<Category> rootClassifyList  = categoryService.selectByExample(example);
        List<TreeNode> rootTreeNodeList = new ArrayList<TreeNode>();
        List<Long> firstIds = new ArrayList<Long>();

        //查询节点数据库操作
        for (int i = 0; i < rootClassifyList.size(); i++) {
            TreeNode rootTreeNode1 = new TreeNode();
            rootTreeNode1.setId(rootClassifyList.get(i).getId().toString());
            rootTreeNode1.setText(rootClassifyList.get(i).getName());
            rootTreeNode1.setSort(rootClassifyList.get(i).getSort());
            rootTreeNode1.setIsValid(rootClassifyList.get(i).getIsValid());
            rootTreeNode1.setLevel(rootClassifyList.get(i).getLevel());
            rootTreeNode1.setFullPathId(rootClassifyList.get(i).getFullPathId());
            rootTreeNode1.setSource(rootClassifyList.get(i).getSource());
            rootTreeNode1.setCategoryCode(rootClassifyList.get(i).getCategoryCode());
            rootTreeNode1.setIsLeaf(rootClassifyList.get(i).getIsLeaf());
            rootTreeNodeList.add(rootTreeNode1);
            firstIds.add(rootClassifyList.get(i).getId());
        }
        Map<Long, List<TreeNode>> firstMap =  handlerNextTreeNode(firstIds);
        /**
         * 2、查询第二级分类节点
         */
        //查询所有父节点是根节点ID的记录
        Example exampleLevel2  =new Example(Category.class);
        criteria=  exampleLevel2.createCriteria();
        criteria.andEqualTo("level", ZeroToNineEnum.TWO.getCode().toString());
        exampleLevel2.orderBy("sort").asc();
        List<Category> secondClassifyList = categoryService.selectByExample(exampleLevel2);
//        List<TreeNode> secondTreeNodeList = new ArrayList<TreeNode>();
        List<Long> secondIds = new ArrayList<Long>();
        for(Category cls : secondClassifyList){
            secondIds.add(cls.getId());
        }
        /**
         * 3、查询和组装所有第二级节点相关的第三级节点数据
         */

        Map<Long, List<TreeNode>> map =  handlerNextTreeNode(secondIds);
        for (Map.Entry<Long, List<TreeNode>> entry : firstMap.entrySet()) {
            List<TreeNode> secondList = entry.getValue();
            for(TreeNode treeNode : secondList){
                treeNode.setChildren(map.get(Long.parseLong(treeNode.getId())));
            }
        }
        for (int i = 0; i <rootTreeNodeList.size() ; i++) {
            rootTreeNodeList.get(i).setChildren(firstMap.get(firstIds.get(i)));
        }

        return rootTreeNodeList;
    }

    @Override
    public int queryCount() throws Exception {

        return  categoryService.queryCategoryCount();
    }

    /**
     * 查询组装下一级ID
     * @param parentIds 父节点ID
     * @return
     */
   private  Map<Long,List<TreeNode>> handlerNextTreeNode(List<Long> parentIds){
        //根据第上级节点ID列表批量查询下级节点
        Example example  =new Example(Category.class);
        Example.Criteria criteria  = example.createCriteria();
        criteria.andIn("parentId",parentIds);
        List<Category> nextCategoryList = categoryService.selectByExample(example);
        example.orderBy("sort").asc();
       Map<Long, List<TreeNode>> map = new HashMap<Long, List<TreeNode>>();
        for(Long id : parentIds){
            List<TreeNode> nextTreeNodeList = new ArrayList<TreeNode>();
            for(Category cls : nextCategoryList){
                if(id.toString() .equals( cls.getParentId())){
                    TreeNode nextTreeNode = new TreeNode();
                    nextTreeNode.setId(cls.getId().toString());
                    nextTreeNode.setText(cls.getName());
                    nextTreeNode.setIsValid(cls.getIsValid());
                    nextTreeNode.setSort(cls.getSort());
                    nextTreeNode.setLevel(cls.getLevel());
                    nextTreeNode.setFullPathId(cls.getFullPathId());
                    nextTreeNode.setSource(cls.getSource());
                    nextTreeNode.setIsLeaf(cls.getIsLeaf());
                    nextTreeNode.setCategoryCode(cls.getCategoryCode());
                    nextTreeNode.setChildren(null);
                    nextTreeNodeList.add(nextTreeNode);
                }
            }
            map.put(id, nextTreeNodeList);
        }
        return map;
    }


}
