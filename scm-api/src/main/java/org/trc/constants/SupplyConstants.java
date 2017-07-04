package org.trc.constants;

/**
 * 积分平台常量
 */
public class SupplyConstants {




    /**
     * 符号
     */
    public static final class Symbol {
        public static final String COMMA = ",";//逗号
        public static final String MINUS = "-";//减号
        public static final String FILE_NAME_SPLIT = ".";//文件名称分隔符
        public static final String FULL_PATH_SPLIT = "|";//分类路径ID分隔符
    }


    public static final class Route {
        public static final class Home {
            public static final String ROOT = "home";
        }

        //--------------------------------------------
        public static final class Auth {
            public static final String ROOT = "auth";
            public static final String BLANKLIST = "blanklist";
        }

    }

    /**
     * 对泰然城开放接口
     */
    public static final class TaiRan {

        public static final String ROOT = "tairan";//根路径

        public static final String BRAND_LIST = "brandList";//品牌查询

        public static final String CATEGORY_LIST = "categoryList";//分类查询

        public static final String PROPERTY_LIST = "propertyList";//属性查询

        public static final String CATEGORY_BRAND_LIST = "categoryBrandList";//分类品牌查询

        public static final String CATEGORY_PROPERTY_LIST = "categoryPrepertyList";//分类属性查询

        public static final String SKU_INFORMATION ="skuInformation";//获取单个sku信息

        public static final String ORDER_PROCESSING ="orderProcessing";//订单处理

        public static final String SKURELATION_UPDATE ="skurelation/update";//sku关联信息变更

        public static final String SKUS_LIST = "skusList";//获取自采商品sku列表

        public static final String EXTERNALITEMSKU_LIST = "externalItemSkuList";//获取自采商品skus列表

        public static final String JD_LOGISTICS = "jdLogistics";//获取京东物流信息
    }

    /**
     * 配置管理
     */
    public static final class Config {
        //根路径
        public static final String ROOT = "config";

        /**
         * 字典类型
         */
        public static final class DictType {
            //字典类型分页查询路径
            public static final String DICT_TYPE_PAGE = "dictTypePage";
            //字典类型列表查询路径
            public static final String DICT_TYPE_LIST = "dictTypes";
            //字典类型路径
            public static final String DICT_TYPE = "dictType";
        }

        /**
         * 字典
         */
        public static final class Dict {
            //字典分页查询路径
            public static final String DICT_PAGE = "dictPage";
            //字典列表查询路径
            public static final String DICT_LIST = "dicts";
            //字典路径
            public static final String DICT = "dict";
        }


    }

    public static final class Category {

        //根路径
        public static final String ROOT = "category";

        /**
         * 品牌管理
         */
        public static final class Brand {
            //品牌分页查询路径
            public static final String BRAND_PAGE = "brandPage";
            //品牌列表查询路径
            public static final String BRAND_LIST = "brands";
            //品牌路径
            public static final String BRAND = "brand";

            public static final String BRAND_STATE = "brand/state";
            //查询复数品牌资源路径
            public static final String BRAND_LIST_SEARCH = "brands/search";
        }

        /**
         * 分类品牌
         */
        public static final class CategoryBrands {
            //分类品牌列表查询路径
            public static final String CATEGORY_BAAND_LIST = "categoryBrands";

            //分类品牌关联
            public static final String CATEGORY_BRAND_LINK = "link";

            //            //查询品牌列表
            public static final String BAND_LIST = "brandPageCategory";
        }


        /**
         * 分类属性
         */

        public static final class CategoryProperty {

            //已经关联的分类属性
            public static final String CATEGORY_PROPERTY_PAGE = "categoryProperty";

            //分类属性关联
            public static final String CATEGORY_PROPERTY_LINK = "linkProperty";
            //更新分类属性关联
            public static final String CATEGORY_PROPERTY_UPDATE = "updateProperty";
        }

        /**
         * 属性管理
         */
        public static final class Property {
            //属性分页查询路径
            public static final String PROPERTY_PAGE = "propertyPage";
            //属性列表查询路径
            public static final String PROPERTY_LIST = "propertys";
            //属性路径
            public static final String PROPERTY = "property";

            public static final String PROPERTY_STATE = "property/state";

            public static final String PROPERTY_ALL = "propertyall";
        }

        /**
         * 属性值管理
         */
        public static final class PropertyValue {
            //属性值列表查询路径
            public static final String PROPERTY_VALUE_LIST = "propertyValues/search";
            //属性值路径
            public static final String PROPERTY_VALUE = "propertyValue";

            //根据多个属性ID查询属性值路径
            public static final String MULTI_PROPERTY_ID_SEARCH_PROPERTY_VALUE_LIST = "propertyValues/multiIdsSearch";

        }


        /**
         * 分类管理
         */
        /**
         * 分类管理
         */
        public static final class Classify {
            //查询树
            public static final String CATEGORY_TREE = "tree";

            //添加，修改分类
            public static final String CATEGORY = "category";

            //分类列表
            public static final String CATEGORY_LIST = "categorys";

            //修改排序
            public static final String CATEGORY_SORT = "sort";

            //修改状态
            public static final String UPDATE_STATE = "category/updateState";

            //重名验证
            public static final String CATEGORY_CHECK = "check";

            //查询分类路径
            public static final String CATEGORY_QUERY = "query";

            //校验起停用
            public static final String CATEGORY_VALID = "valid";

        }
    }

    /**
     * 渠道管理
     */
    public static final class Channel {
        //根路径
        public static final String ROOT = "system";
        //渠道分页查询
        public static final String CHANNEL_PAGE = "channelPage";
        //渠道列表
        public static final String CHANNEL_LIST = "channels";
        //渠道名查询
        public static final String CHANNEL = "channel";
        //状态的修改
        public static final String UPDATE_STATE = "channel/updateState";
    }

    /**
     * 仓库管理
     */
    public static final class Warehouse {
        //根路径
        public static final String ROOT = "system";
        //仓库分页查询
        public static final String WAREHOUSE_PAGE = "warehousePage";
        //仓库名查询
        public static final String WAREHOUSE = "warehouse";
        //状态的修改
        public static final String UPDATE_STATE = "warehouse/updateState";
        //查询可用仓库
        public static final String WAREHOUSE_VALID = "wharehouses";
    }

    /**
     * 授权信息
     */
    public static final class UserAccreditInfo {

        //根路径
        public static final String ROOT = "accredit";

        //授权信息分页查询
        public static final String ACCREDIT_PAGE = "accreditInfoPage";

        //授权的用户使用名查询
        public static final String ACCREDIT = "accreditInfo";

        //状态的修改
        public static final String UPDATE_STATE = "accreditInfo/updateState";

        //查询已启用的渠道
        public static final String CHANNEL = "select/channel";

        //查询拥有采购员角色的用户
        public static final String PURCHASE = "purchase";

        //查询选择用户对应角色
        public static final String ROLE = "rolelist";

        //新增授权
        public static final String SAVE_ACCREDIT = "saveaccredit";

        //用户修改
        public static final String UPDATE_ACCREDIT = "updateaccredit";

        //查询用户名是否已被使用
        public static final String CHECK = "check";

        //查询手机号是否已经注册
        public static final String CHECK_PHONE = "checkPhone";

        //用户采购组状态查询
        public static final String CHECK_PURCHASE = "checkPurchase";

        //编辑用户之前,查询是否有角色被停用
        public static final String ROLE_VALID = "rolevalid";

    }

    /**
     * 角色信息
     */
    public static final class Role {
        //根路径
        public static final String ROOT = "accredit";
        //角色信息分页查询
        public static final String ROLE_PAGE = "rolePage";
        //授权的用户使用名查询
        public static final String ROLE = "role";
        //角色用户授权入口
        public static final String ROLE_ACCREDITINFO = "roleAccreditInfo";
        //状态的修改
        public static final String UPDATE_STATE = "role/updateState";

    }

    /**
     * 资源（权限）
     */
    public static final class Jurisdiction {
        //根路径
        public static final String ROOT = "accredit";
        //资源分页分页查询---unused
        //public static final String ACCREDIT_PAGE = "jurisdictionPage";
        //全局资源资源查询
        public static final String JURISDICTION_WHOLE = "jurisdictionWhole";
        //渠道资源查询
        public static final String JURISDICTION_CHANNEL = "jurisdictionChannel";
        //状态的修改--unused
//        public static final String UPDATE_STATE = "jurisdiction/updateState";
        //查询资源加载树
        public static final String JURISDICTION_TREE = "jurisdictionTree";
        //新增资源
        public static final String JURISDICTION_SAVE = "jurisdictionSave";
        //编辑资源
        public static final String JURISDICTION_EDIT = "jurisdictionEdit";

    }

    /**
     * 采购管理--采购组管理
     */
    public static final class PurchaseGroup {
        //根路径
        public static final String ROOT = "purchase";
        //采购组分页查询
        public static final String PURCHASE_GROUP_PAGE = "purchaseGroupPage";
        //采购组名查询
        public static final String PURCHASE_GROUP = "purchaseGroup";
        //采购组列表
        public static final String PURCHASE_GROUP_LIST = "purchaseGroups";
        //根据采购组的编码查询
        public static final String PURCHASE_GROUP_CODE = "purchaseGroupCode";
        //状态的修改
        public static final String UPDATE_STATE = "purchaseGroup/updateState";
        //根据采购组id查询用户
        public static final String PURCHASE_GROUP_USER = "purchaseGroupUser";
        //根据采购组code查询改组的采购人员
        public static final String PURCHASE_GROUP_CODE_USER = "purchasePerson";

    }

    /**
     * 采购订单审核
     */
    public static final class PurchaseOrderAudit{
        //根路径
        public static final String ROOT = "purchase";
        //采购订单分页查询
        public static final String PURCHASE_ORDER_AUDIT_PAGE = "purchaseOrderAuditPage";

        public static final String PURCHASE_ORDER_AUDIT = "purchaseOrderAudit";

    }

    /**
     * 采购明细
     */
    public static final class PurchaseDetail{
        //根路径
        public static final String ROOT = "purchase";
        //
        public static final String  PURCHASE_DETAIL= "purchaseDetail";

    }


    /**
     * 采购订单管理
     */
    public static final class PurchaseOrder {
        //根路径
        public static final String ROOT = "purchase";
        //采购订单分页查询
        public static final String PURCHASE_ORDER_PAGE = "purchaseOrderPage";
        //采购订单
        public static final String PURCHASE_ORDER = "purchaseOrder";
        //采购单提交审核purchaseOrderAudit
        public static final String PURCHASE_ORDER_AUDIT = "purchaseOrderAudit";
        //根据采购组的编码查询
        //public static final String PURCHASE_GROUP_CODE = "purchaseGroupCode";
        //采购单状态的修改--删除 作废
        public static final String UPDATE_STATE = "purchaseOrder/updateState";
        //采购单状态的修改--冻结
        public static final String FREEZE = "purchaseOrder/freeze";
        //根据采购单id查询采购单相关的信息
        //public static final String PURCHASE_ORDER_USER = "purchaseOrderUser";
        //查询该渠道对应的供应商
        public static final String SUPPLIERS = "suppliers";
        //根据供应商的编码查询供应商的可卖商品（分页）
        public static final String SUPPLIERS_ITEMS = "suppliersItems";
        //根据供应商的编码查询供应商所有可卖商品
        public static final String SUPPLIERS_ALL_ITEMS = "suppliersAllItems";
    }

    /**
     * 七牛
     */
    public static final class QinNiu {

        /**
         * 用到七牛存储的系统功能模块
         */
        public static final class Module {
            //属性管理
            public static final String PROPERTY = "property";
            //供应商管理
            public static final String SUPPLY = "supply";
        }


        //根路径
        public static final String ROOT = "qinniu";
        //上传
        public static final String UPLOAD = "upload";
        //下载
        public static final String DOWNLOAD = "download";
        //删除
        public static final String DELETE = "delete";
        //缩略图
        public static final String THUMBNAIL = "thumbnail";
        //批量获取url
        public static final String URLS = "urls";

    }


    /**
     * 供应商管理
     */
    public static final class Supply {
        //根路径
        public static final String ROOT = "supplier";

        /**
         * 供应商
         */
        public static final class Supplier {
            //供应商分页查询路径
            public static final String SUPPLIER_PAGE = "supplierPage";
            //供应商列表查询路径
            public static final String SUPPLIER_LIST = "suppliers";
            //供应商路径
            public static final String SUPPLIER = "supplier";
            //启用/停用
            public static final String IS_VALID = "isValid";
            //供应商分页查询路径
            public static final String APPLY_SUPPLIER_PAGE = "applySupplierPage";
        }

        /**
         * 供应商分类
         */
        public static final class SupplierCategory {
            //供应商分类列表查询路径
            public static final String SUPPLIER_CATEGORY_LIST = "supplierCategorys";
            //供应商分类路径
            public static final String SUPPLIER_CATEGORY = "supplierCategory";
        }

        /**
         * 供应商品牌
         */
        public static final class SupplierBrand {
            //供应商品牌列表查询路径
            public static final String SUPPLIER_BRAND_LIST = "supplierBrands";
            //供应商品牌路径
            public static final String SUPPLIER_BRAND = "supplierBrand";
            //检查分类品牌启停用状态路径
            public static final String CHECK_CATEGORY_BRAND_VALID_STATUS = "checkCategoryBrandValidStatus";
        }

        /**
         * 供应商申请
         */
        public static final class SupplierApply {
            //供应商申请分页
            public static final String SUPPLIER_APPLY_PAGE = "supplierApplyPage";

            //供应商申请信息路径
            public static final String SUPPLIER_APPLY = "supplierApply";


        }

        /**
         * 供应商申请审批
         */
        public static final class SupplierApplyAudit {
            //供应商申请审批分页
            public static final String SUPPLIER_APPLY_AUDIT_PAGE = "supplierApplyAuditPage";

            //供应商申请审核信息路径
            public static final String SUPPLIER_APPLY_AUDIT = "supplierApplyAudit";

        }

        /**
         * 供应商渠道
         */
        public static final class SupplierChannel {

            //供应商渠道列表查询
            public static final String CHANNELS = "channels";
        }
    }

    /**
     * 商品
     */
    public static final class Goods {

        public static final String ROOT = "goods";

        //商品分页查询路径
        public static final String GOODS_PAGE = "goodsPage";
        //商品SKU分页查询路径
        public static final String GOODS_SKU_PAGE = "goodsSkuPage";
        //商品列表查询路径
        public static final String GOODS_LIST = "goodsList";
        //商品路径
        public static final String GOODS = "goods";
        //根据SPU编码查询商品路径
        public static final String GOODS_SPU_CODE = "goods/spuCode";
        //商品启用/停用
        public static final String IS_VALID = "isValid";
        //SKU启用/停用
        public static final String SKU_VALID = "skuValid";
        //商品分类属性
        public static final String ITEMS_CATEGORY_PROPERTY = "itemsCategoryProperty";

        //代发商品分页查询路径
        public static final String EXTERNAL_GOODS_PAGE = "externalGoodsPage";
        //代发商品分页查询路径2
        public static final String EXTERNAL_GOODS_PAGE_2 = "externalGoodsPage2";
        //代发商品
        public static final String EXTERNAL_ITEM_SKU = "externalItemSku";
        //代发商品
        public static final String EXTERNAL_ITEM_SKU_LIST = "externalItemSkus";
        //代发商品启用/停用
        public static final String EXTERNAL_ITEM__VALID = "externalItemsValid";

    }


    /**
     * 下拉列表
     */
    public static final class SelectList {
        //下拉列表根路径
        public static final String ROOT = "select";
        //根据类型编码查询
        public static final String SELECT_BY_TYPE_CODE = "selectByTypeCode";
        //是否启用
        public static final String VALID_LIST = "validList";
        //供应商性质
        public static final String SUPPLIER_NATURE = "supplierNature";
        //供应商类型
        public static final String SUPPLIER_TYPE = "supplierType";
        //仓库类型字典类型编码
        public static final String WAREHOUSE_TYPE = "warehouseType";
        //省市
        public static final String PROVINCE_CITY = "province";
        //角色类型字典类型编码
        public static final String ROLE_TYPE = "roleType";
        //用户类型字典类型编码
        public static final String USER_TYPE = "userType";
        //采购类型
        public static final String PURCHASE_TYPE = "purchaseType";
        //付款的方式
        public static final String PAY_TYPE = "payType";
        //贸易类型
        public static final String TRADE_TYPE = "tradeType";
        //币种类型
        public static final String CURRENCY_TYPE = "currencyType";
        //运费承担方
        public static final String TRANSORT_COSTS_TAKE = "transportCostsTake";
        //处理优先级
        public static final String HANDLER_PRIORITY = "handlerPriority";
        //国家
        public static final String COUNTRY = "country";
        //海外仓的是否支持清关
        public static final String IS_CUSTOM_CLEARANCE = "clearance";
        //查询采购订单状态
        public static final String PURCHASE_ORDER_STATUS = "purchaseOrderStatus";
        //供应商
        public static final String SUPPLIER = "oneAgentSupplier";
        //采购订单的审核状态
        public static final String PURCHASE_ORDER_AUDIT_STATUS="purchaseOrderAuditStatus";
    }

    /**
     * 序列号
     */
    public static final class Serial {
        public static final String ROOT = "serial";
        //供应商编码名称
        public static final String SUPPLIER_NAME = "GYS";
        //供应商编码长度
        public static final Integer SUPPLIER_LENGTH = 6;

        //仓库级订单
        public static final String WAREHOUSE_ORDER = "ORDER";

        //SPU名称
        public static final String SPU_NAME = "SPU";
        //SPU长度
        public static final Integer SPU_LENGTH = 5;
        //SKU名称
        public static final String SKU_NAME = "SP";
        //SKU长度
        public static final Integer SKU_LENGTH = 7;
        //在此模块维护的商品
        public static final String SKU_INNER = "0";
        //表示一件代发的商品
        public static final String SKU_OUTERER = "1";


        //序列号查询路径
        public static final String SERIAL = "serial";
    }

    /**
     * 京东订单
     */
    public static final class JingDongOrder {
        //订单接口根路径
        public static final String ROOT = "bill";
        //下单路径
        public static final String BILLORDER = "orders";
        //确认预占库存路径
        public static final String CONFIRM = "confirm";
        //取消未确认订单路径
        public static final String CANCEL = "cancel";
        //发起支付路径
        public static final String PAY = "pay";
        //订单反查路径
        public static final String ORDERSELECT = "orderId";
        //订单信息查询路径
        public static final String DETAIL = "detail";
        //查询配送信息路径
        public static final String TRACK = "track";

    }

    /**
     * 粮油订单
     */
    public static final class LiangYouOrder {
        //订单接口根路径
        public static final String ROOT = "LyBill";

        //非签约订单接口路径
        public static final String OUT_ORDER = "AddOutOrder";

        //签约订单接口路径
        public static final String TOUT_ORDER = "AddTOutOrder";

        //查询订单状态接口路径
        public static final String ORDER_STATUS = "OrderStatus";

    }

    /**
     * 一件代发供应商
     */
    public static final class ExternalSupplier {
        public static final String ROOT = "externalSupplier";
        //供应商sku更新通知
        public static final String SUPPLIER_SKU_UPDATE_NOTICE = "supplierSkuUpdateNotice";

    }

    /**
     * 授权
     */
    public static final class Authorization{
        //用户ID
        public static final String USER_ID = "userId";
        //用户授权列表
        public static final String ACL_USER_ACCREDIT_INFO = "aclUserAccreditInfo";

    }

    /**
     * 订单管理
     */
    public static final class Order {

        //京东供应商编码
        public final static String SUPPLIER_JD_CODE = "JD";
        //粮油供应商编码
        public final static String SUPPLIER_LY_CODE = "LY";

        public static final String ROOT = "order";
        //订单分页查询路径
        public static final String SHOP_ORDER_PAGE = "shopOrderPage";
        //订单列表查询路径
        public static final String SHOP_ORDER_LIST = "shopOrders";
        //订单路径
        public static final String SHOP_ORDER = "shopOrder";
        //仓库订单分页查询路径
        public static final String WAREHOUSE_ORDER_PAGE = "warehouseOrderPage";
        //仓库订单列表查询路径
        public static final String WAREHOUSE_ORDER_LIST = "warehouseOrders";
        //根据店铺订单编码查询仓库订单详情路径
        public static final String WAREHOUSE_ORDER_DETAIL = "warehouseOrder/warehouseOrderCode";
        //平台订单列表查询路径
        public static final String PLATFORM_ORDER_LIST = "platformOrders";
        //京东订单路径
        public static final String JING_DONG_ORDER = "jingDongOrder";
    }

}
