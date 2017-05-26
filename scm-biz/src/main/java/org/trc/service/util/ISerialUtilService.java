package org.trc.service.util;

import org.trc.domain.util.Serial;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/8.
 */
public interface ISerialUtilService extends IBaseService<Serial,Long> {

    int selectNumber(String name);

    int updateSerialByName(String name,int number);

    String generateCode(int length,String ...names);

}
