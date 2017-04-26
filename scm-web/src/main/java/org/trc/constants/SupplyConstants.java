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


}
