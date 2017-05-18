package org.trc.biz.serial;

/**
 * Created by hzwdx on 2017/5/18.
 */
public interface ISerialBiz {

    /**
     * 获取序列号
     * @param module 生成编码的模块名称
     * @return
     * @throws Exception
     */
    String getSerialCode(String module) throws Exception;

}
