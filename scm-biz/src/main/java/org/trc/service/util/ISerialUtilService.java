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
    String getSerialCode(String name,int length) throws Exception;

    Serial selectSerialByName(String name) throws Exception;

    int updateSerialByName(String name,int number,int originalNumber) throws Exception;

}
