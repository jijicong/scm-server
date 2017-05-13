package org.trc.util;

import java.util.List;

/**
 * Created by sone on 2017/5/12.
 */
public class StringUtil {

    public  static Long[] splitByComma(String str){
        if(str.length()==0 || str==""||str==null){
            return null;
        }
        String[] strs=str.split(",");
        Long[] longs = new Long[strs.length];
        for (int i = 0; i < strs.length; i++) {
            longs[i] = Long.valueOf(strs[i]);
        }
        return longs ;
    }

    public static void main(String[] args) {
        String len="111";
        System.out.println(len.length());
    }
}
