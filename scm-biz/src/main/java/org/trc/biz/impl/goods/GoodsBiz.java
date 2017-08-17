package org.trc.biz.impl.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.config.IConfigBiz;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.category.*;
import org.trc.domain.dict.Dict;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.enums.*;
import org.trc.exception.GoodsException;
import org.trc.exception.ParamValidException;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.JDModel.SkuDO;
import org.trc.form.JDModel.SupplyItemsForm;
import org.trc.form.SupplyItemsExt;
import org.trc.form.config.DictForm;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsExt;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IJDService;
import org.trc.service.category.*;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemNatureProperyService;
import org.trc.service.impl.goods.ItemSalesProperyService;
import org.trc.service.impl.system.WarehouseService;
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
    //换行符号
    public static final String SWITCH_LINE = "<br>";
    //SKU的属性组合名称空格
    public static final String SKU_PROPERTY_COMBINE_NAME_EMPTY = "&nbsp&nbsp&nbsp";
    //自然属性
    public static final String NATURE_PROPERTY = "natureProperty";
    //采购属性
    public static final String PURCHASE_PROPERTY = "purchaseProperty";
    //供应商京东编码
    public static final String JD_SUPPLIER_CODE = "JD";
    //供应商粮油编码
    public static final String LY_SUPPLIER_CODE = "LY";

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
    @Autowired
    private ICategoryBrandService categoryBrandService;
    @Autowired
    private IExternalItemSkuService externalItemSkuService;
    @Autowired
    private IConfigBiz configBiz;
    @Autowired
    private IJDService jdService;
    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private ITrcBiz trcBiz;
    @Autowired
    private ILogInfoService logInfoService;


    @Override
    //@Cacheable(key="#queryModel.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<Items> itemsPage(ItemsForm queryModel, Pagenation<Items> page) throws Exception {
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getName())) {//商品名称
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        List<String> spuCodes = handlerSkuCondition(queryModel.getSkuCode(), queryModel.getSpuCode());
        if(null != spuCodes){//根据SPU或者SKU来查询
            if(spuCodes.size() == 0){
                return page;
            }else{
                criteria.andIn("spuCode", spuCodes);
            }
        }
        if (null != queryModel.getCategoryId()) {//商品所属分类ID
            criteria.andEqualTo("categoryId", queryModel.getCategoryId());
        }
        if (null != queryModel.getBrandId()) {//商品所属品牌ID
            criteria.andEqualTo("brandId", queryModel.getBrandId());
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

    @Override
    @Cacheable(key="#queryModel.toString()+#aclUserAccreditInfo.channelCode+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<Skus> itemsSkusPage(SkusForm queryModel, Pagenation<Skus> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getSpuCode())) {//spuCode
            criteria.andLike("spuCode", "%" + queryModel.getSpuCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSkuCode())) {//skuCode
            criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        Set<String> spus = getSkusQueryConditonRelateSpus(queryModel);
        if(null != spus){
            if(spus.size() > 0){
                criteria.andIn("spuCode", spus);
            }else{
                return page;
            }
        }
        example.orderBy("updateTime").desc();
        page = skusService.pagination(example, page, queryModel);
        if(page.getResult().size() > 0){
            handerSkusPage(page, aclUserAccreditInfo.getChannelCode());
        }
        //分页查询
        return page;
    }

    /**
     *
     * @param page
     * @param channelCode 渠道编码
     * @throws Exception
     */
    private void handerSkusPage(Pagenation<Skus> page, String channelCode) throws Exception {
        Set<String> spuCodeList = new HashSet<String>();
        List<String> skuCodeList = new ArrayList<String>();
        for(Skus skus: page.getResult()){
            spuCodeList.add(skus.getSpuCode());
            skuCodeList.add(skus.getSkuCode());
        }
        //查询相关商品
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("spuCode", spuCodeList);
        List<Items> itemsList = itemsService.selectByExample(example);
        AssertUtil.notEmpty(itemsList, String.format("根据多个SPU编码[%s]查询商品信息为空",
                CommonUtil.converCollectionToString(Arrays.asList(spuCodeList))));
        Set<Long> categoryIdList = new HashSet<Long>();
        Set<Long> brandIdList = new HashSet<Long>();
        for(Items items: itemsList){
            categoryIdList.add(items.getCategoryId());
            brandIdList.add(items.getBrandId());
        }
        //spu和分类名称map
        Map<String, String> spuCategoryMap = new HashMap<String, String>();
        for(Items items: itemsList){
            List<String> namePathList = null;
            try{
                namePathList = categoryBiz.queryCategoryNamePath(items.getCategoryId());
                AssertUtil.notEmpty(namePathList, String.format("根据分类ID[%s]查询分类名称信息为空",items.getCategoryId()));
            }catch (Exception e){
                log.error("查询分类名称异常", e);
            }
            if(null != namePathList){
                StringBuilder sb = new StringBuilder();
                for(int i=namePathList.size(); i>0; i--){
                    int j = i-1;
                    if(j == 0){
                        sb.append(namePathList.get(j));
                    }else{
                        sb.append(namePathList.get(j)).append(CATEGORY_NAME_SPLIT_SYMBOL);
                    }
                }
                spuCategoryMap.put(items.getSpuCode(), sb.toString());
            }
        }
        //查询相关品牌
        Example example3 = new Example(Brand.class);
        Example.Criteria criteria3 = example3.createCriteria();
        criteria3.andIn("id", brandIdList);
        List<Brand> brandList = brandService.selectByExample(example3);
        AssertUtil.notEmpty(brandList, String.format("根据多个品牌ID[%s]查询品牌信息为空",
                CommonUtil.converCollectionToString(Arrays.asList(brandIdList))));
        //spu和商品名称map
        Map<String, String> spuBrandMap = new HashMap<String, String>();
        for(Items items: itemsList){
            for(Brand brand: brandList){
                if(items.getBrandId().longValue() == brand.getId().longValue()){
                    spuBrandMap.put(items.getSpuCode(), brand.getName());
                    break;
                }
            }
        }
        //查询SKU相关库存信息
        Example example4 = new Example(SkuStock.class);
        Example.Criteria criteria4 = example4.createCriteria();
        criteria4.andIn("spuCode", skuCodeList);
        criteria4.andEqualTo("channelCode", channelCode);
        List<SkuStock> skuStockList = skuStockService.selectByExample(example4);
        /*AssertUtil.notEmpty(skuStockList, String.format("根据多个SKU编码[%s]查询SKU库存信息为空",
                CommonUtil.converCollectionToString(Arrays.asList(skuCodeList))));*/
        //设置分类名称、品牌名称、库存信息
        for(Skus skus: page.getResult()){
            skus.setCategoryName(spuCategoryMap.get(skus.getSpuCode()));
            skus.setBrandName(spuBrandMap.get(skus.getSpuCode()));
            for(Items items: itemsList){
                if(StringUtils.equals(skus.getSpuCode(), items.getSpuCode())){
                    skus.setItemsName(items.getName());
                }
            }
            for(SkuStock skuStock: skuStockList){
                if(StringUtils.equals(skus.getSkuCode(), skuStock.getSkuCode())){
                    skus.setAvailableInventory(skuStock.getAvailableInventory());
                    skus.setRealInventory(skuStock.getRealInventory());
                    skus.setDefectiveInventory(skuStock.getDefectiveInventory());
                }
            }
        }
    }

    /**
     * 获取SKU查询条件相关的SPU
     * @param queryModel
     * @return
     */
    private Set<String> getSkusQueryConditonRelateSpus(SkusForm queryModel){
        if(StringUtil.isNotEmpty(queryModel.getItemName()) || null != queryModel.getCategoryId() || null != queryModel.getBrandId()){
            Example example = new Example(Items.class);
            Example.Criteria criteria = example.createCriteria();
            if(StringUtil.isNotEmpty(queryModel.getItemName())) {//商品名称
                criteria.andLike("name", "%" + queryModel.getItemName() + "%");
            }
            if(null != queryModel.getCategoryId()){
                criteria.andEqualTo("categoryId", queryModel.getCategoryId());
            }
            if(null != queryModel.getBrandId()){
                criteria.andEqualTo("brandId", queryModel.getBrandId());
            }
            List<Items> items = itemsService.selectByExample(example);
            Set<String> spus = new HashSet<String>();
            for(Items item: items){
                spus.add(item.getSpuCode());
            }
            return spus;
        }else{
            return null;
        }
    }


    private List<String> handlerSkuCondition(String skuCode, String spuCode){
        List<String> spuCodes = null;
        if(StringUtils.isNotBlank(spuCode)){
            spuCodes = new ArrayList<String>();
            Example example = new Example(Items.class);
            Example.Criteria criteria2 = example.createCriteria();
            criteria2.andLike("spuCode", "%" + spuCode + "%");
            List<Items> itemsList = itemsService.selectByExample(example);
            for(Items items: itemsList){
                spuCodes.add(items.getSpuCode());
            }
        }
        if(StringUtils.isNotBlank(skuCode)){
            if(null == spuCodes)
                spuCodes = new ArrayList<String>();
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
        return spuCodes;
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
                if(propertyValueId.longValue() == itemSalesPropery.getPropertyValueId().longValue()){
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
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery) throws Exception {
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
        if(StringUtils.isNotBlank(itemNaturePropery.getNaturePropertys())){
            itemNaturePropery.setItemId(items.getId());
            itemNaturePropery.setSpuCode(items.getSpuCode());
            saveItemNatureProperty(itemNaturePropery, items.getCategoryId());
        }
        //保存采购属性信息
        saveItemSalesPropery(itemSalesPropery, skuss, items.getCategoryId());
        //记录操作日志
        logInfoService.recordLog(items,items.getId().toString(),items.getCreateOperator(),LogOperationEnum.ADD.getMessage(),null, null);
    }

    /**
     * 商品更新通知渠道
     * @param items
     */
    private void itemsUpdateNoticeChannel(Items items, TrcActionTypeEnum trcActionTypeEnum){
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try{
                        ItemNaturePropery itemNaturePropery = new ItemNaturePropery();
                        itemNaturePropery.setSpuCode(items.getSpuCode());
                        List<ItemNaturePropery> itemNatureProperies = itemNatureProperyService.select(itemNaturePropery);
                        AssertUtil.notEmpty(itemNatureProperies, String.format("根据商品SPU编码%s查询相关自然属性为空", items.getSpuCode()));
                        ItemSalesPropery itemSalesPropery = new ItemSalesPropery();
                        itemSalesPropery.setSpuCode(items.getSpuCode());
                        List<ItemSalesPropery> itemSalesProperies = itemSalesProperyService.select(itemSalesPropery);
                        AssertUtil.notEmpty(itemSalesProperies, String.format("根据商品SPU编码%s查询相关采购属性为空", items.getSpuCode()));
                        Skus skus = new Skus();
                        skus.setSpuCode(items.getSpuCode());
                        List<Skus> skusList = skusService.select(skus);
                        AssertUtil.notEmpty(itemSalesProperies, String.format("根据商品SPU编码%s查询相关SKU信息为空", items.getSpuCode()));
                        ToGlyResultDO toGlyResultDO = trcBiz.sendItem(trcActionTypeEnum, items, itemNatureProperies, itemSalesProperies, skusList, System.currentTimeMillis());
                        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
                            if(log.isInfoEnabled())
                                log.info(String.format("更新商品%s通知渠道成功", JSON.toJSONString(items)));
                        }else{
                            log.error(String.format("更新商品%s通知渠道失败", JSON.toJSONString(items)));
                        }
                    }catch (Exception e){
                        String msg = String.format("更新商品%s通知渠道异常,异常信息:%s", JSON.toJSONString(items), e.getMessage());
                        log.error(msg, e);
                    }
                }
            }
        ).start();
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateItems(Items items, Skus skus, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notBlank(items.getSpuCode(), "提交商品信息自然属性不能为空");
        AssertUtil.notBlank(itemSalesPropery.getSalesPropertys(), "提交商品信息采购属性不能为空");
        AssertUtil.notBlank(skus.getSkusInfo(), "提交商品信息SKU信息不能为空");
        String userId = aclUserAccreditInfo.getUserId();
        checkSkuInfo(skus);//检查sku参数
        //保存sku信息
        skus.setItemId(items.getId());
        skus.setSpuCode(items.getSpuCode());
        List<Skus> skuss = updateSkus(skus, userId);
        //根据sku启停用状态设置商品启停用状态
        boolean isValidUpdate = setItemsIsValidBySkuStatus(items);
        //保存商品基础信息
        updateItemsBase(items);
        //保存自然属性信息
        if(StringUtils.isNotBlank(itemNaturePropery.getNaturePropertys())){
            itemNaturePropery.setItemId(items.getId());
            itemNaturePropery.setSpuCode(items.getSpuCode());
            updateItemNatureProperty(itemNaturePropery, items.getCategoryId());
        }
        //保存采购属性信息
        updateItemSalesPropery(itemSalesPropery, skuss, items.getCategoryId());
        //商品编辑通知渠道
        itemsUpdateNoticeChannel(items, TrcActionTypeEnum.EDIT_ITEMS);
        //记录操作日志
        String remark = null;
        if(isValidUpdate)
            remark = String.format("SPU状态更新为%s", ValidEnum.getValidEnumByCode(items.getIsValid()).getName());
        logInfoService.recordLog(items,items.getId().toString(),userId ,LogOperationEnum.UPDATE.getMessage(),remark, null);
    }

    /**
     * 根据sku启停用状态设置商品启停用状态
     * @param items
     */
    private boolean setItemsIsValidBySkuStatus(Items items){
        String _isValid = items.getIsValid();
        Skus skus = new Skus();
        skus.setSpuCode(items.getSpuCode());
        List<Skus> skusList = skusService.select(skus);
        AssertUtil.notEmpty(skusList, String.format("根据spu编码查询sku信息为空", items.getSpuCode()));
        boolean flag = false;//商品启用
        for(Skus skus2: skusList){
            if(StringUtils.equals(ValidEnum.VALID.getCode(), skus2.getIsValid())){
                flag = true;
                break;
            }
        }
        if(flag){
            items.setIsValid(ValidEnum.VALID.getCode());
        }else {
            items.setIsValid(ValidEnum.NOVALID.getCode());
        }
        if(StringUtils.equals(_isValid, items.getIsValid()))
            return false;
        else
            return true;
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
        checkCategoryBrandValidStatus(items.getCategoryId(), items.getBrandId());
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
            skus2.setWeight(CommonUtil.getWeightLong(jbo.getString("weight2")));
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
     * @param  categoryId 分类ID
     */
    private void saveItemNatureProperty(ItemNaturePropery itemNaturePropery, Long categoryId)throws  Exception{
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
                Long propertyId = jbo.getLong("propertyId");
                _itemNaturePropery.setPropertyId(propertyId);
                Long propertyValueId = jbo.getLong("propertyValueId");
                _itemNaturePropery.setPropertyValueId(propertyValueId);
                _itemNaturePropery.setIsValid(ZeroToNineEnum.ONE.getCode());
                _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                _itemNaturePropery.setCreateTime(sysTime);
                _itemNaturePropery.setUpdateTime(sysTime);
                //判断属性是否停用
                checkCategoryPropertyValidStatus(categoryId, propertyId, propertyValueId);
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
     * 检查分类品牌启停用状态
     * @param categoryId
     * @param brandId
     * @throws Exception
     */
    private void checkCategoryBrandValidStatus(Long categoryId, Long brandId)throws Exception{
        Category category = categoryService.selectByPrimaryKey(categoryId);
        AssertUtil.notNull(category, String.format("根据主键ID[%s]查询分类信息为空", categoryId));
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), category.getIsValid())){
            throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("分类[%s]已被禁用", category.getName()));
        }
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(categoryId);
        categoryBrand.setBrandId(brandId);
        categoryBrand = categoryBrandService.selectOne(categoryBrand);
        AssertUtil.notNull(categoryBrand, String.format("根据分类ID[%s]和品牌ID[%s]查询分类品牌信息为空", categoryId, brandId));
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), categoryBrand.getIsValid())){
            Brand brand = brandService.selectByPrimaryKey(brandId);
            AssertUtil.notNull(brand, String.format("根据主键ID[%s]查询品牌信息为空", brandId));
            throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("分类[%s]关联品牌[%s]已被禁用", category.getName(), brand.getName()));
        }
    }

    /**
     *
     * @param categoryId
     * @param propertyId
     * @param propertyValueId
     * @throws Exception
     */
    private void checkCategoryPropertyValidStatus(Long categoryId, Long propertyId, Long propertyValueId)throws Exception{
        CategoryProperty categoryProperty = getCategoryProperty(categoryId, propertyId);
        if(null == categoryProperty || StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), categoryProperty.getIsValid())){
            PropertyValue propertyValue = propertyValueService.selectByPrimaryKey(propertyValueId);
            AssertUtil.notNull(propertyValue, String.format("根据属性值ID[%s]查询属性值信息为空", propertyValueId));
            throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("属性[%s]已被禁用", propertyValue.getValue()));
        }
    }

    /**
     * 根据分类ID和属性ID查询分类属性
     * @param categoryId
     * @param propertyId
     * @return
     * @throws Exception
     */
    private CategoryProperty getCategoryProperty(Long categoryId, Long propertyId) throws Exception{
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(categoryId);
        categoryProperty.setPropertyId(propertyId);
        return categoryPropertyService.selectOne(categoryProperty);
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
    private void saveItemSalesPropery(ItemSalesPropery itemSalesPropery, List<Skus> skuses, Long categoryId)throws Exception{
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
                Long propertyId = Long.parseLong(_tmp[0]);
                _itemSalesPropery.setPropertyId(propertyId);
                _itemSalesPropery.setPropertyValueId(propertyValueId);
                _itemSalesPropery.setPropertyActualValue(propertyValuesArray[i]);
                _itemSalesPropery.setPicture(_tmp[1]);
                _itemSalesPropery.setIsValid(ZeroToNineEnum.ONE.getCode());
                _itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                _itemSalesPropery.setCreateTime(sysTime);
                _itemSalesPropery.setUpdateTime(sysTime);
                //判断属性是否停用
                checkCategoryPropertyValidStatus(categoryId, propertyId, propertyValueId);
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
        String[] _result = null;
        for(Object obj : itemSalesArray){
            JSONObject jbo = (JSONObject) obj;
            if(propertyValueId.longValue() == jbo.getLong("propertyValueId").longValue()){
                _result = new String[3];
                _result[0] = jbo.getString("propertyId");
                _result[1] = jbo.getString("picture");
                _result[2] = jbo.getString("isValid");
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
    @CacheEvit(key = { "#items.id"} )
    private void updateItemsBase(Items items) throws Exception{
        AssertUtil.notNull(items.getId(), "商品ID不能为空");
        items.setUpdateTime(Calendar.getInstance().getTime());
        checkCategoryBrandValidStatus(items.getCategoryId(), items.getBrandId());
        int count = itemsService.updateByPrimaryKeySelective(items);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改商品基础信息",JSON.toJSONString(items),"数据库操作失败").toString();
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
    }

    private Long getLongValue(String val){
        if(StringUtils.isNotBlank(val)){
            BigDecimal d = new BigDecimal(val);
            return CommonUtil.getMoneyLong(d);
        }
        return null;
    }

    private List<Skus> updateSkus(Skus skus, String userId) throws Exception{
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
            skus2.setWeight(CommonUtil.getWeightLong(jbo.getString("weight2")));
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
            //记录sku启停用状态更新日志
            updateSkusValidStatusLog(updatelist, userId);
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
        _tmp.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Skus> _skus = skusService.select(_tmp);
        AssertUtil.notEmpty(_skus, String.format("根据商品SPU编码[%s]查询相关SKU为空", skus.getSpuCode()));
        return _skus;
    }

    /**
     * 记录sku启停用状态更新日志
     * @param skusList
     */
    private void updateSkusValidStatusLog(List<Skus> skusList, String userId){
        List<String> skuCodes = new ArrayList<String>();
        for(Skus skus: skusList){
            skuCodes.add(skus.getSkuCode());
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skuCodes);
        List<Skus> skusList2 = skusService.selectByExample(example);
        AssertUtil.notEmpty(skusList2, String.format("根据多个sku编码查询sku信息为空", CommonUtil.converCollectionToString(skuCodes)));
        List<Skus> tmpList = new ArrayList<Skus>();
        for(Skus skus: skusList2){
            for(Skus skus2: skusList){
                if(StringUtils.equals(skus.getSkuCode(), skus2.getSkuCode())){
                    skus2.setId(skus.getId());
                    if(!StringUtils.equals(skus.getIsValid(), skus2.getIsValid()))
                        tmpList.add(skus2);
                }
            }
        }
        if(tmpList.size() > 0){
            for(Skus skus: tmpList){
                //记录操作日志
                logInfoService.recordLog(skus,skus.getId().toString(),userId,
                        LogOperationEnum.UPDATE.getMessage(),String.format("SKU[%s]状态更新为%s", skus.getSkuCode(), ValidEnum.getValidEnumByCode(skus.getIsValid()).getName()), null);
            }
        }

    }

    /**
     * 更新商品自然属性
     * @param itemNaturePropery
     */
    private void updateItemNatureProperty(ItemNaturePropery itemNaturePropery, Long categoryId)throws Exception{
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
                Long propertyId = jbo.getLong("propertyId");
                _itemNaturePropery.setPropertyId(propertyId);
                Long propertyValueId = jbo.getLong("propertyValueId");
                _itemNaturePropery.setPropertyValueId(propertyValueId);
                _itemNaturePropery.setUpdateTime(sysTime);
                _itemNaturePropery.setIsValid(jbo.getString("isValid"));
                _itemNaturePropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), _itemNaturePropery.getIsValid())){
                    delList.add(_itemNaturePropery.getPropertyValueId());
                }else {
                    //判断属性是否停用
                    checkCategoryPropertyValidStatus(categoryId, propertyId, propertyValueId);
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
    private void updateItemSalesPropery(ItemSalesPropery itemSalesPropery, List<Skus> skuses, Long categoryId) throws Exception{
        JSONArray itemSalesArray = JSONArray.parseArray(itemSalesPropery.getSalesPropertys());
        AssertUtil.notEmpty(itemSalesArray, "保存商品采购属性不能为空");
        List<ItemSalesPropery> addList = new ArrayList<ItemSalesPropery>();
        List<ItemSalesPropery> updateList = new ArrayList<ItemSalesPropery>();
        List<Long> delList = new ArrayList<Long>();
        Set<String> stopSkusList = new HashSet<String>();
        Date sysTime = Calendar.getInstance().getTime();
        List<ItemSalesPropery> list = new ArrayList<ItemSalesPropery>();
        for(Skus skus : skuses){
            String[] propertyValueIdsArray = skus.getPropertyValueId().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            String[] propertyValuesArray = skus.getPropertyValue().split(SKU_PROPERTY_VALUE_ID_SPLIT_SYMBOL);
            List<ItemSalesPropery> _currentList = querySkuItemSalesProperys(skus.getSpuCode(), skus.getSkuCode(), null);
            for(int i=0; i<propertyValueIdsArray.length; i++){
                Long propertyValueId = Long.parseLong(propertyValueIdsArray[i]);
                ItemSalesPropery _itemSalesPropery = getItemSalesPropery(skus, itemSalesArray, propertyValueId, propertyValuesArray[i], sysTime);
                if(null != _itemSalesPropery){
                    if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), _itemSalesPropery.getIsValid())){
                        delList.add(_itemSalesPropery.getPropertyValueId());
                        stopSkusList.add(_itemSalesPropery.getSkuCode());
                    }else {
                        //判断属性是否停用
                        checkCategoryPropertyValidStatus(categoryId, _itemSalesPropery.getPropertyId(), propertyValueId);
                    }
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
        if(delList.size() > 0){
            Example example = new Example(ItemSalesPropery.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuCode", itemSalesPropery.getSpuCode());
            criteria.andIn("propertyValueId", delList);
            count = itemSalesProperyService.deleteByExample(example);
            if (count == 0) {
                String msg = String.format("根据商品SPU编码[%s]和属性值ID[%s]删除采购属性信息", itemSalesPropery.getSpuCode(), JSON.toJSONString(delList));
                log.error(msg);
                throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
            }
        }
        if(stopSkusList.size() > 0){
            //停用SKU
            stopSku(stopSkusList);
            //停用SKU库存
            stopSkuStock(stopSkusList);
        }
    }

    private ItemSalesPropery getItemSalesPropery(Skus skus, JSONArray itemSalesArray, Long propertyValueId, String propertyValue, Date sysTime){
        ItemSalesPropery itemSalesPropery = new ItemSalesPropery();
        itemSalesPropery.setItemId(skus.getItemId());
        itemSalesPropery.setSpuCode(skus.getSpuCode());
        itemSalesPropery.setSkuCode(skus.getSkuCode());
        String[] _tmp = getPropertyIdAndPicture(itemSalesArray, propertyValueId);
        if(null != _tmp){
            itemSalesPropery.setPropertyId(Long.parseLong(_tmp[0]));
            itemSalesPropery.setPropertyValueId(propertyValueId);
            itemSalesPropery.setPropertyActualValue(propertyValue);
            itemSalesPropery.setPicture(_tmp[1]);
            itemSalesPropery.setUpdateTime(sysTime);
            String isValid = _tmp[2];
            itemSalesPropery.setIsValid(isValid);
            itemSalesPropery.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        }else {
            itemSalesPropery = null;
        }
        return itemSalesPropery;
    }

    /**
     * 停用SKU
     * @param stopSkusList
     * @throws Exception
     */
    private void stopSku(Set<String> stopSkusList) throws Exception{
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", stopSkusList);
        Skus skus = new Skus();
        skus.setIsValid(ZeroToNineEnum.ZERO.getCode());
        skusService.updateByExampleSelective(skus, example);
    }

    /**
     * 停用SKU库存
     * @param stopSkusList
     * @throws Exception
     */
    private void stopSkuStock(Set<String> stopSkusList) throws Exception{
        Example example = new Example(SkuStock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", stopSkusList);
        SkuStock skuStock = new SkuStock();
        skuStock.setIsValid(ZeroToNineEnum.ZERO.getCode());
        skuStockService.updateByExampleSelective(skuStock, example);
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
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AppResult updateValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(id, "商品启用/停用操作参数id不能为空");
        AssertUtil.notBlank(isValid, "商品启用/停用操作参数isValid不能为空");
        Items items2 = new Items();
        items2.setId(id);
        items2 = itemsService.selectOne(items2);
        AssertUtil.notNull(items2, String.format("根据主键ID[%s]查询商品基础信息为空", id.toString()));
        if(stopItemsSkusCheck(items2.getSpuCode())){
            return ResultUtil.createFailAppResult("当前SPU下还存在启用的商品,无法停用!");
        }
        Items items = new Items();
        items.setId(id);
        items.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        String _isValid = ZeroToNineEnum.ZERO.getCode();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            _isValid = ZeroToNineEnum.ONE.getCode();
        }
        items.setIsValid(_isValid);
        items2.setIsValid(_isValid);
        int count = itemsService.updateByPrimaryKeySelective(items);
        if(count == 0){
            String msg = "商品启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
        //更新商品相关SKU启用/停用状态
        //updateGoodsSkusValid(items2.getSpuCode(),_isValid, CommonUtil.getUserId(requestContext));
        //更新SKU库存启停用状态
        //updateSkuStockIsValid(items2.getSpuCode(), null, _isValid);
        //更新采购单明细启停用状态
        //updatePurchaseDetailIsValid(items2.getSpuCode(), null, _isValid);
        //商品启停用通知渠道
        itemsUpdateNoticeChannel(items2, TrcActionTypeEnum.ITEMS_IS_VALID);
        //记录操作日志
        logInfoService.recordLog(items2,items.getId().toString(),aclUserAccreditInfo.getUserId(),
                LogOperationEnum.UPDATE.getMessage(),String.format("SPU状态更新为%s", ValidEnum.getValidEnumByCode(_isValid).getName()), null);
        return ResultUtil.createSucssAppResult(String.format("%s商品SPU成功", ValidEnum.getValidEnumByCode(_isValid).getName()), "");
    }

    /**
     * 停用自采商品检查是否存在启用的SKU
     * @param spuCode
     * @return
     */
    private boolean stopItemsSkusCheck(String spuCode){
        boolean flag = false;
        Skus skus = new Skus();
        skus.setSpuCode(spuCode);
        skus.setIsValid(ValidEnum.VALID.getCode());
        List<Skus> skusList = skusService.select(skus);
        if(skusList.size() > 0)
            flag = true;
        return flag;
    }

    private void updateGoodsSkusValid(String spuCode, String isValid, String userId) throws Exception{
        Skus skus = new Skus();
        skus.setSpuCode(spuCode);
        String _isValid = ZeroToNineEnum.ZERO.getCode();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            _isValid = ZeroToNineEnum.ONE.getCode();
        }
        skus.setIsValid(_isValid);
        List<Skus> skusList = skusService.select(skus);
        //AssertUtil.notEmpty(skusList, String.format("根据商品SPU编码[%s]查询相关SKU信息为空", spuCode));
        if(skusList.size() > 0){
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
            for(Skus skus2: skusList){
                //记录操作日志
                logInfoService.recordLog(skus2,skus2.getId().toString(),userId,
                        LogOperationEnum.UPDATE.getMessage(),String.format("SKU[%s]状态更新为%s", skus2.getSkuCode(), ValidEnum.getValidEnumByCode(isValid).getName()), null);
            }
        }
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSkusValid(Long id, String spuCode, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
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
        //updateItemsValid(spuCode, _isValid);
        Skus skus2 = new Skus();
        skus2.setId(id);
        skus2 = skusService.selectOne(skus2);
        AssertUtil.notNull(skus2, String.format("根据商品sku的ID[%s]查询SKU信息为空", id));
        //更新SKU库存启停用状态
        updateSkuStockIsValid(spuCode, skus2.getSkuCode(), _isValid);
        //更新采购单明细启停用状态
        updatePurchaseDetailIsValid(spuCode, skus2.getSkuCode(), _isValid);
        Items items = new Items();
        items.setSpuCode(spuCode);
        items = itemsService.selectOne(items);
        AssertUtil.notNull(items, String.format("根据商品spuCode编码[%s]查询商品信息为空", spuCode));
        //商品SKU启停用通知渠道
        itemsUpdateNoticeChannel(items, TrcActionTypeEnum.ITEMS_SKU_IS_VALID);
        //记录操作日志
        logInfoService.recordLog(items,items.getId().toString(),aclUserAccreditInfo.getUserId(),
                LogOperationEnum.UPDATE.getMessage(),String.format("SKU[%s]状态更新为%s", skus2.getSkuCode(), ValidEnum.getValidEnumByCode(_isValid).getName()), null);
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
    @Cacheable(key="#spuCode+#skuCode+#aclUserAccreditInfo.channelCode",isList=true)
    public ItemsExt queryItemsInfo(String spuCode, String skuCode, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notBlank(spuCode, "查询商品详情参数商品SPU编码supCode不能为空");
        AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
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
        if(StringUtils.isNotBlank(skuCode)){
            skus.setSkuCode(skuCode);
        }
        skus.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Skus> skuses = skusService.select(skus);
        AssertUtil.notEmpty(skuses, String.format("根据商品SPU编码[%s]查询商品SKU信息为空", spuCode));
        //设置商品重量和市场价返回值
        for(Skus s : skuses){
            if(null != s.getWeight() && s.getWeight() >= 0){
                s.setWeight2(CommonUtil.getWeight(s.getWeight()));
            }
            if(null != s.getMarketPrice() && s.getMarketPrice() >= 0){
                s.setMarketPrice2(CommonUtil.fenToYuan(s.getMarketPrice()));
            }
            if(StringUtils.isNotBlank(skuCode)){//查询查询模块发起的sku详情查询
                Example example = new Example(SkuStock.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("skuCode", skuCode);
                criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
                List<SkuStock> skuStocks = skuStockService.selectByExample(example);
                if(skuStocks.size() > 0){
                    SkuStock skuStock = skuStocks.get(0);
                    s.setAvailableInventory(skuStock.getAvailableInventory());
                    s.setRealInventory(skuStock.getRealInventory());
                    s.setDefectiveInventory(skuStock.getDefectiveInventory());
                    Warehouse warehouse = new Warehouse();
                    warehouse.setCode(skuStock.getWarehouseCode());
                    warehouse = warehouseService.selectOne(warehouse);
                    AssertUtil.notNull(warehouse, String.format("根据仓库编码[%s]查询仓库信息为空", skuStock.getWarehouseCode()));
                    s.setWarehouse(warehouse.getName());
                }
            }
        }
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
    //@Cacheable(key="#spuCode+#categoryId",isList=true)
    public List<CategoryProperty> queryItemsCategoryProperty(String spuCode, Long categoryId) throws Exception {
        AssertUtil.notBlank(spuCode, "查询商品分类属性spuCode为空");
        AssertUtil.notNull(categoryId, "查询商品分类属性categoryId为空");
        Example example = new Example(CategoryProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        //criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        example.orderBy("propertySort").asc();
        List<CategoryProperty> categoryProperties = categoryPropertyService.selectByExample(example);
        AssertUtil.notEmpty(categoryProperties, String.format("根据分类ID[%s]查询分类属性为空", categoryId));
        //获取自然属性和采购属性
        Object[] objs = getItemsPropertys(spuCode);
        List<ItemNaturePropery> itemNatureProperies = (List<ItemNaturePropery>)objs[0];
        List<ItemSalesPropery> itemSalesProperies = (List<ItemSalesPropery>)objs[1];
        //将已经禁用的属性加入到返回的分类属性列表里面
        handlerCategoryPropertys(itemNatureProperies, itemSalesProperies, categoryProperties, categoryId);
        //设置分类属性名称
        setCategoryPropertyName(categoryProperties);
        return categoryProperties;
    }

    @Override
    @Cacheable(key="#queryModel.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<ExternalItemSku> externalGoodsPage(ExternalItemSkuForm queryModel, Pagenation<ExternalItemSku> page) throws Exception{
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(queryModel.getSupplierCode())) {//供应商编号
            criteria.andEqualTo("supplierCode", queryModel.getSupplierCode());
        }
        if (StringUtils.isNotBlank(queryModel.getSkuCode())) {//商品SKU编号
            criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getItemName())) {//商品名称
            criteria.andLike("itemName", "%" + queryModel.getItemName() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getWarehouse())) {//仓库名称
            criteria.andLike("warehouse", "%" + queryModel.getWarehouse() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getBrand())) {//品牌
            criteria.andLike("brand", "%" + queryModel.getBrand() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getBarCode())) {//条形码
            criteria.andLike("barCode", "%" + queryModel.getBarCode() + "%");
        }
        example.orderBy("updateTime").desc();
        page = externalItemSkuService.pagination(example, page, queryModel);
        setSupplierName(page.getResult());
        return page;
    }

    @Override
    @Cacheable(key="#trc.toString()",isList=true)
    public List<ExternalItemSku> queryExternalItems(ExternalItemSkuForm form) {
        AssertUtil.notNull(form, "查询代发商品参数不能为空");
        ExternalItemSku externalItemSku = new ExternalItemSku();
        BeanUtils.copyProperties(form, externalItemSku);
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.select(externalItemSku);
        for(ExternalItemSku externalItems : externalItemSkuList){
            if(StringUtils.equals(externalItems.getSupplierCode(), JD_SUPPLIER_CODE)){
                externalItems.setJdPictureUrl(externalSupplierConfig.getJdPictureUrl());
            }
        }
        return externalItemSkuList;
    }

    /**
     * 设置一件代发供应商名称
     * @param externalItemSkuList
     * @throws Exception
     */
    private void setSupplierName(List<ExternalItemSku> externalItemSkuList) throws Exception{
        for(ExternalItemSku items: externalItemSkuList){
            items.setSupplierName(getSupplierName(items.getSupplierCode()));
        }
    }

    @Override
    public Pagenation<SupplyItemsExt> externalGoodsPage2(SupplyItemsForm queryModel, Pagenation<SupplyItemsExt> page) throws Exception{
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        SupplyItemsExt supplyItems2 = new SupplyItemsExt();
        BeanUtils.copyProperties(queryModel, supplyItems2);
        ReturnTypeDO<Pagenation<SupplyItemsExt>> returnTypeDO = jdService.skuPage(supplyItems2, page);
        if(!returnTypeDO.getSuccess()){
            log.error(returnTypeDO.getResultMessage());
            return page;
        }
        page = returnTypeDO.getResult();
        setOutSupplierName(page.getResult());
        return page;
    }


    /**
     * 设置一件代发供应商名称
     * @param supplyItemsList
     * @throws Exception
     */
    private void setOutSupplierName(List<SupplyItemsExt> supplyItemsList) throws Exception{
        for(SupplyItemsExt items: supplyItemsList){
            items.setSupplyName(getSupplierName(items.getSupplierCode()));
        }
    }

    /**
     * 获取一件代发供应商名称
     * @param supplierCode
     * @return
     * @throws Exception
     */
    private String getSupplierName(String supplierCode) throws Exception{
        DictForm form = new DictForm();
        form.setTypeCode(SupplyConstants.SelectList.SUPPLIER);
        form.setValue(supplierCode);
        List<Dict> dicts = configBiz.queryDicts(form);
        AssertUtil.notEmpty(dicts, String.format("根据字典类型编码[%s]和字典编码[%s]查询字典信息为空",
                SupplyConstants.SelectList.SUPPLIER, supplierCode));
        return dicts.get(0).getName();
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveExternalItems(String supplySkus, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(supplySkus, "新增代发商品不能为空");
        JSONArray skuArray = null;
        try{
            skuArray = JSONArray.parseArray(supplySkus);
        }catch (Exception e){
            String msg = String.format("新增代发商品参数不是JSON格式,错误信息:%s", e.getMessage());
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
        AssertUtil.notEmpty(skuArray, "新增代发商品不能为空");
        List<SupplyItems> supplyItems = new ArrayList<SupplyItems>();
        for(Object jbo: skuArray){
            JSONObject obj =  (JSONObject)jbo;
            SupplyItems items = JSON.parseObject(JSON.toJSONString(obj),SupplyItems.class);
            supplyItems.add(items);
        }
        List<ExternalItemSku> externalItemSkuList = getExternalItemSkus(supplyItems, ZeroToNineEnum.ZERO.getCode());
        int count = externalItemSkuService.insertList(externalItemSkuList);
        if(count == 0){
            String msg = String.format("保存京东一件代发商品%s到数据库失败", JSON.toJSONString(externalItemSkuList));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_SAVE_EXCEPTION, msg);
        }
        updateSupplyItemsUsedStatus(externalItemSkuList);
        List<String> newIds = new ArrayList<String>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            newIds.add(externalItemSku.getId().toString());
        }
        //记录操作日志
        logInfoService.recordLogs(new ExternalItemSku(),aclUserAccreditInfo.getUserId(),
                LogOperationEnum.ADD.getMessage(), null, null, newIds);
    }

    /**
     * 代发商品更新通知渠道
     * @param oldExternalItemSkuList
     * @param externalItemSkuList
     * @param trcActionTypeEnum
     */
    private void externalItemsUpdateNoticeChannel(List<ExternalItemSku> oldExternalItemSkuList, List<ExternalItemSku> externalItemSkuList, TrcActionTypeEnum trcActionTypeEnum){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            ToGlyResultDO toGlyResultDO = trcBiz.sendExternalItemSkuUpdation(trcActionTypeEnum, oldExternalItemSkuList, externalItemSkuList, System.currentTimeMillis());
                            if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
                                if(log.isInfoEnabled())
                                    log.info("更新代发商品通知渠道成功");
                            }else{
                                log.error(String.format("更新代发商品通知渠道失败,错误信息:%s", toGlyResultDO.getMsg()));
                            }
                        }catch (Exception e){
                            String msg = String.format("更新代发商品通知渠道异常,异常信息:%s", e.getMessage());
                            log.error(msg, e);
                        }
                    }
                }
        ).start();
    }

    @Override
    @CacheEvit
    public void updateExternalItemsValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(id, "代发商品启用/停用操作参数id不能为空");
        AssertUtil.notBlank(isValid, "代发商品启用/停用操作参数isValid不能为空");
        ExternalItemSku externalItemSku = externalItemSkuService.selectByPrimaryKey(id);
        AssertUtil.notNull(externalItemSku, String.format("根据主键ID[%s]查询代发商品为空", id));
        String _isValid = ZeroToNineEnum.ZERO.getCode();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)){
            _isValid = ZeroToNineEnum.ONE.getCode();
        }
        ExternalItemSku externalItemSku2 = new ExternalItemSku();
        externalItemSku2.setId(id);
        externalItemSku2.setIsValid(_isValid);
        int count = externalItemSkuService.updateByPrimaryKeySelective(externalItemSku2);
        if(count == 0){
            String msg = "代发商品启用/停用操作更新数据库失败";
            log.error(msg);
            throw new GoodsException(ExceptionEnum.GOODS_UPDATE_EXCEPTION, msg);
        }
        ExternalItemSku externalItemSku3 = externalItemSkuService.selectByPrimaryKey(id);
        AssertUtil.notNull(externalItemSku3, String.format("根据主键ID[%s]查询代发商品为空", id));
        List<ExternalItemSku> oldExternalItemSkuList = new ArrayList<ExternalItemSku>();
        List<ExternalItemSku> externalItemSkuList = new ArrayList<ExternalItemSku>();
        oldExternalItemSkuList.add(externalItemSku);
        externalItemSkuList.add(externalItemSku3);
        //代发商品启停用通知渠道
        externalItemsUpdateNoticeChannel(oldExternalItemSkuList, externalItemSkuList, TrcActionTypeEnum.EXTERNAL_ITEMS_IS_VALID);
        //记录操作日志
        logInfoService.recordLog(externalItemSku3,id.toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(),
                String.format("SKU[%s]状态更新为%s", externalItemSku3.getSkuCode(), ValidEnum.getValidEnumByCode(_isValid).getName()), null);
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateExternalItems(ExternalItemSku externalItemSku, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(externalItemSku, "更新代发商品不能为空");
        AssertUtil.notNull(externalItemSku.getId(), "更新代发商品ID不能为空");
        ExternalItemSku externalItemSku2 = externalItemSkuService.selectByPrimaryKey(externalItemSku.getId());
        AssertUtil.notNull(externalItemSku2, String.format("根据主键ID[%s]查询代发商品为空", externalItemSku.getId()));
        int count = externalItemSkuService.updateByPrimaryKeySelective(externalItemSku);
        if(count == 0){
            String msg = String.format("根据主键ID[%s]更新代发商品%s失败", externalItemSku.getId(), JSONObject.toJSON(externalItemSku));
            log.error(msg);
            throw new GoodsException(ExceptionEnum.EXTERNAL_GOODS_UPDATE_EXCEPTION, msg);
        }
        ExternalItemSku externalItemSku3 = externalItemSkuService.selectByPrimaryKey(externalItemSku.getId());
        AssertUtil.notNull(externalItemSku3, String.format("根据主键ID[%s]查询代发商品为空", externalItemSku.getId()));
        List<ExternalItemSku> oldExternalItemSkuList = new ArrayList<ExternalItemSku>();
        List<ExternalItemSku> externalItemSkuList = new ArrayList<ExternalItemSku>();
        oldExternalItemSkuList.add(externalItemSku2);
        externalItemSkuList.add(externalItemSku3);
        //代发商品编辑通知渠道
        externalItemsUpdateNoticeChannel(oldExternalItemSkuList, externalItemSkuList, TrcActionTypeEnum.EDIT_EXTERNAL_ITEMS);
        //记录操作日志
        logInfoService.recordLog(externalItemSku3,externalItemSku3.getId().toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(), null, null);
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void supplierSkuUpdateNotice(String updateSupplierSkus) {
        AssertUtil.notBlank(updateSupplierSkus, "根据供应商sku更新通知更新一件代发商品供应商更新的sku参数updateSupplierSkus不能为空");
        JSONArray skusArray = null;
        try{
            skusArray = JSONArray.parseArray(updateSupplierSkus);
        }catch (JSONException e){
            String msg = String.format("根据供应商sku更新通知更新一件代发商品供应商更新的sku参数updateSupplierSkus不是json数组格式,错误信息:%s", e.getMessage());
            log.error(msg);
            throw new GoodsException(ExceptionEnum.EXTERNAL_GOODS_UPDATE_EXCEPTION, msg);
        }
        AssertUtil.notEmpty(skusArray, "根据供应商sku更新通知更新一件代发商品供应商更新的sku参数updateSupplierSkus不能为空");
        List<String> supplySkuList = new ArrayList<>();
        List<SupplyItems> supplyItems = new ArrayList<SupplyItems>();
        for(Object obj : skusArray){
            JSONObject jbo = (JSONObject)obj;
            SupplyItems supplyItems2 = jbo.toJavaObject(SupplyItems.class);
            supplyItems.add(supplyItems2);
            supplySkuList.add(supplyItems2.getSupplySku());
        }
        Example example2 = new Example(ExternalItemSku.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andIn("supplierSkuCode", supplySkuList);
        List<ExternalItemSku> oldExternalItemSkuList = externalItemSkuService.selectByExample(example2);
        if(CollectionUtils.isEmpty(oldExternalItemSkuList)){
            return;
        }
        List<ExternalItemSku> externalItemSkuList = getExternalItemSkus(supplyItems, ZeroToNineEnum.ONE.getCode());
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            for(ExternalItemSku externalItemSku2: oldExternalItemSkuList){
                if(StringUtils.equals(externalItemSku.getSupplierCode(), externalItemSku2.getSupplierCode())&&
                        StringUtils.equals(externalItemSku.getSupplierSkuCode(), externalItemSku2.getSupplierSkuCode())){
                    externalItemSku.setSkuCode(externalItemSku2.getSkuCode());
                }
            }
            Example example = new Example(ExternalItemSku.class);
            Example.Criteria criteria =example.createCriteria();
            criteria.andEqualTo("supplierSkuCode", externalItemSku.getSupplierSkuCode());
            int count = externalItemSkuService.updateByExampleSelective(externalItemSku, example);
            if(count == 0){
                String msg = String.format("根据供应商SKU编号[%s]更新代发商品%s失败", externalItemSku.getSupplierSkuCode(), JSONObject.toJSON(externalItemSku));
                log.error(msg);
                throw new GoodsException(ExceptionEnum.EXTERNAL_GOODS_UPDATE_EXCEPTION, msg);
            }
        }

        List<ExternalItemSku> oldExternalItemSkuList2 = externalItemSkuService.selectByExample(example2);
        AssertUtil.notEmpty(oldExternalItemSkuList2, String.format("根据多个供应商skuCode[%s]查询代发商品为空", CommonUtil.converCollectionToString(supplySkuList)));
        //代发商品更新通知渠道
        externalItemsUpdateNoticeChannel(oldExternalItemSkuList, externalItemSkuList, TrcActionTypeEnum.DAILY_EXTERNAL_ITEMS_UPDATE);
    }


    /**
     *
     * @param supplyItems
     * @param flag 0-新增代发商品,1-根据供应商sku更新通知更新一件代发商品
     * @return
     */
    private List<ExternalItemSku> getExternalItemSkus(List<SupplyItems> supplyItems, String flag){
        List<ExternalItemSku> externalItemSkus = new ArrayList<ExternalItemSku>();
        Date sysDate = Calendar.getInstance().getTime();
        String sysDateStr = DateUtils.dateToCompactString(sysDate);
        for(SupplyItems items: supplyItems){
            ExternalItemSku externalItemSku = new ExternalItemSku();
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//新增代发商品
                String code = serialUtilService.generateCode(SupplyConstants.Serial.SKU_LENGTH, SupplyConstants.Serial.SKU_NAME,
                        SupplyConstants.Serial.SKU_OUTERER, sysDateStr);
                externalItemSku.setSkuCode(code);
                externalItemSku.setIsValid(ZeroToNineEnum.ONE.getCode());
                externalItemSku.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                externalItemSku.setCreateTime(sysDate);
                if(StringUtils.equals(externalItemSku.getSupplierCode(), JD_SUPPLIER_CODE)){
                    externalItemSku.setWarehouse(externalSupplierConfig.getJdWarehouse());//京东仓库
                }else if(StringUtils.equals(externalItemSku.getSupplierCode(), LY_SUPPLIER_CODE)){
                    externalItemSku.setWarehouse(externalSupplierConfig.getLyWarehouse());//粮油仓库
                }
                externalItemSku.setSupplyPrice(CommonUtil.getMoneyLong(items.getSupplyPrice()));
                externalItemSku.setSupplierPrice(CommonUtil.getMoneyLong(items.getSupplierPrice()));
                externalItemSku.setMarketReferencePrice(CommonUtil.getMoneyLong(items.getMarketPrice()));
            }else{
                if(null != items.getSupplyPrice())
                    externalItemSku.setSupplyPrice(items.getSupplyPrice().longValue());
                if(null != items.getSupplierPrice())
                    externalItemSku.setSupplierPrice(items.getSupplierPrice().longValue());
                if(null != items.getMarketPrice())
                    externalItemSku.setMarketReferencePrice(items.getMarketPrice().longValue());
            }
            externalItemSku.setSupplierCode(items.getSupplierCode());
            externalItemSku.setSupplierName(items.getSupplyName());
            externalItemSku.setSupplierSkuCode(items.getSupplySku());
            externalItemSku.setItemName(items.getSkuName());
            externalItemSku.setCategory(items.getCategory());
            externalItemSku.setCategoryName(items.getCategoryName());
            externalItemSku.setBarCode(items.getUpc());
            //externalItemSku.setSubtitle();//商品副标题 TODO
            externalItemSku.setBrand(items.getBrand());
            externalItemSku.setCategory(items.getCategory());
            externalItemSku.setWeight(CommonUtil.getWeightLong(items.getWeight()));
            externalItemSku.setProducingArea(items.getProductArea());
            //externalItemSku.setPlaceOfDelivery();//发货地址 TODO
            externalItemSku.setItemType(items.getSkuType());
            //externalItemSku.setTariff(); //税率 TODO
            externalItemSku.setMainPictrue(items.getImagePath());//主图
            externalItemSku.setDetailPictrues(items.getDetailImagePath());//详图
            externalItemSku.setDetail(items.getIntroduction());
            //externalItemSku.setProperties();// 属性 TODO
            //externalItemSku.setStock();//库存 ,调用京东接口实时设置 TODO
            externalItemSku.setUpdateTime(sysDate);
            externalItemSkus.add(externalItemSku);
        }
        return externalItemSkus;
    }

    /**
     * 更新京东商品是否使用状态
     * @param externalItemSkuList
     */
    private void updateSupplyItemsUsedStatus(List<ExternalItemSku> externalItemSkuList){
        List<SkuDO> skuDOList = new ArrayList<SkuDO>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            SkuDO skuDO = new SkuDO();
            skuDO.setSupplySku(externalItemSku.getSupplierSkuCode());
            skuDOList.add(skuDO);
        }
        ReturnTypeDO returnTypeDO = jdService.noticeUpdateSkuUsedStatus(skuDOList);
        if(!returnTypeDO.getSuccess()){
            log.error(returnTypeDO.getResultMessage());
            throw new GoodsException(ExceptionEnum.EXTERNAL_GOODS_UPDATE_NOTICE_CHANNEL_EXCEPTION, returnTypeDO.getResultMessage());
        }
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
     * 将已经禁用的属性加入到返回的分类属性列表里面
     * @param categoryProperties
     */
    private void handlerCategoryPropertys(List<ItemNaturePropery> itemNatures, List<ItemSalesPropery>
            itemSales, List<CategoryProperty> categoryProperties, Long categoryId){
        for(ItemNaturePropery itemNaturePropery: itemNatures){
            Boolean flag = false;
            for(CategoryProperty categoryProperty: categoryProperties){
                if(itemNaturePropery.getPropertyId().longValue() == categoryProperty.getPropertyId().longValue()){
                    flag = true;
                    break;
                }
            }
            if(!flag){
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
        }
        for(ItemSalesPropery itemSalesPropery: itemSales){
            Boolean flag = false;
            for(CategoryProperty categoryProperty: categoryProperties){
                if(itemSalesPropery.getPropertyId().longValue() == categoryProperty.getPropertyId().longValue()){
                    flag = true;
                    break;
                }
            }
            if(!flag){
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
