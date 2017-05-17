package org.trc.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 序列号工具类
 * Created by sone on 2017/5/3.
 */
public  class SerialUtil {
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
        //i += 1;
        String rs = "" + i;
        for (int j = rs.length(); j < len; j++) {
            rs = "0" + rs;
        }
        return rs;
    }
    //根据接受的长度，拼接9 ：列入 len=3   拼3个9
    public  static int jointNineByLen(int len){
        String temp="";
        for (int i=0;i<len;i++){
            temp=temp+"9";
        }
        return  Integer.parseInt(temp);
    }


    public static void main(String[] args) {
       // String test = getMoveOrderNo("QD",5,1);
       // System.out.println(test);
       // System.out.println(jointNineByLen(3));
        List<Long> longs=new ArrayList<>();
        longs.add(11L);
        longs.add(2L);
        List<Long> longs1=new ArrayList<>();
        longs1.add(11L);

        for (Long l1:
                longs) {
            for (Long l2:
                    longs1 ) {
                if(l1==l2){
                    System.out.println(111);
                }
            }
        }
        System.out.println(longs1.contains(longs));
        //System.out.println(11L==11L);
    }
}
