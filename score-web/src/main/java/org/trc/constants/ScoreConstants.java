package org.trc.constants;

/**
 * 积分平台常量
 */
public class ScoreConstants {
    //--清理
    public static final class PageParam {
        public static final String DEFAULT_PAGEINDEX_STR = "1";
        public static final String DEFAULT_PAGESIZE_STR = "10";
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

        public static final class Restrictions {
            public static final String ROOT = "restrictions";
        }

        public static final class Exchange {
            public static final String ROOT = "exchange";
            public static final String CONVERTER = "/converter";
            public static final String IN = "/in";
            public static final String OUT = "/out";
        }

        public static final class Consumption {
            public static final String ROOT = "consumption";
            public static final String RULE = "/rule";
        }

        public static final class Flow {
            public static final String ROOT = "flow";
        }

        public static final class Order{
            public static final String ROOT = "order";
            /**
             * 热门排行
             */
            public static final String HOT = "hot";
        }

        public static final class Goods {
            public static final String ROOT = "goods";
            public static final String HOT = "/hot";
            public static final String VALUE = "/value";
            public static final String CATEGORY = "/category";
            public static final String DETAIL = "/detail";
        }

        public static final class Area {
            public static final String ROOT = "area";
        }


        public static final class Address{
            public static final String ROOT = "address";
            public static final String ADD = "/add";
            public static final String DEFAULT = "/default";
        }

        public static final class Mall{
            public static final String ROOT = "mall";
        }
        public static final class Virtual{
            public static final String ROOT = "virtual";
        }
        public static final class Barrage {
            public static final String ROOT = "barrage";
            public static final String LIST = "/list";
        }
    }


}
