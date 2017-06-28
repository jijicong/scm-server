package org.trc.biz.jingdong;

/**
 * Created by sone on 2017/6/19.
 */
public interface IJingDongAreaBiz  {
    /**
     * 获得京东地址的json 放入缓存中
     * @return
     * @throws Exception
     */
    void getJingDongAreaTree() throws Exception;

    void updateJingDongArea() throws Exception;

}
