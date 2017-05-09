package org.trc.constants;

/**
 * 积分平台常量
 */
public class SupplyConstants {

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
            //字典分页查询路径
            public static final String BRAND_PAGE = "brandPage";
            //字典列表查询路径
            public static final String BRAND_LIST = "brands";
            //字典路径
            public static final String BRAND = "brand";

            public static final String BRAND_STATE ="brand/state";
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
        //省市
        public static final String PROVINCE_CITY="province";
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
    }



}
