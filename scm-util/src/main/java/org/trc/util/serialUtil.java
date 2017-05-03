package org.trc.util;

/**
 * 序列号工具类
 * Created by sone on 2017/5/3.
 */
public  class serialUtil {
    static {

    }
    /**
     * 产生流水号  默认从1开始
     * @param prefix   前缀
     * @param len      前缀之后的流水号长度
     * @param dataLen  当前的流水长度
     * @return 返回产生的序列号
     */
    public synchronized static String getMoveOrderNo(String prefix,int len,int dataLen) {
        String num = "";
        num += getNo(dataLen,len);
        num = prefix + num;
        return num;
    }

    public static String getNo(int account,int len) {
        int i = account;
        i += 1;
        String rs = "" + i;
        for (int j = rs.length(); j < len; j++) {
            rs = "0" + rs;
        }
        return rs;
    }

    public static void main(String[] args) {
        String test = getMoveOrderNo("QD",5,1);
        System.out.println(test);
    }
}
