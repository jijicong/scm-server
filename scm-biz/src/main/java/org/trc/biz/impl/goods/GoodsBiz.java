package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.GoodsException;
import org.trc.exception.ParamValidException;
import org.trc.exception.SupplierException;
import org.trc.form.goods.ItemsForm;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemNatureProperyService;
import org.trc.service.impl.goods.ItemSalesProperyService;
import org.trc.service.util.ISerialUtilService;
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
    //SKU的属性值ID分割符号
    public static final String SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL = ",";
    //金额数字
    public static final Integer MONEY_MULTI = 100;

    @Autowired
    private IItemsService itemsService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ISkusService skusService;
    @Autowired
    private ItemNatureProperyService itemNatureProperyService;
    @Autowired
    private ItemSalesProperyService itemSalesProperyService;


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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery) throws Exception {
        AssertUtil.notBlank(itemNaturePropery.getNaturePropertys(), "提交商品信息自然属性不能为空");
        AssertUtil.notBlank(itemSalesPropery.getSalesPropertys(), "提交商品信息采购属性不能为空");
        AssertUtil.notBlank(skus.getSkusInfo(), "提交商品信息SKU信息不能为空");
        checkSkuInfo(skus);//检查sku参数
        //生成序列号
        String code = serialUtilService.generateCode(SupplyConstants.Serial.SPU_LENGTH, SupplyConstants.Serial.SPU_NAME, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        items.setSpuCode(code);
        ParamsUtil.setBaseDO(items);
        //保存商品基础信息
        saveItemsBase(items);
        //保存sku信息
        skus.setItemId(items.getId());
        skus.setSpuCode(items.getSpuCode());
        List<Skus> skuss = saveSkus(skus);
        //保存自然属性信息
        itemNaturePropery.setItemId(items.getId());
        itemNaturePropery.setSpuCode(items.getSpuCode());
        saveItemNatureProperty(itemNaturePropery);
        //保存采购属性信息
        saveItemSalesPropery(itemSalesPropery, skuss);
    }

    /**
     *
     * @param skus
     */
    private void checkSkuInfo(Skus skus){
        JSONArray skuArray = JSONArray.parseArray(skus.getSkusInfo());
        if(skuArray.size() == 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "提交商品信息SKU信息不能为空");
        }
        List<Skus> list = new ArrayList<Skus>();
        for(Object obj : skuArray){
            JSONObject jbo = (JSONObject) obj;
            AssertUtil.notBlank(jbo.getString("barCode"),"SKU条形码不能为空");
            AssertUtil.notNull(jbo.getLong("weight"),"SKU重量不能为空");
        }
    }

    /**
     * 保存商品基础信息
     * @param items
     * @throws Exception
     */
    private void saveItemsBase(Items items) throws Exception{
        ParamsUtil.setBaseDO(items);
        int count = itemsService.insert(items);
        if (count == 0) {
            String msg = String.format("保商品基础信息%s到数据库失败", JSON.toJSONString(items));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存SKU信息
     * @param skus
     * @throws Exception
     */
    private List<Skus> saveSkus(Skus skus) throws Exception{
        JSONArray skuArray = JSONArray.parseArray(skus.getSkusInfo());
        List<Skus> list = new ArrayList<Skus>();
        for(Object obj : skuArray){
            JSONObject jbo = (JSONObject) obj;
            String code = serialUtilService.generateCode(SupplyConstants.Serial.SKU_LENGTH, SupplyConstants.Serial.SKU_NAME,
                    SupplyConstants.Serial.SKU_INNER, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            Skus skus2 = new Skus();
            skus2.setSkuCode(code);
            skus2.setItemId(skus.getItemId());
            skus2.setSpuCode(skus.getSpuCode());
            skus2.setPropertyValueId(jbo.getString("propertyValueId"));
            skus2.setPropertyValue(jbo.getString("propertyValue"));
            skus2.setBarCode(jbo.getString("barCode"));
            skus2.setMarketPrice(jbo.getLong("marketPrice") * MONEY_MULTI);
            skus2.setPicture(jbo.getString("picture"));
            skus2.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            Date sysTime = Calendar.getInstance().getTime();
            skus2.setCreateTime(sysTime);
            skus2.setUpdateTime(sysTime);
            list.add(skus2);
        }
        int count = skusService.insertList(list);
        if (count == 0) {
            String msg = String.format("保存商品SKU信息%s到数据库失败", JSON.toJSONString(list));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
        return list;
    }

    /**
     * 保存商品自然属性
     * @param itemNaturePropery
     */
    private void saveItemNatureProperty(ItemNaturePropery itemNaturePropery){
        JSONArray categoryArray = JSONArray.parseArray(itemNaturePropery.getNaturePropertys());
        List<ItemNaturePropery> itemNatureProperies = new ArrayList<ItemNaturePropery>();
        for(Object obj : categoryArray){
            JSONObject jbo = (JSONObject) obj;
            ItemNaturePropery _itemNaturePropery = new ItemNaturePropery();
            _itemNaturePropery.setItemId(itemNaturePropery.getItemId());
            _itemNaturePropery.setSpuCode(itemNaturePropery.getSpuCode());
            _itemNaturePropery.setPropertyId(jbo.getLong("propertyId"));
            _itemNaturePropery.setPropertyValueId(jbo.getLong("propertyValueId"));
            _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            Date sysTime = Calendar.getInstance().getTime();
            _itemNaturePropery.setCreateTime(sysTime);
            _itemNaturePropery.setUpdateTime(sysTime);
            itemNatureProperies.add(_itemNaturePropery);
        }
        int count = itemNatureProperyService.insertList(itemNatureProperies);
        if (count == 0) {
            String msg = String.format("保存商品自然属性信息%s到数据库失败", JSON.toJSONString(itemNatureProperies));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存商品采购属性
     * @param skuses
     */
    private void saveItemSalesPropery(ItemSalesPropery itemSalesPropery, List<Skus> skuses){
        List<ItemSalesPropery> itemSalesProperys = new ArrayList<ItemSalesPropery>();
        Date sysTime = Calendar.getInstance().getTime();
        for(Skus skus : skuses){
            String[] propertyValueIdsArray = skus.getPropertyValueId().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            String[] propertyValuesArray = skus.getPropertyValue().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            for(int i=0; i<propertyValueIdsArray.length; i++){
                Long propertyValueId = Long.parseLong(propertyValueIdsArray[i]);
                ItemSalesPropery _itemSalesPropery = new ItemSalesPropery();
                _itemSalesPropery.setItemId(skus.getItemId());
                _itemSalesPropery.setSpuCode(skus.getSpuCode());
                _itemSalesPropery.setSkuCode(skus.getSkuCode());
                String[] _tmp = getPropertyIdAndPicture(itemSalesPropery, propertyValueId);
                _itemSalesPropery.setPropertyId(Long.parseLong(_tmp[0]));
                _itemSalesPropery.setPropertyValueId(propertyValueId);
                _itemSalesPropery.setPropertyActualValue(propertyValuesArray[i]);
                _itemSalesPropery.setPicture(_tmp[1]);
                _itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                _itemSalesPropery.setCreateTime(sysTime);
                _itemSalesPropery.setUpdateTime(sysTime);
                itemSalesProperys.add(_itemSalesPropery);
            }
        }
        int count = itemSalesProperyService.insertList(itemSalesProperys);
        if (count == 0) {
            String msg = String.format("保存商品采购属性信息%s到数据库失败", JSON.toJSONString(itemSalesProperys));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 获取属性值ID对应的属性ID和属性值对应的图片路径
     * @param itemSalesPropery
     * @param propertyValueId
     * @return
     */
    private String[] getPropertyIdAndPicture(ItemSalesPropery itemSalesPropery, Long propertyValueId){
        JSONArray categoryArray = JSONArray.parseArray(itemSalesPropery.getSalesPropertys());
        String[] _result = new String[2];
        for(Object obj : categoryArray){
            JSONObject jbo = (JSONObject) obj;
            if(propertyValueId == jbo.getLong("propertyValueId")){
                _result[0] = jbo.getString("propertyId");
                _result[1] = jbo.getString("picture");
                break;
            }
        }
        return _result;
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
