package org.trc.service.util;

import org.trc.domain.util.Serial;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/8.
 */
public interface ISerialUtilService extends IBaseService<Serial,Long> {

    /**
     * 生成各个模块的流水号
     * names数组首个元素必须为流水号前缀如：PP（品牌）
     * @param names
     * @param length
     * @return
     */
    String getSerialCode(int length,String ...names) throws Exception;

    Serial selectSerialByName(String name) throws Exception;

    int updateSerialByName(String name,int number,int originalNumber) throws Exception;

}
