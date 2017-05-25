package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.goods.Items;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.GoodsException;
import org.trc.form.goods.ItemsForm;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("goodsBiz")
public class GoodsBiz implements IGoodsBiz {

    private final static Logger log = LoggerFactory.getLogger(GoodsBiz.class);

    //分类ID全路径分割符号
    public static final String CATEGORY_ID_SPLIT_SYMBOL = "|";
    //分类名称全路径分割符号
    public static final String CATEGORY_NAME_SPLIT_SYMBOL = "/";

    @Autowired
    private IItemsService itemsService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private ICategoryService categoryService;


    @Override
    public Pagenation<Items> ItemsPage(ItemsForm queryModel, Pagenation<Items> page) throws Exception {
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getName())) {//商品名称
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSpuCode())) {//SPU编码
            criteria.andLike("spuCode", "%" + queryModel.getSpuCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSkuCode())) {//SKU编码
            criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
        }
        if (null != queryModel.getCategoryId()) {//商品所属分类ID
            criteria.andEqualTo("categoryId", queryModel.getCategoryId());
        }
        if (null != queryModel.getBrandId()) {//商品所属品牌ID
            criteria.andEqualTo("brandId", queryModel.getBrandId());
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate, 1));
        }
        if (StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        page = itemsService.pagination(example, page, queryModel);
        handerPage(page);
        //分页查询
        return page;
    }

    private void handerPage(Pagenation<Items> page){
        List<Long> categoryIds = new ArrayList<Long>();
        List<Long> brandIds = new ArrayList<Long>();
        for(Items item : page.getResult()){
            categoryIds.add(item.getCategoryId());
            brandIds.add(item.getBrandId());
        }
        if(categoryIds.size() > 0){
            setCategoryName(page.getResult(), categoryIds);
        }
        if(brandIds.size() > 0){
            setBrandName(page.getResult(), brandIds);
        }
    }

    /**
     *设置分类名称
     * @param items
     * @param categoryIds
     */
    private void setCategoryName(List<Items> items, List<Long> categoryIds){
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", categoryIds);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        List<Category> thridCategories = categoryService.selectByExample(example);
        AssertUtil.notEmpty(thridCategories,String.format("查询商品所属分类ID为[%s]的分类信息为空", CommonUtil.converCollectionToString(categoryIds)));
        /**
         * 将分类的全路径ID(full_path_id)取出来，然后从中取到从第一级到第三季的所有分类ID
         * 放到分类ID列表categoryIds中
         */
        for(Category c : thridCategories){
            String[] tmps = c.getFullPathId().split("\\"+CATEGORY_ID_SPLIT_SYMBOL);
            for(String s : tmps){
                categoryIds.add(Long.parseLong(s));
            }
        }
        List<Category> categories = categoryService.selectByExample(example);
        //获取三级分类对应的全路径名称
        Map<Long, String> map = getThirdCategoryFullPathName(thridCategories, categories);
        for(Items items2 : items){
            items2.setCategoryName(map.get(items2.getCategoryId()));
        }
    }

    /**
     * 获取第三级分类全路径名称
     * @param thirdCategories 第三级分类列表
     * @param categories 当前相关所有分类列表
     * @return
     */
    private Map<Long, String> getThirdCategoryFullPathName(List<Category> thirdCategories, List<Category> categories){
        Map<Long, String> map = new HashMap<Long, String>();
        for(Category c : thirdCategories){
            String[] tmps = c.getFullPathId().split("\\"+CATEGORY_ID_SPLIT_SYMBOL);
            StringBuilder sb = new StringBuilder();
            //第一级分类名称
            for(Category c2 : categories){
                if(Long.parseLong(tmps[0]) == c2.getId()){
                    sb.append(c2.getName());
                    break;
                }
            }
            //第二级分类名称
            for(Category c2 : categories){
                if(Long.parseLong(tmps[1]) == c2.getId()){
                    sb.append(CATEGORY_NAME_SPLIT_SYMBOL).append(c2.getName());
                    break;
                }
            }
            //第三级分类名称
            for(Category c2 : categories){
                if(Long.parseLong(tmps[2]) == c2.getId()){
                    sb.append(CATEGORY_NAME_SPLIT_SYMBOL).append(c2.getName());
                    break;
                }
            }
            map.put(c.getId(), sb.toString());
        }
        return map;
    }

    /**
     *设置品牌名称
     * @param items
     * @param brandIds
     */
    private void setBrandName(List<Items> items, List<Long> brandIds){
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", brandIds);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        List<Brand> brands = brandService.selectByExample(example);
        AssertUtil.notEmpty(brands,String.format("查询商品品牌ID为[%s]的品牌信息为空", CommonUtil.converCollectionToString(brandIds)));
        for(Items items2 : items){
            for(Brand c : brands){
                if(items2.getBrandId() == c.getId()){
                    items2.setBrandName(c.getName());
                    break;
                }
            }
        }
    }

    @Override
    public List<Items> queryItems(ItemsForm itemsForm) throws Exception {
        Items items = new Items();
        BeanUtils.copyProperties(itemsForm, items);
        if(StringUtils.isEmpty(itemsForm.getIsValid())){
            items.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        items.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return itemsService.select(items);
    }

    @Override
    public void saveItems(Items items) throws Exception {
        ParamsUtil.setBaseDO(items);
        int count = itemsService.insert(items);
        if(count == 0){
            String msg = CommonUtil.joinStr("保存商品", JSON.toJSONString(items),"数据库操作失败").toString();
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    public void updateItems(Items items) throws Exception {

    } 

    @Override
    public void updateValid(Long id, String isValid) throws Exception {
        AssertUtil.notNull(id, "商品启用/停用操作供应商ID不能为空");
        AssertUtil.notBlank(isValid, "商品启用/停用操作参数isValid不能为空");
        Items items = new Items();
        items.setId(id);
        items.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            items.setIsValid(ZeroToNineEnum.ONE.getCode());
        }else{
            items.setIsValid(ZeroToNineEnum.ZERO.getCode());
        }
        int count = itemsService.updateByPrimaryKeySelective(items);
        if(count == 0){
            String msg = "商品启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
    }


}
