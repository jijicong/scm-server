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
        public static final String SEMICOLON = ";";//分号
        public static final String XIE_GANG = "/";//斜杠
        public static final String AND = "&";//与


        /**
         * 等号
         */
        public static final String EQUAL = "=";
    }

    public static final class WarehouseConstant {
        public static final String OWNER_NAME = "浙江小泰电子商务有限公司";

        public static final String CHANNEL_CODE = "TRMALL";
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

        public static final String SKU_INFORMATION = "skuInformation";//获取自采sku信息

        public static final String EXTERNAL_SKU_INFORMATION = "externalSkuInformation";//批量获取代发sku信息

        public static final String ORDER_PROCESSING = "orderProcessing";//订单处理

        public static final String SKURELATION_UPDATE = "skurelation/update";//sku关联信息变更

        public static final String ITEM_LIST = "itemList";//获取自采商品列表

        public static final String SKUS_LIST = "skusList";//获取自采商品sku列表

        public static final String EXTERNALITEMSKU_LIST = "externalItemSkuList";//获取自采商品skus列表

        public static final String JD_LOGISTICS = "jdLogistics";//获取京东物流信息

        public static final String SUPPLIER_LIST = "supplierList";//供应商分页查询

        public static final String RETURN_WAREHOUSE= "returnWarehouseQuery";//供应商分页查询
        
        public static final String AFTER_SALE_CREATE= "afterSaleCreate";//创建售后单接口
        
        public static final String CANCEL_AFTER_SALE_ORDER= "cancelAfterSaleOrder";//取消售后单接口
        
        public static final String SUBMIT_WAYBILL= "submitWaybill";//提交物流单号接口
    }

    /**
     * 对泰然城开放接口
     */
    public static final class Qimen {
        /**
         * 根路径
         */
        public static final String QI_MEN = "Qimen";

        /**
         * 接收奇门回调
         */
        public static final String QIMEN_CALLBACK = "QimenCallback";

        /**
         * 奇门接口返回成功标志
         */
        public static final String QIMEN_RESPONSE_SUCCESS_FLAG = "success";

        /**
         * 奇门接口返回失败标志
         */
        public static final String QIMEN_RESPONSE_FAILURE_FLAG = "failure";


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
            //联想查询
            public static final String ASSOCIATION_SEARCH = "brands/associationSearch";

            /**
             * 品牌名称唯一校验
             */
            public static final String CHECK_NAME = "brand/checkName";
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

            /**
             * 属性值名称唯一校验
             */
            public static final String CHECK_NAME = "propertyValue/checkValueName";
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

            /**
             * 分类名称唯一性校验
             */
            public static final String CATEGORY_CHECK_NAME = "checkCategoryName";

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
        //销售渠道分页查询
        public static final String SALES_CHANNEL_PAGE = "salesChannelPage";
        //销售渠道
        public static final String SALES_CHANNEL = "salesChannel";
        /**
         * 根据渠道ID查询关联的业务线
         */
        public static final String CHANNEL_ID = "channelId";
        /**
         * 编辑时回写的数据
         */
        public static final String CHANNEL_ID_SELL_CHANNEL = "channelInSellChannel";
        /**
         * 查询所有的销售渠道
         */
        public static final String SELL_CHANNEL_LIST = "channelList";
        //查询业务线相关销售渠道
        public static final String YWX_SELL_CHANNEL_LIST = "ywxSellChannelList";
    }

    /**
     * 销售渠道管理
     */
    public static final class SellChannel {
        //根路径
        public static final String ROOT = "system";
        /**
         * 销售渠道分页查询
         */
        public static final String SELL_CHANNEL_PAGE = "sellChannelPage";
        /**
         * 销售渠道列表
         */
        public static final String SELL_CHANNEL_LIST = "sellChannelList";
        /**
         * 销售渠道名称查询
         */
        public static final String SELL_CHANNEL_NAME = "sellChannelName";
        /**
         * 销售渠道修改
         */
        public static final String SELL_CHANNEL_UPDATE = "update/sellChannel";
        /**
         * 销售渠道新增
         */
        public static final String SELL_CHANNEL_SAVE = "save/sellChannel";
        /**
         * 根据主键查询销售渠道
         */
        public static final String SELL_CHANNEL = "sellChannel";
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
        //仓库信息配置
        public static final String WAREHOUSE_CONFIG = "warehouseConfig";
    }

    public static final class LogisticsCorporation {
        //根路径
        public static final String ROOT = "system";
        //分页查询
        public static final String LOGISTICS_CORPORATION_PAGE = "logisticsCorporationPage";
        //物流公司
        public static final String LOGISTICS_CORPORATION = "logisticsCorporation";
        //更新物流公司状态
        public static final String UPDATE_STATE = "updateState";
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

        //根据手机号查询用户名称
        public static final String NAME = "getName";

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

        public static final String ROLE_MODULE = "roleModule";
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
        //全局资源资源查询(模块)
        public static final String JURISDICTION_WHOLE_MODULE = "jurisdictionWholeModule";
        //渠道资源查询
        public static final String JURISDICTION_CHANNEL = "jurisdictionChannel";
        //渠道资源查询(模块)
        public static final String JURISDICTION_CHANNEL_MODULE = "jurisdictionChannelModule";
        //状态的修改--unused
//        public static final String UPDATE_STATE = "jurisdiction/updateState";
        //查询资源加载树
        public static final String JURISDICTION_TREE = "jurisdictionTree";
        //新增资源
        public static final String JURISDICTION_SAVE = "jurisdictionSave";
        //编辑资源
        public static final String JURISDICTION_EDIT = "jurisdictionEdit";
        //页面资源
        public static final String JURISDICTION_HTML = "jurisdictionHtml";


        //页面资源
        public static final String HTML = "html";


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
        //采购组员查询
        public static final String PURCHASE_GROUP_USER_NEW = "purchaseGroupUserNew";

    }

    /**
     * 采购订单审核
     */
    public static final class PurchaseOrderAudit {
        //根路径
        public static final String ROOT = "purchase";
        //采购订单分页查询
        public static final String PURCHASE_ORDER_AUDIT_PAGE = "purchaseOrderAuditPage";

        public static final String PURCHASE_ORDER_AUDIT = "purchaseOrderAudit";

    }

    /**
     * 采购明细
     */
    public static final class PurchaseDetail {
        //根路径
        public static final String ROOT = "purchase";
        //
        public static final String PURCHASE_DETAIL = "purchaseDetail";
        //根据采购单编码查询采购信息列表
        public static final String PURCHASE_DETAILE_BY_CODE = "purchaseDetailByCode";
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
        //采购单的入库-作废的状态
        public static final String WAREHOUSE_UPDATE = "warahouseAdvice/cancellation";
        //采购单状态的修改--冻结
        public static final String FREEZE = "purchaseOrder/freeze";
        public static final String WAREHOUSE_ADVICE = "purchaseOrder/warahouseAdvice";
        //根据采购单id查询采购单相关的信息
        //public static final String PURCHASE_ORDER_USER = "purchaseOrderUser";
        //查询该渠道对应的供应商
        public static final String SUPPLIERS = "suppliers";
        //根据供应商的编码查询供应商的可卖商品（分页）
        public static final String SUPPLIERS_ITEMS = "suppliersItems";
        //根据供应商的编码查询供应商所有可卖商品
        public static final String SUPPLIERS_ALL_ITEMS = "suppliersAllItems";
        //根据采购单编码查询采购单
        public static final String PURCHASE_ORDER_BY_CODE = "purchaseOrderByCode";
        //根据供应商编码查询该对应上对应的品牌
        public static final String SUPPLIER_BRAND = "supplierBrand";
        //查询该业务线对应的仓库
        public static final String WAREHOUSE = "warehouse";
        //查询商品路径
        public static final String PURCHASE_ORDER_ITEM = "purchaseOrderItem";
    }

    /**
     * 入库通知
     */
    public static final class WarehouseNotice {
        //根路径
        public static final String ROOT = "warehouseNotice";
        //入库通知的分页路径
        public static final String WAREHOUSE_NOTICE_PAGE = "warehouseNoticePage";
        //入库通知的点击操作
        public static final String RECEIPT_ADVICE = "receiptAdvice";
        //入库通知单详情页的入库通知操作
        public static final String RECEIPT_ADVICE_INFO = "receiptAdviceInfo";
        //入库通知的信息查询
        public static final String WAERHOUSE_NOTICE_INFO = "warehouseNoticeInfo";
        //入库通知明细查询
        public static final String WAREHOUSE_NOTICE_DETAIL = "warehouseNoticeDetail";
        //入库通知单的取消收货
        public static final String CANCEL = "cancel";

    }

    /**
     * 调拨单
     */
    public static final class AllocateOrder {
//    	//根路径
//    	public static final String ROOT = "allocateOrder";

    }

    /**
     * 七牛
     */
    public static final class QinNiu {

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

        /**
         * 用到七牛存储的系统功能模块
         */
        public static final class Module {
            //属性管理
            public static final String PROPERTY = "property";
            //供应商管理
            public static final String SUPPLY = "supply";
        }

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
            //供应商性质：采购
            public static final String SUPPLIER_PURCHASE = "purchase";
            //供应商性质：一件代发
            public static final String SUPPLIER_ONE_AGENT_SELLING = "oneAgentSelling";
            //供应商类型：国内供应商
            public static final String INTERNAL_SUPPLIER = "internalSupplier";
            //供应商类型：海外供应商
            public static final String OVERSEAS_SUPPLIER = "overseasSupplier";
            //证件类型:普通三证
            public static final String NORMAL_THREE_CERTIFICATE = "normalThreeCertificate";
            //证件类型:多证合一
            public static final String MULTI_CERTIFICATE_UNION = "multiCertificateUnion";


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

            //供应商修改申请信息路径
            public static final String SUPPLIER_APPLY = "supplierApply";
            //
            public static final String SUPPLIER_STATE = "supplierApply/state";

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

        //自采商品SKU前缀
        public static final String SKU_PREFIX = "SP0";
        //代发商品SKU前缀
        public static final String EXTERNAL_SKU_PREFIX = "SP1";

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
        //自采商品导出
        public static final String ITEMS_EXPORT = "itemsExport";


        //代发商品分页查询路径
        public static final String EXTERNAL_GOODS_PAGE = "externalGoodsPage";
        //代发商品分页查询路径2
        public static final String EXTERNAL_GOODS_PAGE_2 = "externalGoodsPage2";
        //代发商品
        public static final String EXTERNAL_ITEM_SKU = "externalItemSku";
        //代发商品导出
        public static final String EXTERNAL_ITEM_EXPORT = "externalItemExport";
        //代发商品
        public static final String EXTERNAL_ITEM_SKU_LIST = "externalItemSkus";
        //代发商品启用/停用
        public static final String EXTERNAL_ITEM__VALID = "externalItemsValid";
        //检查属性启停用状态
        public static final String CHECK_PROPERTY_STATUS = "checkPropetyStatus";
        //查询供应商列表
        public static final String SUPPLIERS_LIST = "suppliersList";
        //校验条形码唯一
        public static final String CHECK_BARCODE_ONLY = "checkBarcodeOnly";
        //sku信息实时校验
        public static final String SKU_INFO_BAR = "skuInfoBarCode";
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
        //查询入库通知单的列表页面
        public static final String WAREHOSUE_NOTICE_STATUS = "warehouseNoticeStatus";
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
        //查询采购单入库状态
        public static final String PURCHASE_ORDER_RECEIVE_STATUS = "purchaseOrderReceiveStatus";
        //供应商
        public static final String SUPPLIER = "oneAgentSupplier";
        //采购订单的审核状态
        public static final String PURCHASE_ORDER_AUDIT_STATUS = "purchaseOrderAuditStatus";
        //查询发货通知的状态
        public static final String OUTBOUND_ORDER_STATUS = "outboundOrderStatus";
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
        //异常订单
        public static final String EXCEPTION_ORDER = "CDYC";
        //异常订单编码长度
        public static final Integer EXCEPTION_ORDER_LENGTH = 5;
        //发货通知单
        public static final String OUTBOUND_ORDER = "ZYFHTZ";
        //发货通知单编码长度
        public static final Integer OUTBOUND_ORDER_LENGTH = 5;
        //仓库订单
        public static final String WAREHOUSE_ORDER_CODE = "CKDD";
        //仓库订单编码长度
        public static final Integer WAREHOUSE_ORDER_CODE_LENGTH = 7;

        //调拨单前缀
        public static final String ALLOCATE_ORDER_CODE = "DBD";
        //调拨单编码长度
        public static final Integer ALLOCATE_ORDER_LENGTH = 5;
        //调拨出库单前缀
        public static final String ALLOCATE_ORDER_OUT_CODE = "DBCKTZ";
        //调拨单出库单编码长度
        public static final Integer ALLOCATE_ORDER_OUT_LENGTH = 5;
        //调拨入库单前缀
        public static final String ALLOCATE_ORDER_IN_CODE = "DBRKTZ";
        //调拨单入库单编码长度
        public static final Integer ALLOCATE_ORDER_IN_LENGTH = 5;

        //导入订单前缀
        public static final String IMPORT_ORDER_CODE = "DRDD";
        //导入订单长度
        public static final Integer IMPORT_ORDER_LENGTH = 5;

        //导入订单前缀
        public static final String SYSTEM_ORDER_CODE = "ZY";
        //导入订单长度
        public static final Integer SYSTEM_ORDER_LENGTH = 8;
        
        //售后单编码长度
        public static final Integer AFTER_SALE_LENGTH=5;
        //售后单编码前缀
        public static final String AFTER_SALE_CODE="AS";

        //退货入库单编码长度
        public static final Integer WAREHOUSE_NOTICE_LENGTH=5;
        //退货入库单编码前缀
        public static final String WAREHOUSE_NOTICE_CODE="THRKTZ";
        
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
        //查询余额对账明细路径
        public static final String CHECK_BALANCE = "check";
        //查询所有业务类型路径
        public static final String GET_ALL_TREAD_TYPE = "treadType";
        //获取京东账户余额信息
        public static final String GET_BALANCE_INFO = "balance";
        //获取订单对比明细
        public static final String ORDER_DETAIL_PAGE = "orderDetailPage";
        //获取余额明细
        public static final String BALANCE_DETAIL_PAGE = "balanceDetailPage";
        //导出余额明细
        public static final String EXPORT_BALANCE_DETAIL = "exportBalanceDetail";
        //导出订单明细
        public static final String EXPORT_ORDER_DETAIL = "exportOrderDetail";
        //订单操作
        public static final String OPERATE_ORDER = "operate";
        //订单操作查询
        public static final String GET_OPERATE = "getOperate";
        //统计余额明细
        public static final String GET_STATISTIC_BALANCE = "statisticsBalance";
        //针对某天账单进行补对
        public static final String COMPLETION_ORDER = "completionOrder";

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

        //查询粮油代发报表导出路径
        public static final String LY_OREDER_PAGE = "LyOrderPage";

        //查询粮油代发报表导出路径
        public static final String EXPORT_ORDER = "ExportOrder";

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
    public static final class Authorization {
        //用户ID
        public static final String USER_ID = "userId";
        //用户授权列表
        public static final String ACL_USER_ACCREDIT_INFO = "aclUserAccreditInfo";
        /**
         * 业务线ID
         */
        public static final String CHANNEL_CODE = "channelCode";

    }

    public static final class WarehouseInfo {
        public static final String ROOT = "warehouseInfo";

        public static final String SAVE_WAREHOUSE_INFO = "saveWarehouseInfo";

        public static final String SELECT_WAREHOUSE_NAME_NOT_LOCATION = "selectWarehouseNameNotLocation";

        public static final String SELECT_WAREHOUSE_NAME = "selectWarehouseName";

        public static final String SELECT_RETURN_WAREHOUSE_NAME = "selectReturnWarehouseName";

        public static final String WAREHOUSE_INFO_PAGE = "warehouseInfoPage";

        public static final String OWNER_INFO = "ownerInfo";

        public static final String DELETE_WAREHOUSE_INFO = "deleteWarehouse";

        //仓库商品信息分页查询路径
        public static final String WAREHOUSE_ITEM_INFO_PAGE = "warehouseItemInfoPage";

        //仓库商品信息路径
        public static final String WAREHOUSE_ITEM_INFO = "warehouseItemInfo";

        //通知状态
        public static final String NOTICE_STATUS = "noticeStatus";

        //仓库商品信息导出路径
        public static final String ITEMS_EXPORT = "itemsExport";

        //新增商品路径
        public static final String SAVE_ITEMS = "saveItems";

        //新增商品信息分页查询路径
        public static final String ITEMS_PAGE = "itemsPage";

        //仓库商品信息通知奇门同步
        public static final String WAREHOUSE_ITEM_NOTICE_QIMEN = "warehouseItemNoticeQimen";

        public static final String EXCEPTION_EXCEL = "exceptionExcel";
    }

    /**
     * 仓库匹配优先级
     */
    public static final class WarehousePriority {
        public static final String ROOT = "warehousePriority";
        //仓库匹配优先级列表查询
        public static final String WAREHOUSE_PRIORITY_LIST = "warehousePriorityList";
        //可用仓库列表查询
        public static final String WAREHOUSE_LIST = "warehouseList";
        //保存仓库匹配优先级
        public static final String WAREHOUSE_PRIORITY = "warehousePriority";
    }

    /**
     * 出库通知单
     */
    public static final class OutboundOrder {

        public static final String ROOT = "outOrder";

        public static final String OUTBOUND_ORDER_PAGE = "outboundOrderPage";

        public static final String WAREHOUSE_LIST = "warehouseList";

        public static final String WAREHOUSE_LIST_ALL = "warehouseListAll";

        //发货通知单创建路径
        public static final String DELIVERY_ORDER_CREATE = "deliveryOrderCreate";

        /**
         * 取消订单
         */
        public static final String ORDER_CANCEL = "orderCancel";

        /**
         * 取消关闭
         */
        public static final String CANCEL_CLOSE = "cancelClose";

        /**
         * 关闭
         */
        public static final String CLOSE = "close";

        /**
         * 获取发货通知单详情
         */
        public static final String OUTBOUND_ORDER_DETAIL = "outboundOrderDetail";

        /**
         * 更新收货信息
         */
        public static final String UPDATE_RECEIVER_INFO = "updateReceiverInfo";
    }

    public static final class AllocateOutOrder {
        public static final String ROOT = "allocateOutOrder";

        public static final String ALLOCATE_OUT_ORDER_PAGE = "allocateOutOrderPage";

        public static final String CLOSE = "close";

        public static final String CANCEL_CLOSE = "cancelClose";

        public static final String ALLOCATE_OUT_ORDER = "allocateOutOrder";

        public static final String ALLOCATE_ORDER_OUT_NOTICE = "allocateOrderOutNotice";

        public static final String ALLOCATE_ORDER_OUT_CANCEL = "allocateOrderOutCancel";

        public static final String NOTICE_SEND_GOODS = "noticeSendGoods";
    }

    /**
     * 调拨入库单
     */
    public static final class AllocateInOrder {
        public static final String ROOT = "allocateInOrder";
        //调拨入库单分页
        public static final String ORDER_PAGE = "orderPage";
        //调拨入库单详情查询
        public static final String ORDER_DETAIL = "orderDetail";
        //调拨入库单取消
        public static final String ORDER_CANCEL = "orderCancel";
        //调拨入库单关闭
        public static final String ORDER_CLOSE = "orderClose";
        //调拨入库单通知收货
        public static final String NOTICE_RECIVE_GOODS = "noticeReciveGoods";

    }


    public static final class AclWmsUser {

        public static final String ROOT = "aclWmsUser";

        /**
         * wms用户分页查询
         */
        public static final String ACL_WMS_USER_PAGE = "aclWmsUserPage";

        /**
         * wms用户编辑
         */
        public static final String ACL_WMS_USER_UPDATE = "aclWmsUserUpdate";

        /**
         * wms用户查询
         */
        public static final String ACL_WMS_USER_QUERY = "aclWmsUserQuery";

        /**
         * wms用户状态修改
         */
        public static final String ACL_WMS_USER_UPDATE_STATE = "aclWmsUserUpdate/state";

        /**
         * wms用户新增
         */
        public static final String ACL_WMS_USER_SAVE = "aclWmsUserSave";

        /***
         * 查询自营仓库
         */
        public static final String ACL_WMS_USER_WAREHOUSE = "aclWmsUserWarehouse";

        /**
         * 查询WMS资源
         */
        public static final String ACL_WMS_USER_RESOURCE = "aclWmsUserResource";

        /**
         * 手机号校验
         */
        public static final String ACL_WMS_USER_PHONE = "aclWmsUserPhone";

        /**
         * 查询wms权限树
         */
        public static final String ACL_WMS_TREE = "aclWmsTree";

        /**
         * 查询wms新增
         */
        public static final String ACL_WMS_SAVE = "aclWmsSave";

        /**
         * 查询wms编辑
         */
        public static final String ACL_WMS_UPDATE = "aclWmsUpdate";

    }

    /**
     * 订单管理
     */
    public static final class Order {

        //京东供应商编码
        public final static String SUPPLIER_JD_CODE = "JD";
        //粮油供应商编码
        public final static String SUPPLIER_LY_CODE = "LY";
        //京东物流公司
        public final static String SUPPLIER_JD_LOGISTICS_COMPANY = "京东物流";
        //京东物流公司
        public final static String SUPPLIER_JD_LOGISTICS_COMPANY2 = "京东快递";

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
        //订单取消
        public static final String ORDER_CANCEL = "orderCancel";
        //订单导出
        public static final String EXPORT_ORDER = "exportOrder";
        //供应商订单导出
        public static final String EXPORT_SUPPLIER_ORDER = "exportSupplierOrder";
        //拆单异常订单分页查询路径
        public static final String EXCEPTION_ORDER_PAGE = "exceptionOrderPage";
        //根据拆单异常订单编码查询拆单异常订单详情路径
        public static final String EXCEPTION_ORDER_DETAIL = "exceptionOrder/exceptionOrderCode";

        //导入订单路径
        public static final String ORDER_IMPORT = "orderImport";
        //下载错误订单路径
        public static final String DOWNLOAD_ERROR_ORDER = "downloadErrorOrder";
    }

    /**
     * 异常订单管理
     */
    public static final class ExceptionOrder {

        //找不到满足发货数量的仓库
        public final static String ALL_WAREHOUSE_STOCK_LESS = "找不到满足发货数量的仓库";
        //供应商反馈库存不足
        public final static String SUPPLIER_STOCK_LESS = "供应商反馈库存不足";
        //指定仓库“XX仓库名称”库存不足
        public final static String WAREHOUSE_STOCK_LESS = "指定仓库“%s”库存不足";
        //同一仓库/供应商中存在库存不足的sku
        public final static String SKU_STOCK_LESS_OF_WAREHOUSE = "同一仓库/供应商中存在库存不足的sku";
    }


    /**
     * api
     */
    public static final class Api {
        public final static String Root = "api";
        //接收代发商品变更通知
        public final static String EXTERNAL_ITEM_UPDATE = "externalItemUpdate";
        //接收供应商订单取消通知
        public final static String SUPPLIER_ORDER_CANCEL = "supplierOrderCancel";
        /**
         * 查询当前用户所属业务线
         */
        public static final String JURISDICTION_USER_CHANNEL = "jurisdictionUserChannel";
        /**
         * 确认用户业务线
         */
        public static final String CONFIRM_USER_CHANNEL = "confirmUser";

        /**
         * 清除session
         */
        public static final String CLEAR_SESSION = "clearSession";

        //京东订单拆分子订单通知
        public final static String JD_ORDER_SPLIT_NOTICE = "jdOrderSplitNotice";
        //订单下单结果通知
        public final static String ORDER_SUBMIT_RESULT_NOTICE = "orderSubmitResultNotice";

    }

    /**
     * 操作日志
     */
    public static final class LogInfo {
        //日志分页查询路径
        public static final String LOG_INFO_PAGE = "logInfoPage";
    }

    /**
     * 直辖市
     */
    public static final class DirectGovernedCity {

        public final static String BEI_JING = "北京";

        public final static String SHANG_HAI = "上海";

        public final static String TIAN_JING = "天津";

        public final static String CHONG_QING = "重庆";

        public final static String[] DIRECT_CITY = {BEI_JING, SHANG_HAI, TIAN_JING, CHONG_QING};

    }

    /**
     * 元数据
     */
    public static final class Metadata {
        public final static String ROOT = "metadata";
        //字典
        public final static String DICT = "dict";
        //地址
        public final static String ADDRESS = "address";
        //京东地址
        public final static String JD_ADDRESS = "jdAddress";
        //京东地址更新
        public final static String JD_ADDRESS_UPDATE = "jdAddressUpdate";

    }

    /**
     * 系统配置类型
     */
    public static final class SystemConfigType {
        //对接供应链渠道类型编码
        public final static String CHANNEL = "channel";
    }

    /**
     * 订单来源平台编码
     */
    public static final class SourcePlatformCodeType {
        //奇门接口订单来源编码
        public final static String OTHER = "OTHER";
    }

    /**
     * 奇门物流公司编码
     */
    public static final class QimenLogisticsCompanyCode {
        //圆通
        public final static String YTO = "YTO";
    }

    /**
     * 缓存key
     */
    public static final class Cache {
        //供应链用户
        public final static String SCM_USER = "scm_user";
        //供应链资源
        public final static String SCM_RESOURCE = "scm_resource";
        //店铺订单
        public final static String SHOP_ORDER = "shopOrder";
        //供应商订单
        public final static String SUPPLIER_ORDER = "supplierOrder";
        //发货通知单
        public final static String OUTBOUND_ORDER = "outboundOrder";

        //字典
        public final static String DICT = "dict";
        //品牌
        public final static String BRAND = "brand";
        //属性
        public final static String PROPERTY = "property";
        //分类
        public final static String CATEGORY = "category";

        //自采商品
        public final static String GOODS = "goods";
        //代发商品
        public final static String OUT_GOODS = "outGoods";
        //商品查询
        public final static String GOODS_QUERY = "goodsQuery";
        //代发商品查询
        public final static String OUT_GOODS_QUERY = "outGoodsQuery";

        //供应商
        public final static String SUPPLIER = "supplier";

        //采购组
        public final static String PURCHASE_GROUP = "purchase_group";
        //采购单
        public final static String PURCHASE_ORDER = "purchase_order";
        /**
         * 采购退货单
         */
        public final static String PURCHASE_OUTBOUND_ORDER = "purchaseOutboundOrder";
        //采购单审核
        public final static String PURCHASE_ORDER_AUDIT = "purchase_order_audit";
        //入库通知
        public final static String WAREHOUSE_NOTICE = "warehouse_notice";

        //仓库
        public final static String WAREHOUSE = "warehouse";
        //渠道
        public final static String CHANNEL = "channel";
        //销售渠道
        public final static String SELL_CHANNEL = "sell_channel";

        //地址
        public final static String ADDRESS = "address";
        //京东地址
        public final static String JD_ADDRESS = "jd_address";

        public final static String WAREHOUSE_ITEM = "warehouse_item";

        //调拨单
        public final static String ALLOCATE_ORDER = "allocate_order";

        //调拨发货单
        public final static String ALLOCATE_OUT_ORDER = "allocate_out_order";

        //商品组
        public final static String ITEM_GROUP = "item_group";

    }

    /**
     * 商品组
     */
    public static final class ItemGroupConstants {

        public final static String ROOT = "itemGroup";
        //商品组查询分页
        public final static String ITEM_GROUP_PAGE = "itemGroupPage";
        //商品组详情查询
        public final static String ITEM_GROUP_DETAIL_QUERY = "itemGroupDetailQuery";
        //商品组员信息列表查询
        public final static String ITEM_GROUP_USERS_QUERY = "itemGroupUsersQuery";
        //商品组详情编辑
        public final static String ITEM_GROUP_EDIT = "itemGroupEdit";
        //商品组新增
        public final static String ITEM_GROUP_SAVE = "itemGroupSave";
        //商品组新增
        public final static String ITEM_GROUP_ISVALID = "isValid";
    }
    
    /**
     * 售后单
     * @author hzwjie
     *
     */
    public static final class AfterSaleOrder {
    	
    	public final static String AFTER_SALE_ORDER = "afterSaleOrder";
    	
    	public final static String AFTER_SALE_ORDER_DETAIL = "afterSaleOrderDetail";
        public final static String AFTER_SALE_ORDER_DETAIL_QUERY = "orderDetail";
    	
    	//新增售后单
    	public final static String ADD = "add";
    	//根据订单号查询子订单
    	public final static String SELECT_ORDER_ITEM="selectOrderItem";

        //售后单卡片
        public final static String AFTER_SALE_TAB="afterSaleOrderTab";
    }
    
}
