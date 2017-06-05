package org.trc.resource.api;


/**
 * 返回值
 * Created by hzdzf on 2017/5/26.
 */
public class Constant {

    //返回信息
    public static final class Return {
        public static final String STATUS = "status";//0-失败，1-成功

        public static final String MSG = "msg";//返回描述
    }

    public static final class Commom {

        public static final long TIMELIMIT = 30 * 60 * 1000;

        public static final String KEY = "gyl-tairan";
    }
}
