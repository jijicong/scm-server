package org.trc.util;

import org.trc.enums.ZeroToNineEnum;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hzcyn on 2018/5/8.
 */
public class DateCheckUtil {

    public static boolean checkDate(Date updateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateTime);
        calendar.add(Calendar.DATE, Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()));
        if(calendar.compareTo(Calendar.getInstance()) == 1){
            return false;
        }
        return true;
    }
}
