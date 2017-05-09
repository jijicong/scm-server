package org.trc.service.util;

import org.trc.domain.util.Serial;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/8.
 */
public interface ISerialUtilService extends IBaseService<Serial,Long> {

    /**
     * 对流水号进行加工
     * @param name
     * @param length
     * @return
     */
    public String getSerilCode(String name,int length);

    public Serial selectSerialByname(String name);

    public  int updateSeralByName(String name,int number,int originalNumber);

}
