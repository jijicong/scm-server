package org.trc.constants;

/**
 * 积分平台常量
 */
public class SupplyConstants {

    /**
     * 符号
     */
    public static final class Symbol{
        public static final String COMMA = ",";//逗号
        public static final String MINUS = "-";//减号
    }


    public static final class Route {
        public static final class Home{
            public static final String ROOT = "home";
        }
        //--------------------------------------------
        public static final class Auth {
            public static final String ROOT = "auth";
            public static final String BLANKLIST = "blanklist";
        }

    }

    /**
     * 配置管理
     */
    public static final class Config{
        //根路径
        public static final String ROOT = "config";
        /**
         * 下拉列表
         */
        public static final class SelectList{
            //是否启用
            public static final String VALID_LIST = "validList";
            //海外仓的是否支持清关
            public static final String IS_CUSTOM_CLEARANCE="clearance";
        }
        /**
         * 字典类型
         */
        public static final class DictType{
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
        public static final class Dict{
            //字典分页查询路径
            public static final String DICT_PAGE = "dictPage";
            //字典列表查询路径
            public static final String DICT_LIST = "dicts";
            //字典路径
            public static final String DICT = "dict";
        }


    }

    public static final class Category{

        //根路径
        public static final String ROOT = "category";
        /**
         * 品牌管理
         */
        public static final class Brand{
            //品牌分页查询路径
            public static final String BRAND_PAGE = "brandPage";
            //品牌列表查询路径
            public static final String BRAND_LIST = "brands";
            //品牌路径
            public static final String BRAND = "brand";

            public static final String BRAND_STATE ="brand/state";
            //查询复数品牌资源路径
            public static final String BRAND_LIST_SEARCH = "brands/search";
        }



        /**
         * 属性管理
         */
        public static final class Property{
            //属性分页查询路径
            public static final String PROPERTY_PAGE = "propertyPage";
            //属性列表查询路径
            public static final String PROPERTY_LIST = "propertys";
            //属性路径
            public static final String PROPERTY = "property";

            public static final String PROPERTY_STATE ="property/state";
        }

        /**
         * 属性值管理
         */
        public static final class PropertyValue{
            //属性值列表查询路径
            public static final String PROPERTY_VALUE_LIST = "propertyValues";
            //属性值路径
            public static final String PROPERTY_VALUE = "propertyValue";

        }


        /**
         * 分类管理
         */
        public static final class  Classify{
            //查询树
            public static  final String CLASSIFY_TREE = "classifytree";
            //添加，修改分类
            public  static  final  String  CLASSIFY ="classify";
            //修改排序
            public static final  String CLASSIFY_SORT="classifysort";


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
    public static final class Warehouse{
        //根路径
        public static final String ROOT = "system";
        //仓库分页查询
        public static final String WAREHOUSE_PAGE = "warehousePage";
        //仓库名查询
        public static final String WAREHOUSE = "warehouse";
        //状态的修改
        public static final String UPDATE_STATE = "warehouse/updateState";
    }
    /**
     * 授权信息
     */
    public static final class UserAccreditInfo{
        //根路径
        public static final String ROOT = "accredit";
        //授权信息分页查询
        public static final String ACCREDIT_PAGE = "accreditInfoPage";
        //授权的用户使用名查询
        public static final String ACCREDIT = "accreditInfo";
        //状态的修改
        public static final String UPDATE_STATE = "accreditInfo/updateState";
    }
    /**
     * 角色信息
     */
    public static final class Role{
        //根路径
        public static final String ROOT = "accredit";
        //授权信息分页查询
        public static final String ROLE_PAGE = "rolePage";
        //授权的用户使用名查询
        public static final String ROLE = "role";
        //状态的修改
        public static final String UPDATE_STATE = "role/updateState";
    }
    /**
     * 资源（权限）
     */
    public static final class Jurisdiction{
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
     *七牛
     */
    public static final class QinNiu{

        /**
         * 用到七牛存储的系统功能模块
         */
        public static final class Module{
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
         * 供应商申请审批
         */
        public static final class SupplierApply {
            //供应商申请审批分页
            public static final String SUPPLIER_APPLY_PAGE = "supplierApplyPage";
        }

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
        public static final String SUPPLIER_TYPE= "supplierType";
        //仓库类型字典类型编码
        public static final String WAREHOUSE_TYPE="warehouseType";
        //省市
        public static final String PROVINCE_CITY="province";
        //角色类型字典类型编码
        public static final String ROLE_TYPE="roleType";
        //用户类型字典类型编码
        public static final String USER_TYPE="userType";
    }


}
