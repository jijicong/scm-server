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
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.*;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.GoodsException;
import org.trc.exception.ParamValidException;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
import org.trc.service.category.*;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemNatureProperyService;
import org.trc.service.impl.goods.ItemSalesProperyService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("goodsBiz")
public class GoodsBiz implements IGoodsBiz {

    private Logger  log = LoggerFactory.getLogger(GoodsBiz.class);

    //分类ID全路径分割符号
    public static final String CATEGORY_ID_SPLIT_SYMBOL = "|";
    //分类名称全路径分割符号
    public static final String CATEGORY_NAME_SPLIT_SYMBOL = "/";
    //SKU的属性值ID分割符号
    public static final String SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL = ",";
    //SKU的属性组合名称分割符号
    public static final String SKU_PROPERTY_COMBINE_NAME_SPLIT_SYMBOL = ":";
    //SKU的属性组合名称空格
    public static final String SKU_PROPERTY_COMBINE_NAME_EMPTY = "&nbsp&nbsp&nbsp";
    //金额数字
    public static final Double MONEY_MULTI = 100.0;
    //自然属性
    public static final String NATURE_PROPERTY = "natureProperty";
    //采购属性
    public static final String PURCHASE_PROPERTY = "purchaseProperty";

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
    @Autowired
    private ICategoryBiz categoryBiz;
    @Autowired
    private IPropertyService propertyService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IPurchaseDetailService iPurchaseDetailService;
    @Autowired
    private IPropertyValueService propertyValueService;
    @Autowired
    private ICategoryPropertyService categoryPropertyService;


    @Override
    public Pagenation<Items> itemsPage(ItemsForm queryModel, Pagenation<Items> page) throws Exception {
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getName())) {//商品名称
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        handlerSkuCondition(criteria, queryModel.getSkuCode(), queryModel.getSpuCode());
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
        example.orderBy("updateTime").desc();
        page = itemsService.pagination(example, page, queryModel);
        handerPage(page);
        //分页查询
        return page;
    }

    private void handlerSkuCondition(Example.Criteria criteria, String skuCode, String spuCode){
        List<String> spuCodes = new ArrayList<String>();
        if(StringUtils.isNotBlank(spuCode)){
            Example example = new Example(Items.class);
            Example.Criteria criteria2 = example.createCriteria();
            criteria.andLike("spuCode", "%" + spuCode + "%");
            List<Items> itemsList = itemsService.selectByExample(example);
            for(Items items: itemsList){
                spuCodes.add(items.getSpuCode());
            }
        }
        if(StringUtils.isNotBlank(skuCode)){
            Example example = new Example(Skus.class);
            Example.Criteria criteria2 = example.createCriteria();
            criteria2.andLike("skuCode", "%" + skuCode + "%");
            List<Skus> skusList = skusService.selectByExample(example);
            if(skusList.size() > 0){
                for(Skus s : skusList){
                    spuCodes.add(s.getSpuCode());
                }
            }
        }
        if(spuCodes.size() > 0){
            criteria.andIn("spuCode", spuCodes);
        }
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
        //设置商品对应的Sku信息
        setSkus(page.getResult());
    }

    /**
     * 设置商品对应的sku
     * @param items
     */
    private void setSkus(List<Items> items){
        List<String> spuCodes = new ArrayList<String>();
        for(Items item : items){
            spuCodes.add(item.getSpuCode());
        }
        if(spuCodes.size() > 0){
            //查询商品对应的SKU
            Example example = new Example(Skus.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("spuCode", spuCodes);
            criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            example.orderBy("isValid").desc();
            example.orderBy("skuCode").asc();
            List<Skus> skusList = skusService.selectByExample(example);
            AssertUtil.notEmpty(skusList, String.format("批量查询商品SPU编码为[%s]的商品对应的SKU信息为空", CommonUtil.converCollectionToString(spuCodes)));
            List<String> skuCodes = new ArrayList<String>();
            for(Skus skus : skusList){
                skuCodes.add(skus.getSkuCode());
            }
            //查询所有sku对应的采购属性
            Example example2 = new Example(ItemSalesPropery.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("skuCode", skuCodes);
            criteria2.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            List<ItemSalesPropery> itemSalesProperies = itemSalesProperyService.selectByExample(example2);
            AssertUtil.notEmpty(skusList, String.format("批量查询商品SKU编码为[%s]的SKU对应的采购属性信息为空", CommonUtil.converCollectionToString(skuCodes)));
            //查询所有采购属性详细信息
            List<Long> propertyIds = new ArrayList<Long>();
            for(ItemSalesPropery itemSalesPropery : itemSalesProperies){
                propertyIds.add(itemSalesPropery.getPropertyId());
            }
            Example example3 = new Example(Property.class);
            Example.Criteria criteria3 = example3.createCriteria();
            criteria3.andIn("id", propertyIds);
            List<Property> propertyList = propertyService.selectByExample(example3);
            AssertUtil.notEmpty(skusList, String.format("批量查询属性ID为[%s]的属性对应的信息为空", CommonUtil.converCollectionToString(propertyIds)));
            //设置SKU的采购属性组合名称
            for(Skus skus : skusList){
                skus.setPropertyCombineName(getPropertyCombineName(skus, itemSalesProperies, propertyList));
            }
            //设置商品SKU
            for(Items item : items){
                List<Skus> _tmpSkus = new ArrayList<Skus>();
                for(Skus skus : skusList){
                    if(StringUtils.equals(item.getSpuCode(), skus.getSpuCode())){
                        _tmpSkus.add(skus);
                    }
                }
                item.setRecords(_tmpSkus);
            }
        }
    }

    /**
     * 获取SKU属性组合名称
     * @param skus
     * @param itemSalesProperies
     * @param properties
     * @return
     */
    private String getPropertyCombineName(Skus skus, List<ItemSalesPropery> itemSalesProperies, List<Property> properties){
        String[] propertyValueIdArray = skus.getPropertyValueId().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
        String[] propertyValueArray = skus.getPropertyValue().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<propertyValueIdArray.length; i++){
            Long propertyValueId = Long.parseLong(propertyValueIdArray[i]);
            for(ItemSalesPropery itemSalesPropery : itemSalesProperies){
                if(propertyValueId == itemSalesPropery.getPropertyValueId()){
                    for(Property property : properties){
                        if(itemSalesPropery.getPropertyId().equals(property.getId())){
                        sb.append(SKU_PROPERTY_COMBINE_NAME_EMPTY).append(property.getName()).append(SKU_PROPERTY_COMBINE_NAME_SPLIT_SYMBOL).append(propertyValueArray[i]);
                        break;
                      }
                    }
                    break;
                }
            }
        }
        return sb.toString();
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
                if(c.getId() == c2.getId()){
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
                if(items2.getBrandId().longValue() == c.getId().longValue()){
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
        AssertUtil.notBlank(itemSalesPropery.getSalesPropertys(), "提交商品信息采购属性不能为空");
        AssertUtil.notBlank(skus.getSkusInfo(), "提交商品信息SKU信息不能为空");
        checkSkuInfo(skus, ZeroToNineEnum.ZERO.getCode());//检查sku参数
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
        if(StringUtils.isNotBlank(itemNaturePropery.getNaturePropertys())){
            itemNaturePropery.setItemId(items.getId());
            itemNaturePropery.setSpuCode(items.getSpuCode());
            saveItemNatureProperty(itemNaturePropery);
        }
        //保存采购属性信息
        saveItemSalesPropery(itemSalesPropery, skuss);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery) throws Exception {
        AssertUtil.notBlank(items.getSpuCode(), "提交商品信息自然属性不能为空");
        AssertUtil.notBlank(itemSalesPropery.getSalesPropertys(), "提交商品信息采购属性不能为空");
        AssertUtil.notBlank(skus.getSkusInfo(), "提交商品信息SKU信息不能为空");
        checkSkuInfo(skus, ZeroToNineEnum.ONE.getCode());//检查sku参数
        //保存商品基础信息
        updateItemsBase(items);
        //保存sku信息
        skus.setItemId(items.getId());
        skus.setSpuCode(items.getSpuCode());
        List<Skus> skuss = updateSkus(skus);
        //保存自然属性信息
        if(StringUtils.isNotBlank(itemNaturePropery.getNaturePropertys())){
            itemNaturePropery.setItemId(items.getId());
            itemNaturePropery.setSpuCode(items.getSpuCode());
            updateItemNatureProperty(itemNaturePropery);
        }
        //保存采购属性信息
        updateItemSalesPropery(itemSalesPropery, skuss);
    }

    /**
     *
     * @param skus
     * @param  flag 0-新增,1-修改
     */
    private void checkSkuInfo(Skus skus,String flag){
        JSONArray skuArray = JSONArray.parseArray(skus.getSkusInfo());
        if(skuArray.size() == 0){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "提交商品信息SKU信息不能为空");
        }
        for(Object obj : skuArray){
            JSONObject jbo = (JSONObject) obj;
            AssertUtil.notNull(jbo.getBigDecimal("weight2"),"SKU重量不能为空");
            AssertUtil.notBlank(jbo.getString("barCode"),"SKU条形码不能为空");
            if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), jbo.getString("source"))){
                AssertUtil.notBlank(jbo.getString("skuCode"),"SKU编码不能为空");
            }
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
        Date sysTime = Calendar.getInstance().getTime();
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
            skus2.setWeight(getLongValue(jbo.getString("weight2")));
            skus2.setMarketPrice(getLongValue(jbo.getString("marketPrice2")));
            skus2.setPicture(jbo.getString("picture"));
            skus2.setIsValid(jbo.getString("isValid"));
            skus2.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
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
        checkItemNatureProperty(categoryArray);
        if(categoryArray.size() > 0){
            List<ItemNaturePropery> itemNatureProperies = new ArrayList<ItemNaturePropery>();
            Date sysTime = Calendar.getInstance().getTime();
            for(Object obj : categoryArray){
                JSONObject jbo = (JSONObject) obj;
                ItemNaturePropery _itemNaturePropery = new ItemNaturePropery();
                _itemNaturePropery.setItemId(itemNaturePropery.getItemId());
                _itemNaturePropery.setSpuCode(itemNaturePropery.getSpuCode());
                _itemNaturePropery.setPropertyId(jbo.getLong("propertyId"));
                _itemNaturePropery.setPropertyValueId(jbo.getLong("propertyValueId"));
                _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
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
    }

    /**
     * 自然属性校验
     * @param itemNaturePropertyArray
     */
    private void checkItemNatureProperty(JSONArray itemNaturePropertyArray){
        for(Object obj : itemNaturePropertyArray) {
            JSONObject jbo = (JSONObject) obj;
            AssertUtil.notNull(jbo.getLong("propertyId"), "商品自然属性ID不能为空");
            AssertUtil.notNull(jbo.getLong("propertyValueId"), "商品自然属性值ID不能为空");
        }
    }

    /**
     * 保存商品采购属性
     * @param skuses
     */
    private void saveItemSalesPropery(ItemSalesPropery itemSalesPropery, List<Skus> skuses){
        JSONArray itemSalesArray = JSONArray.parseArray(itemSalesPropery.getSalesPropertys());
        AssertUtil.notEmpty(itemSalesArray, "保存商品采购属性不能为空");
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
                String[] _tmp = getPropertyIdAndPicture(itemSalesArray, propertyValueId);
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
     * @param itemSalesArray
     * @param propertyValueId
     * @return
     */
    private String[] getPropertyIdAndPicture(JSONArray itemSalesArray, Long propertyValueId){
        String[] _result = new String[2];
        for(Object obj : itemSalesArray){
            JSONObject jbo = (JSONObject) obj;
            if(propertyValueId.longValue() == jbo.getLong("propertyValueId").longValue()){
                _result[0] = jbo.getString("propertyId");
                _result[1] = jbo.getString("picture");
                break;
            }
        }
        return _result;
    }

    /**
     * 修改商品基础信息
     * @param items
     * @throws Exception
     */
    private void updateItemsBase(Items items) throws Exception{
        AssertUtil.notNull(items.getId(), "商品ID不能为空");
        items.setUpdateTime(Calendar.getInstance().getTime());
        int count = itemsService.updateByPrimaryKeySelective(items);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改商品基础信息",JSON.toJSONString(items),"数据库操作失败").toString();
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
    }

    private Long getLongValue(String val){
        if(StringUtils.isNotBlank(val)){
            Double d = Double.parseDouble(val);
            d = d*MONEY_MULTI;
            return d.longValue();
        }
        return null;
    }

    private List<Skus> updateSkus(Skus skus) throws Exception{
        AssertUtil.notBlank(skus.getSpuCode(), "更新SKU信息商品SPU编码不能为空");
        JSONArray skuArray = JSONArray.parseArray(skus.getSkusInfo());
        List<Skus> addlist = new ArrayList<Skus>();
        List<Skus> updatelist = new ArrayList<Skus>();
        Date sysTime = Calendar.getInstance().getTime();
        for(Object obj : skuArray){
            JSONObject jbo = (JSONObject) obj;
            Skus skus2 = new Skus();
            skus2.setItemId(skus.getItemId());
            skus2.setSpuCode(skus.getSpuCode());
            skus2.setSkuCode(jbo.getString("skuCode"));
            skus2.setPropertyValueId(jbo.getString("propertyValueId"));
            skus2.setPropertyValue(jbo.getString("propertyValue"));
            skus2.setBarCode(jbo.getString("barCode"));
            skus2.setWeight(getLongValue(jbo.getString("weight2")));
            skus2.setMarketPrice(getLongValue(jbo.getString("marketPrice2")));
            skus2.setPicture(jbo.getString("picture"));
            skus2.setIsValid(jbo.getString("isValid"));
            skus2.setUpdateTime(sysTime);
            skus2.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), jbo.getString("source"))){//新增的数据
                String code = serialUtilService.generateCode(SupplyConstants.Serial.SKU_LENGTH, SupplyConstants.Serial.SKU_NAME,
                        SupplyConstants.Serial.SKU_INNER, DateUtils.dateToCompactString(sysTime));
                skus2.setSkuCode(code);
                addlist.add(skus2);
            }else{
                if(StringUtils.equals(ZeroToNineEnum.TWO.getCode(), jbo.getString("status"))){//已修改
                    updatelist.add(skus2);
                }
            }
        }
        int count = 0;
        if(updatelist.size() > 0){
            count = skusService.updateSkus(updatelist);
            if (count == 0) {
                String msg = CommonUtil.joinStr("更新商品SKU", JSON.toJSONString(updatelist), "到数据库失败").toString();
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
            }
        }
        if(addlist.size() > 0){
            count = skusService.insertList(addlist);
            if (count == 0) {
                String msg = CommonUtil.joinStr("保存商品SKU", JSON.toJSONString(addlist), "到数据库失败").toString();
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
            }
        }
        Skus _tmp = new Skus();
        _tmp.setSpuCode(skus.getSpuCode());
        //_tmp.setIsValid(ZeroToNineEnum.ONE.getCode());
        _tmp.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Skus> _skus = skusService.select(_tmp);
        AssertUtil.notEmpty(_skus, String.format("根据商品SPU编码[%s]查询相关SKU为空", skus.getSpuCode()));
        return _skus;
    }

    /**
     * 更新商品自然属性
     * @param itemNaturePropery
     */
    private void updateItemNatureProperty(ItemNaturePropery itemNaturePropery)throws Exception{
        JSONArray categoryArray = JSONArray.parseArray(itemNaturePropery.getNaturePropertys());
        checkItemNatureProperty(categoryArray);
        if(categoryArray.size() > 0){
            List<ItemNaturePropery> addList = new ArrayList<ItemNaturePropery>();
            List<ItemNaturePropery> updateList = new ArrayList<ItemNaturePropery>();
            List<Long> delList = new ArrayList<Long>();
            ItemNaturePropery tmp = new ItemNaturePropery();
            tmp.setSpuCode(itemNaturePropery.getSpuCode());
            tmp.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            List<ItemNaturePropery> itemNatureProperyList = itemNatureProperyService.select(tmp);
            Date sysTime = Calendar.getInstance().getTime();
            List<ItemNaturePropery> list = new ArrayList<ItemNaturePropery>();
            for(Object obj : categoryArray){
                JSONObject jbo = (JSONObject) obj;
                ItemNaturePropery _itemNaturePropery = new ItemNaturePropery();
                _itemNaturePropery.setItemId(itemNaturePropery.getItemId());
                _itemNaturePropery.setSpuCode(itemNaturePropery.getSpuCode());
                _itemNaturePropery.setPropertyId(jbo.getLong("propertyId"));
                _itemNaturePropery.setPropertyValueId(jbo.getLong("propertyValueId"));
                _itemNaturePropery.setUpdateTime(sysTime);
                _itemNaturePropery.setIsValid(jbo.getString("isValid"));
                _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), _itemNaturePropery.getIsValid())){
                    delList.add(_itemNaturePropery.getPropertyValueId());
                }
                list.add(_itemNaturePropery);
                Boolean flag = false;
                for(ItemNaturePropery it : itemNatureProperyList){
                    if(_itemNaturePropery.getPropertyId().longValue() == it.getPropertyId().longValue()){
                        updateList.add(_itemNaturePropery);
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                    _itemNaturePropery.setCreateTime(sysTime);
                    addList.add(_itemNaturePropery);
                }
            }
            for(ItemNaturePropery it : itemNatureProperyList){
                Boolean flag = false;
                for(ItemNaturePropery it2 : list){
                    if(it.getPropertyId().longValue() == it2.getPropertyId().longValue()){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    delList.add(it.getPropertyValueId());
                }
            }
            int count = 0;
            if(addList.size() > 0){
                count = itemNatureProperyService.insertList(addList);
                if (count == 0) {
                    String msg = String.format("保存商品自然属性信息%s到数据库失败", JSON.toJSONString(addList));
                    log.error(msg);
                    throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
                }
            }
            if(updateList.size() > 0){
                count = itemNatureProperyService.updateItemNaturePropery(updateList);
                if (count == 0) {
                    String msg = String.format("更细商品自然属性信息%s到数据库失败", JSON.toJSONString(updateList));
                    log.error(msg);
                    throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
                }
            }
            if(delList.size() > 0){
                Example example = new Example(ItemNaturePropery.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("spuCode", itemNaturePropery.getSpuCode());
                criteria.andIn("propertyValueId", delList);
                count = itemNatureProperyService.deleteByExample(example);
                if (count == 0) {
                    String msg = String.format("根据商品SPU编码[%s]和属性值ID[%s]删除自然属性信息", itemNaturePropery.getSpuCode(), JSON.toJSONString(delList));
                    log.error(msg);
                    throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
                }
            }
        }
    }

    /**
     * 更新商品采购属性
     * @param itemSalesPropery
     * @param skuses
     * @throws Exception
     */
    private void updateItemSalesPropery(ItemSalesPropery itemSalesPropery, List<Skus> skuses) throws Exception{
        JSONArray itemSalesArray = JSONArray.parseArray(itemSalesPropery.getSalesPropertys());
        AssertUtil.notEmpty(itemSalesArray, "保存商品采购属性不能为空");
        List<ItemSalesPropery> addList = new ArrayList<ItemSalesPropery>();
        List<ItemSalesPropery> updateList = new ArrayList<ItemSalesPropery>();
        Date sysTime = Calendar.getInstance().getTime();
        List<ItemSalesPropery> list = new ArrayList<ItemSalesPropery>();
        for(Skus skus : skuses){
            String[] propertyValueIdsArray = skus.getPropertyValueId().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            String[] propertyValuesArray = skus.getPropertyValue().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            List<ItemSalesPropery> _currentList = querySkuItemSalesProperys(skus.getSpuCode(), skus.getSkuCode(), null);
            //AssertUtil.notEmpty(_currentList, String.format("根据商品SPU编码[%s]和SKU编码[%s]查询相关采购属性为空", skus.getSpuCode(), skus.getSkuCode()));
            for(int i=0; i<propertyValueIdsArray.length; i++){
                Long propertyValueId = Long.parseLong(propertyValueIdsArray[i]);
                ItemSalesPropery _itemSalesPropery = new ItemSalesPropery();
                _itemSalesPropery.setItemId(skus.getItemId());
                _itemSalesPropery.setSpuCode(skus.getSpuCode());
                _itemSalesPropery.setSkuCode(skus.getSkuCode());
                String[] _tmp = getPropertyIdAndPicture(itemSalesArray, propertyValueId);
                _itemSalesPropery.setPropertyId(Long.parseLong(_tmp[0]));
                _itemSalesPropery.setPropertyValueId(propertyValueId);
                _itemSalesPropery.setPropertyActualValue(propertyValuesArray[i]);
                _itemSalesPropery.setPicture(_tmp[1]);
                _itemSalesPropery.setUpdateTime(sysTime);
                _itemSalesPropery.setIsValid(ZeroToNineEnum.ONE.getCode());
                _itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                list.add(_itemSalesPropery);
                Boolean flag = false;
                for(ItemSalesPropery it : _currentList){
                    if(_itemSalesPropery.getPropertyId().longValue() == it.getPropertyId().longValue() &&
                            _itemSalesPropery.getPropertyValueId().longValue() == it.getPropertyValueId().longValue()){
                        updateList.add(_itemSalesPropery);
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    _itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                    _itemSalesPropery.setCreateTime(sysTime);
                    addList.add(_itemSalesPropery);
                }
            }
            for(ItemSalesPropery it : _currentList){
                Boolean flag = false;
                for(ItemSalesPropery it2 : list){
                    if(it.getPropertyId().longValue() == it2.getPropertyId().longValue() &&
                            it.getPropertyValueId().longValue() == it2.getPropertyValueId().longValue()){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    it.setUpdateTime(sysTime);
                    it.setIsDeleted(ZeroToNineEnum.ONE.getCode());
                    updateList.add(it);
                }
            }
        }
        int count = 0;
        if(updateList.size() > 0){
            count = itemSalesProperyService.updateItemSalesPropery(updateList);
            if (count == 0) {
                String msg = String.format("更细商品采购属性信息%s到数据库失败", JSON.toJSONString(updateList));
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
            }
        }
        if(addList.size() > 0){
            count = itemSalesProperyService.insertList(addList);
            if (count == 0) {
                String msg = String.format("保存商品采购属性信息%s到数据库失败", JSON.toJSONString(addList));
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 查询sku对应的采购属性
     * @param spuCode
     * @param skuCode
     * @param isDelete
     * @return
     * @throws Exception
     */
    private List<ItemSalesPropery> querySkuItemSalesProperys(String spuCode, String skuCode, String isDelete) throws Exception{
        ItemSalesPropery itemSalesPropery = new ItemSalesPropery();
        itemSalesPropery.setSpuCode(spuCode);
        itemSalesPropery.setSkuCode(skuCode);
        if(StringUtils.isNotBlank(isDelete)){
            itemSalesPropery.setIsDeleted(isDelete);
        }
        return itemSalesProperyService.select(itemSalesPropery);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateValid(Long id, String isValid) throws Exception {
        AssertUtil.notNull(id, "商品启用/停用操作参数id不能为空");
        AssertUtil.notBlank(isValid, "商品启用/停用操作参数isValid不能为空");
        Items items = new Items();
        items.setId(id);
        items.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        String _isValid = ZeroToNineEnum.ZERO.getCode();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            _isValid = ZeroToNineEnum.ONE.getCode();
        }
        items.setIsValid(_isValid);
        int count = itemsService.updateByPrimaryKeySelective(items);
        if(count == 0){
            String msg = "商品启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
        Items items2 = new Items();
        items2.setId(id);
        items2 = itemsService.selectOne(items2);
        AssertUtil.notNull(items2, String.format("根据主键ID[%s]查询商品基础信息为空", id.toString()));
        //更新商品相关SKU启用/停用状态
        updateGoodsSkusValid(items2.getSpuCode(),_isValid);
        //更新SKU库存启停用状态
        updateSkuStockIsValid(items2.getSpuCode(), null, _isValid);
        //更新采购单明细启停用状态
        updatePurchaseDetailIsValid(items2.getSpuCode(), null, _isValid);
    }

    private void updateGoodsSkusValid(String spuCode, String isValid) throws Exception{
        Skus skus = new Skus();
        skus.setSpuCode(spuCode);
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            skus.setIsValid(ZeroToNineEnum.ONE.getCode());
        }else {
            skus.setIsValid(ZeroToNineEnum.ZERO.getCode());
        }
        List<Skus> skusList = skusService.select(skus);
        AssertUtil.notEmpty(skusList, String.format("根据商品SPU编码[%s]查询相关SKU信息为空", spuCode));
        Date sysTime = Calendar.getInstance().getTime();
        for(Skus sku: skusList){
            sku.setIsValid(isValid);
            sku.setUpdateTime(sysTime);
        }
        int count = skusService.updateSkus(skusList);
        if(count == 0){
            String msg = "商品SKU启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSkusValid(Long id, String spuCode, String isValid) throws Exception {
        AssertUtil.notNull(id, "SKU启用/停用操作参数ID不能为空");
        AssertUtil.notBlank(spuCode, "SKU启用/停用操作参数spuCode不能为空");
        AssertUtil.notBlank(isValid, "SKU启用/停用操作参数isValid不能为空");
        Skus skus = new Skus();
        skus.setId(id);
        String _isValid = ZeroToNineEnum.ZERO.getCode();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            _isValid = ZeroToNineEnum.ONE.getCode();
        }
        skus.setIsValid(_isValid);
        int count = skusService.updateByPrimaryKeySelective(skus);
        if(count == 0){
            String msg = "商品SKU启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
        //更新商品启停用状态
        updateItemsValid(spuCode, _isValid);
        Skus skus2 = new Skus();
        skus2.setId(id);
        skus2 = skusService.selectOne(skus2);
        AssertUtil.notNull(skus2, String.format("根据商品sku的ID[%s]查询SKU信息为空", id));
        //更新SKU库存启停用状态
        updateSkuStockIsValid(spuCode, skus2.getSkuCode(), _isValid);
        //更新采购单明细启停用状态
        updatePurchaseDetailIsValid(spuCode, skus2.getSkuCode(), _isValid);
    }

    /**
     * 更新商品启用/停用状态
     * @param spuCode
     * @param isValid
     * @throws Exception
     */
    private void updateItemsValid(String spuCode, String isValid) throws Exception{
        Items items = new Items();
        items.setSpuCode(spuCode);
        items = itemsService.selectOne(items);
        AssertUtil.notNull(items, String.format("根据商品SPU编码[%s]查询商品基础信息为空", spuCode));
        Boolean flag = false;
        if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), isValid)){//启用SKU
            if(!StringUtils.equals(isValid, items.getIsValid())){
                items.setIsValid(isValid);
                flag = true;
            }
        }else{//停用SKU
            //查询商品spu对应的所有启用的sku
            Skus skus = new Skus();
            skus.setSpuCode(spuCode);
            skus.setIsValid(ZeroToNineEnum.ONE.getCode());
            List<Skus> list = skusService.select(skus);
            if(list.size() == 0){
                items.setIsValid(ZeroToNineEnum.ZERO.getCode());
                flag = true;
            }
        }
        if(flag){
            items.setUpdateTime(Calendar.getInstance().getTime());
            int count = itemsService.updateByPrimaryKeySelective(items);
            if(count == 0){
                String msg = String.format("更新商品基础信息%s到数据库失败", JSON.toJSONString(items));
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 跟新SKU库存启停用状态
     * @param spuCode
     * @param skuCode
     * @param isValid
     * @throws Exception
     */
    private void updateSkuStockIsValid(String spuCode, String skuCode, String isValid) throws Exception{
        Example example = new Example(SkuStock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuCode", spuCode);
        if(StringUtils.isNotBlank(skuCode)){//商品SKU启停用
            criteria.andEqualTo("skuCode", skuCode);
        }
        SkuStock skuStock = new SkuStock();
        skuStock.setIsValid(isValid);
        skuStockService.updateByExampleSelective(skuStock, example);
    }

    /**
     * 跟新采购单明细启停用状态
     * @param spuCode
     * @param skuCode
     * @param isValid
     * @throws Exception
     */
    private void updatePurchaseDetailIsValid(String spuCode, String skuCode, String isValid) throws Exception{
        Example example = new Example(PurchaseDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuCode", spuCode);
        if(StringUtils.isNotBlank(skuCode)){//商品SKU启停用
            criteria.andEqualTo("skuCode", skuCode);
        }
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setIsValid(isValid);
        iPurchaseDetailService.updateByExampleSelective(purchaseDetail, example);
    }



    @Override
    public ItemsExt queryItemsInfo(String spuCode) throws Exception {
        AssertUtil.notBlank(spuCode, "查询商品详情参数商品SPU编码supCode不能为空");
        //查询商品基础信息
        Items items = new Items();
        items.setSpuCode(spuCode);
        items.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        items = itemsService.selectOne(items);
        AssertUtil.notNull(items, String.format("根据商品SPU编码[%s]查询商品基础信息为空", spuCode));
        String categoryName = categoryBiz.getCategoryName(items.getCategoryId());
        items.setCategoryName(categoryName);
        //查询商品SKU信息
        Skus skus = new Skus();
        skus.setSpuCode(spuCode);
        skus.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Skus> skuses = skusService.select(skus);
        //设置商品重量和市场价返回值
        for(Skus s : skuses){
            if(null != s.getWeight() && s.getWeight() > 0){
                s.setWeight2(BigDecimal.valueOf(Math.round(s.getWeight())/MONEY_MULTI));
            }
            if(null != s.getMarketPrice() && s.getMarketPrice() > 0){
                s.setMarketPrice2(BigDecimal.valueOf(Math.round(s.getMarketPrice())/MONEY_MULTI));
            }
        }
        AssertUtil.notEmpty(skuses, String.format("根据商品SPU编码[%s]查询商品SKU信息为空", spuCode));
        //获取自然属性和采购属性
        Object[] objs = getItemsPropertys(spuCode);
        List<ItemNaturePropery> itemNatureProperies = (List<ItemNaturePropery>)objs[0];
        List<ItemSalesPropery> itemSalesProperies = (List<ItemSalesPropery>)objs[1];
        //返回数据组装
        ItemsExt itemsExt = new ItemsExt();
        itemsExt.setItems(items);
        itemsExt.setSkus(skuses);
        itemsExt.setItemNatureProperys(itemNatureProperies);
        itemsExt.setItemSalesProperies(itemSalesProperies);
        return itemsExt;
    }

    private Object[] getItemsPropertys(String spuCode) throws Exception{
        //查询商品自然属性信息
        ItemNaturePropery itemNaturePropery = new ItemNaturePropery();
        itemNaturePropery.setSpuCode(spuCode);
        itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<ItemNaturePropery> itemNatureProperies = itemNatureProperyService.select(itemNaturePropery);
        //查询商品采购属性信息
        ItemSalesPropery itemSalesPropery = new ItemSalesPropery();
        itemSalesPropery.setSpuCode(spuCode);
        itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<ItemSalesPropery> itemSalesProperies = itemSalesProperyService.select(itemSalesPropery);
        AssertUtil.notEmpty(itemSalesProperies, String.format("根据商品SPU编码[%s]查询商品采购属性信息为空", spuCode));
        //设置自然属性和采购属性中文名词
        setItemNatureProperyValue(itemNatureProperies, itemSalesProperies);
        Object[] objs = new Object[2];
        objs[0] = itemNatureProperies;
        objs[1] = itemSalesProperies;
        return objs;
    }

    @Override
    public List<CategoryProperty> queryItemsCategoryProperty(String spuCode, Long categoryId) throws Exception {
        AssertUtil.notBlank(spuCode, "查询商品分类属性spuCode为空");
        AssertUtil.notNull(categoryId, "查询商品分类属性categoryId为空");
        Example example = new Example(CategoryProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        example.orderBy("propertySort").asc();
        List<CategoryProperty> categoryProperties = categoryPropertyService.selectByExample(example);
        AssertUtil.notEmpty(categoryProperties, String.format("根据分类ID[%s]查询分类属性为空", categoryId));
        //设置分类属性名称
        setCategoryPropertyName(categoryProperties);
        //获取自然属性和采购属性
        Object[] objs = getItemsPropertys(spuCode);
        List<ItemNaturePropery> itemNatureProperies = (List<ItemNaturePropery>)objs[0];
        List<ItemSalesPropery> itemSalesProperies = (List<ItemSalesPropery>)objs[1];
        //已禁用的自然属性
        List<ItemNaturePropery> deletedItemNatures = new ArrayList<ItemNaturePropery>();
        //已禁用的采购属性
        List<ItemSalesPropery> deletedItemSales = new ArrayList<ItemSalesPropery>();
        //获取已经删除的属性
        getDeletedPropertys(itemNatureProperies, itemSalesProperies, deletedItemNatures, deletedItemSales, categoryProperties);
        //将已经禁用的属性加入到返回的分类属性列表里面
        handlerCategoryPropertys(deletedItemNatures, deletedItemSales, categoryProperties, categoryId);
        return categoryProperties;
    }

    private void setCategoryPropertyName(List<CategoryProperty> categoryProperties) throws Exception{
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
    }

    /**
     * 获取已经禁用的自然属性和采购属性
     * @param itemNatureProperies
     * @param itemSalesProperies
     * @param deletedItemNatures
     * @param deletedItemSales
     * @param categoryProperties
     */
    private void getDeletedPropertys(List<ItemNaturePropery> itemNatureProperies, List<ItemSalesPropery> itemSalesProperies,
           List<ItemNaturePropery> deletedItemNatures, List<ItemSalesPropery> deletedItemSales, List<CategoryProperty> categoryProperties){
        for(ItemNaturePropery itemNaturePropery: itemNatureProperies){
            Boolean flag = false;
            for(CategoryProperty categoryProperty: categoryProperties){
                if(itemNaturePropery.getPropertyId().longValue() == categoryProperty.getPropertyId().longValue()){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                deletedItemNatures.add(itemNaturePropery);
            }
        }
        for(ItemSalesPropery itemSalesPropery: itemSalesProperies){
            Boolean flag = false;
            for(CategoryProperty categoryProperty: categoryProperties){
                if(itemSalesPropery.getPropertyId().longValue() == categoryProperty.getPropertyId().longValue()){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                deletedItemSales.add(itemSalesPropery);
            }
        }
    }

    /**
     * 将已经禁用的属性加入到返回的分类属性列表里面
     * @param deletedItemNatures
     * @param deletedItemSales
     * @param categoryProperties
     */
    private void handlerCategoryPropertys(List<ItemNaturePropery> deletedItemNatures, List<ItemSalesPropery>
            deletedItemSales, List<CategoryProperty> categoryProperties, Long categoryId){
        for(ItemNaturePropery itemNaturePropery: deletedItemNatures){
            CategoryProperty categoryProperty = new CategoryProperty();
            categoryProperty.setCategoryId(categoryId);
            categoryProperty.setPropertyId(itemNaturePropery.getPropertyId());
            categoryProperty.setName(itemNaturePropery.getPropertyName());
            categoryProperty.setTypeCode(NATURE_PROPERTY);//自然属性
            categoryProperty.setValueType(ZeroToNineEnum.ZERO.getCode());//文字类型
            categoryProperty.setIsValid(ZeroToNineEnum.ZERO.getCode());//禁用
            if(!hasCategoryProperty(categoryProperties, categoryProperty)){
                categoryProperties.add(categoryProperty);
            }
        }
        for(ItemSalesPropery itemSalesPropery: deletedItemSales){
            CategoryProperty categoryProperty = new CategoryProperty();
            categoryProperty.setCategoryId(categoryId);
            categoryProperty.setPropertyId(itemSalesPropery.getPropertyId());
            categoryProperty.setName(itemSalesPropery.getPropertyName());
            categoryProperty.setTypeCode(PURCHASE_PROPERTY);//采购属性
            categoryProperty.setValueType(ZeroToNineEnum.ZERO.getCode());//文字类型
            if(StringUtils.isNotBlank(itemSalesPropery.getPicture())){
                categoryProperty.setValueType(ZeroToNineEnum.ONE.getCode());//图片类型
            }
            categoryProperty.setIsValid(ZeroToNineEnum.ZERO.getCode());//禁用
            if(!hasCategoryProperty(categoryProperties, categoryProperty)){
                categoryProperties.add(categoryProperty);
            }
        }
    }

    private Boolean hasCategoryProperty(List<CategoryProperty> categoryProperties, CategoryProperty categoryProperty){
        for(CategoryProperty categoryProperty2 : categoryProperties){
            if(categoryProperty2.getCategoryId().longValue() == categoryProperty.getCategoryId().longValue() &&
                    categoryProperty2.getPropertyId().longValue() == categoryProperty.getPropertyId().longValue()){
                return true;
            }
        }
        return false;
    }



    /**
     * 设置自然属性值
     * @param itemNatureProperies
     * @throws Exception
     */
    private void setItemNatureProperyValue(List<ItemNaturePropery> itemNatureProperies, List<ItemSalesPropery> itemSalesProperies) throws Exception{
        Set<Long> propertyIds = new HashSet<Long>();
        Set<Long> propertyValueIds = new HashSet<Long>();
        for(ItemNaturePropery itemNaturePropery: itemNatureProperies){
            propertyIds.add(itemNaturePropery.getPropertyId());
            propertyValueIds.add(itemNaturePropery.getPropertyValueId());
        }
        for(ItemSalesPropery itemSalesPropery: itemSalesProperies){
            propertyIds.add(itemSalesPropery.getPropertyId());
            propertyValueIds.add(itemSalesPropery.getPropertyValueId());
        }
        Example example = new Example(Property.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", propertyIds);
        List<Property> propertyList = propertyService.selectByExample(example);
        AssertUtil.notEmpty(propertyList, String.format("根据属性ID[%s]查询属性信息为空",
                CommonUtil.converCollectionToString(Arrays.asList(propertyIds.toArray())).toString()));
        Example example2 = new Example(PropertyValue.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("id", propertyValueIds);
        List<PropertyValue> propertyValueList = propertyValueService.selectByExample(example2);
        AssertUtil.notEmpty(propertyValueList, String.format("根据属性值ID[%s]查询属性值信息为空",
                CommonUtil.converCollectionToString(Arrays.asList(propertyValueIds.toArray()))).toString());
        for(ItemNaturePropery itemNaturePropery: itemNatureProperies){
            for(Property property: propertyList){
                if(itemNaturePropery.getPropertyId().longValue() == property.getId().longValue()){
                    itemNaturePropery.setPropertyName(property.getName());
                }
            }
            for(PropertyValue propertyValue: propertyValueList){
                if(itemNaturePropery.getPropertyValueId().longValue() == propertyValue.getId().longValue()){
                    itemNaturePropery.setPropertyValue(propertyValue.getValue());
                }
            }
        }
        for(ItemSalesPropery itemSalesPropery: itemSalesProperies){
            for(Property property: propertyList){
                if(itemSalesPropery.getPropertyId().longValue() == property.getId().longValue()){
                    itemSalesPropery.setPropertyName(property.getName());
                }
            }
        }
    }


}
