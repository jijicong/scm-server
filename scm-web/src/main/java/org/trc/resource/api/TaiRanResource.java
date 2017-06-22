package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.txframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.biz.impl.category.BrandBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.order.*;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.PropertyForm;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.order.*;
import org.trc.util.*;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * 对泰然城开放接口
 * Created by hzdzf on 2017/5/26.
 */
@Component
@Path(SupplyConstants.TaiRan.ROOT)
public class TaiRanResource {

    private Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @Resource
    private BrandBiz brandBiz;

    @Resource
    private IPropertyBiz propertyBiz;

    @Resource
    private ICategoryBiz categoryBiz;

    @Resource
    private IRequestFlowService requestFlowService;

    @Resource
    private ISkuRelationBiz skuRelationBiz;

    @Resource
    private ISkuRelationService skuRelationService;

    @Resource
    private IExternalItemSkuService externalItemSkuService;

    @Resource
    private IOrderItemService orderItemService;

    @Resource
    private IPlatformOrderService platformOrderService;

    @Resource
    private IShopOrderService shopOrderService;

    @Resource
    private IWarehouseOrderService warehouseOrderService;

    @Resource
    private IOrderFlowService orderFlowService;

    /**
     * 分页查询品牌
     *
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Brand>> queryBrand(@BeanParam BrandForm form, @BeanParam Pagenation<Brand> page) {

        try {
            page = brandBiz.brandList(form, page);
            List<Brand> list = new ArrayList<Brand>();
            for (Brand brand : page.getResult()) {
                Brand brand1 = new Brand();
                brand1.setName(brand.getName());
                brand1.setBrandCode(brand.getBrandCode() == null ? "" : brand.getBrandCode());
                brand1.setAlise(brand.getAlise() == null ? "" : brand.getAlise());
                brand1.setWebUrl(brand.getWebUrl() == null ? "" : brand.getWebUrl());
                brand1.setIsValid(brand.getIsValid());
                brand1.setUpdateTime(brand.getUpdateTime());
                brand1.setSort(brand.getSort());
                list.add(brand1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询品牌列表成功", page);
        } catch (Exception e) {
            logger.error("查询品牌列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询品牌列表报错：" + e.getMessage());
        }
    }

    /**
     * 分页查询属性
     *
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.PROPERTY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Property>> queryProperty(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) {
        try {
            page = propertyBiz.propertyPage(form, page);
            List<Property> list = new ArrayList<Property>();
            for (Property property : page.getResult()) {
                Property property1 = new Property();
                property1.setName(property.getName());
                property1.setSort(property.getSort());
                property1.setTypeCode(property.getTypeCode());
                property1.setValueType(property.getValueType());
                property1.setIsValid(property.getIsValid());
                property1.setUpdateTime(property.getUpdateTime());
                list.add(property1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询属性列表成功", page);
        } catch (Exception e) {
            logger.error("查询属性列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询属性列表报错：" + e.getMessage());
        }
    }

    /**
     * 分页查询分类
     *
     * @param categoryForm
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Category>> queryCategory(@BeanParam CategoryForm categoryForm, @BeanParam Pagenation<Category> page) {
        try {
            page = categoryBiz.categoryPage(categoryForm, page);
            List<Category> list = new ArrayList<Category>();
            for (Category category : page.getResult()) {
                Category category1 = new Category();
                category1.setName(category.getName());
                category1.setSort(category.getSort());
                category1.setIsValid(category.getIsValid());
                category1.setUpdateTime(category.getUpdateTime());
                if (category.getParentId() != null) {
                    category1.setParentId(category.getParentId());
                }
                category1.setClassifyDescribe(category.getClassifyDescribe() == null ? "" : category.getClassifyDescribe());
                category1.setLevel(category.getLevel());
                list.add(category1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询分类列表成功", page);
        } catch (Exception e) {
            logger.error("查询分类列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询分类品牌列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryBrand>> queryCategoryBrand(@QueryParam("categoryId") Long categoryId) {
        try {
            return ResultUtil.createSucssAppResult("查询分类品牌列表成功", categoryBiz.queryBrands(categoryId));
        } catch (Exception e) {
            logger.error("查询分类品牌列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类品牌列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询分类属性列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_PROPERTY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryProperty>> queryCategoryProperty(@QueryParam("categoryId") Long categoryId) {
        try {
            return ResultUtil.createSucssAppResult("查询分类属性列表成功", categoryBiz.queryProperties(categoryId));
        } catch (Exception e) {
            logger.error("查询分类属性列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类属性列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询单个sku信息
     *
     * @param skuCode 传递供应链skuCode
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.SKU_INFORMATION)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<String> getSpuInformation(@QueryParam("skuCode") String skuCode) {
        try {
            return ResultUtil.createSucssAppResult("查询sku信息成功", skuRelationBiz.getSkuInformation(skuCode));
        } catch (Exception e) {
            logger.error("查询sku信息报错: " + e.getMessage());
            return ResultUtil.createFailAppResult("查询sku信息报错：" + e.getMessage());
        }
    }

    /**
     * 订单拆分，以仓库级订单传参
     *
     * @return
     */
    //@BeanParam PlatformOrder platformOrder, @BeanParam ShopOrder shopOrder, @BeanParam OrderItem orderItem
    @POST
    @Path(SupplyConstants.TaiRan.ORDER_PROCESSING)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<String> getOrderList(JSONObject information) {
        try {
            //获取平台订单信息
            PlatformOrder platformOrder = JSONObject.parseObject(information.getJSONObject("platformOrder").toJSONString(), PlatformOrder.class);
            JSONArray shopOrderList = information.getJSONArray("shopOrderList");
            JSONArray orderItemList = information.getJSONArray("orderItemList");
            List<OrderItem> orderItems = orderItemList.toJavaList(OrderItem.class);
            List<ShopOrder> shopOrders = shopOrderList.toJavaList(ShopOrder.class);
            //验参
            Assert.notNull(platformOrder.getChannelCode(), "渠道编码不能为空");
            Assert.notNull(platformOrder.getPlatformCode(), "来源平台编码不能为空");
            Assert.notNull(platformOrder.getPlatformOrderCode(), "平台订单编码不能为空");
            Assert.notNull(platformOrder.getUserId(), "会员id不能为空");
            Assert.notNull(platformOrder.getUserName(), "会员名称不能为空");
            Assert.notNull(platformOrder.getAdjustFee(), "卖家手工调整金额不能为空");
            Assert.notNull(platformOrder.getTotalFee(), "订单总金额不能为空");
            Assert.notNull(platformOrder.getPostageFee(), "邮费不能为空");
            Assert.notNull(platformOrder.getTotalTax(), "总税费不能为空");
            Assert.notNull(platformOrder.getPayment(), "实付金额不能为空");
            Assert.notNull(platformOrder.getPayType(), "支付类型不能为空");
            Assert.notNull(platformOrder.getItemNum(), "买家购买的商品总数不能为空");
            for (ShopOrder shopOrder : shopOrders) {
                Assert.notNull(shopOrder.getChannelCode(), "渠道编码不能为空");
                Assert.notNull(shopOrder.getPlatformCode(), "来源平台编码不能为空");
                Assert.notNull(shopOrder.getPlatformOrderCode(), "平台订单编码不能为空");
                Assert.notNull(shopOrder.getShopOrderCode(), "店铺订单编码不能为空");
                Assert.notNull(shopOrder.getPlatformType(), "订单来源类型不能为空");
                Assert.notNull(shopOrder.getShopId(), "订单所属的店铺id不能为空");
                Assert.notNull(shopOrder.getShopName(), "店铺名称不能为空");
                Assert.notNull(shopOrder.getUserId(), "会员id不能为空");
                Assert.notNull(shopOrder.getStatus(), "订单状态不能为空");
            }
            for (OrderItem orderItem : orderItems) {
                Assert.notNull(orderItem.getChannelCode(), "渠道编码不能为空");
                Assert.notNull(orderItem.getPlatformCode(), "来源平台编码不能为空");
                Assert.notNull(orderItem.getPlatformOrderCode(), "平台订单编码不能为空");
                Assert.notNull(orderItem.getShopOrderCode(), "店铺订单编码不能为空");
                Assert.notNull(orderItem.getShopId(), "订单所属的店铺id不能为空");
                Assert.notNull(orderItem.getShopName(), "店铺名称不能为空");
                Assert.notNull(orderItem.getUserId(), "会员id不能为空");
                Assert.notNull(orderItem.getItemNo(), "商品货号不能为空");
                Assert.notNull(orderItem.getBarCode(), "条形码不能为空");
                Assert.notNull(orderItem.getItemName(), "商品名称不能为空");
            }
            //插入流水
            try {
                for (ShopOrder shopOrder : shopOrders) {
                    OrderFlow orderFlow = new OrderFlow();
                    orderFlow.setPlatformOrderCode(platformOrder.getPlatformOrderCode());
                    orderFlow.setShopOrderCode(shopOrder.getShopOrderCode());
                    orderFlow.setType("DEAL");
                    orderFlowService.insert(orderFlow);
                }
            } catch (Exception e) {
                logger.error("重复提交订单: " + e.getMessage());
                return ResultUtil.createFailAppResult("重复提交订单：" + e.getMessage());
            }

            //分离一件代发和自采商品
            List<OrderItem> orderItems1 = new ArrayList<>();//TODO 自采商品,二期处理
            List<OrderItem> orderItems2 = new ArrayList<>();//一件代发
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getSkuCode().startsWith("SP0")) {
                    orderItems1.add(orderItem);
                } else {
                    orderItems2.add(orderItem);
                }
            }

            //向数据库中批量插入platformOrder
            platformOrderService.insert(platformOrder);

            //以店铺为单位拆分
            for (ShopOrder shopOrder : shopOrders) {
                //匹配供应商，新建仓库级订单，修改orderItem，直接发送订单信息
                dealSupplier(orderItems2, shopOrder, platformOrder);
                shopOrderService.insert(shopOrder);
            }
        } catch (Exception e) {
            logger.error("订单处理报错: " + e.getMessage());
            return ResultUtil.createFailAppResult("订单处理报错：" + e.getMessage());
        }
        logger.info("平台订单推送成功");
        return ResultUtil.createSucssAppResult("订单推送成功，请等待后续通知", "");
    }


    public void dealSupplier(List<OrderItem> orderItems, ShopOrder shopOrder, PlatformOrder platformOrder) throws Exception {
        //新建仓库级订单
        //TODO 待测试
        List<String> supplierNames = skuRelationService.selectSupplierSkuCode(orderItems);

        for (int i = 0; i < supplierNames.size(); i++) {

            Map map = new HashMap();//最后传出去的封装数据
            map.put("platformOrder", platformOrder);
            List<OrderItem> orderItemsToSupplier = new ArrayList<>();
            WarehouseOrder warehouseOrder = new WarehouseOrder();
            warehouseOrder.setShopId(shopOrder.getShopId());
            warehouseOrder.setShopOrderCode(shopOrder.getShopOrderCode());
            warehouseOrder.setShopName(shopOrder.getShopName());
            warehouseOrder.setSupplierName(supplierNames.get(i));
            warehouseOrder.setPlatformCode(shopOrder.getPlatformCode());
            warehouseOrder.setChannelCode(shopOrder.getChannelCode());
            warehouseOrder.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
            warehouseOrder.setPlatformType(shopOrder.getPlatformType());
            warehouseOrder.setUserId(shopOrder.getUserId());
            warehouseOrder.setStatus(new Byte("1"));
            warehouseOrder.setWarehouseOrderCode(GuidUtil.getNextUid(supplierNames.get(i) + "_"));

            Boolean flag = true;
            //循环orderItem,获取供应商skucode，关联仓库级订单，并修改仓库级订单
            Iterator<OrderItem> iterator = orderItems.iterator();
            while (iterator.hasNext()) {
                OrderItem orderItem = iterator.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(orderItem.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                if (externalItemSku.getSupplierName().equals(warehouseOrder.getSupplierName())) {
                    if (flag) {
                        warehouseOrder.setSupplierCode(externalItemSku.getSupplierCode());
                        warehouseOrder.setPayment(orderItem.getPayment());
                        flag = false;
                    } else {
                        warehouseOrder.setPayment(warehouseOrder.getPayment() + orderItem.getPayment());
                    }
                    orderItem.setWarehouseOrderCode(warehouseOrder.getWarehouseOrderCode());
                    orderItem.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
//                    orderItem.setCategoryId(externalItemSku.getCategory());
                    // 向数据库中插入OrderItem
                    orderItemService.insert(orderItem);
                    orderItemsToSupplier.add(orderItem);
                    iterator.remove();
                }
            }
            //向数据中插入仓库级订单
            warehouseOrderService.insert(warehouseOrder);

            map.put("orderItems", orderItemsToSupplier);
            map.put("warehouseOrder", warehouseOrder);
            //TODO 根据供应商信息分别调接口

        }


    }
}
