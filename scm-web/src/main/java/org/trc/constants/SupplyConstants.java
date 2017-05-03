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
        public static final String ROOT = "catagory";
        /**
         * 品牌管理
         */
        public static final class Brand{
            //字典分页查询路径
            public static final String Brand_PAGE = "brandPage";
            //字典列表查询路径
            public static final String Brand_LIST = "brands";
            //字典路径
            public static final String Brand = "brand";

            public static final String Brand_Status="brand/state";
        }

    }
    /**
     * 系统管理：仓库，渠道，授权
     */
    public static final class System {
        //根路径
        public static final String ROOT = "system";

        /**
         * 渠道管理
         */
        public static final class Channel {
            //渠道分页查询
            public static final String CHANNEL_PAGE = "channelPage";
            //渠道名查询
            public static final String CHANNEL = "channel";
            //状态的修改
            public static final String UPDATE_STATE = "channel/updateState";
        }
    }
    /**
     *七牛
     */
    public static final class QinNiu{

        //根路径
        public static final String ROOT = "qinniu";
        //供应链测试token
        public static final String TEST_TOKEN = "testToken";
        //供应链正式token
        public static final String PRODUCT_TOKEN = "productToken";
    }


}
