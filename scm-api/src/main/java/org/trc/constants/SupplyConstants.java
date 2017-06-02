package org.trc.constants;

/**
 * 积分平台常量
 */
public class SupplyConstants {

    /**
     * 返回值
     */
    public static final class Comment {
        public static final String STATUS = "status";//0-失败，1-成功

        public static final String MSG = "msg";//返回描述
    }


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

    }

    /**
     * 配置管理
     */
    public static final class Config {
        //根路径
        public static final String ROOT = "config";

        /**
         * 下拉列表
         */
        public static final class SelectList {
            //是否启用
            public static final String VALID_LIST = "validList";
            //海外仓的是否支持清关
            public static final String IS_CUSTOM_CLEARANCE = "clearance";
        }

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

//            //已经关联的分类品牌
//            public static final String CATEGORY_BRAND_PAGE = "categoryBrandPage";
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

            //修改排序
            public static final String CATEGORY_SORT = "sort";

            //修改状态
            public static final String UPDATE_STATE = "category/updateState";

            //重名验证
            public static final String CATEGORY_CHECK = "check";

            //查询分类
            public static final String CATEGORY_QUERY = "query";
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
        public static final String WAREHOUSE_VALID = "wharehouseValid";
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
        public static final String CHANNEL = "channel";

        //查询拥有采购员角色的用户
        public static final String PURCHASE = "purchase";

        //查询选择用户对应角色
        public static final String ROLE = "rolelist";

        //新增角色
        public static final String SAVE_ACCREDIT = "saveaccredit";

        //用户修改
        public static final String UPDATE_ACCREDIT = "updateaccredit";

        //查询用户名是否已被使用
        public static final String CHECK = "check";

    }

    /**
     * 角色信息
     */
    public static final class Role {
        //根路径
        public static final String ROOT = "accredit";
        //授权信息分页查询
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
        //public static final String UPDATE_STATE = "jurisdiction/updateState";
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
        public static final String PURCHASE_GROUP_LIST = "purchaseGroupList";
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
     * 采购订单管理
     */
    public static final class PurchaseOrder {
        //根路径
        public static final String ROOT = "purchase";
        //采购订单分页查询
        public static final String PURCHASE_ORDER_PAGE = "purchaseOrderPage";
        //采购订单
        public static final String PURCHASE_ORDER = "purchaseOrder";
        //根据采购组的编码查询
        //public static final String PURCHASE_GROUP_CODE = "purchaseGroupCode";
        //采购单状态的修改
        public static final String UPDATE_STATE = "purchaseOrder/updateState";
        //根据采购单id查询采购单相关的信息
        //public static final String PURCHASE_ORDER_USER = "purchaseOrderUser";
        //查询该渠道对应的供应商S
        public static final String SUPPLIERS = "suppliers";
        //根据供应商的编码查询供应商的可卖商品
        public static final String SUPPLIERS_ITEMS="suppliersItems";
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
            //供应商分类列表查询路径
            public static final String SUPPLIER_BRAND_LIST = "supplierBrands";
            //供应商分类路径
            public static final String SUPPLIER_BRAND = "supplierBrand";
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

    }


    /**
     * 下拉列表
     */
    public static final class SelectList {
        //下拉列表根路径
        public static final String ROOT = "select";
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

        //SPU名称
        public static final String SPU_NAME = "SPU";
        //SPU长度
        public static final Integer SPU_LENGTH = 5;
        //SKU名称
        public static final String SKU_NAME = "SKU";
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
        public static final String  DETAIL= "detail";
        //查询配送信息路径
        public static final String  TRACK= "track";

    }


}
