package org.trc.biz.jingdong;

import org.trc.domain.jingDong.JingDongAreaTreeNode;

import java.util.List;

/**
 * Created by sone on 2017/6/19.
 */
public interface IJingDongAreaBiz  {
    /**
     * 获得京东地址的json 放入缓存中
     * @return
     * @throws Exception
     */
    List<JingDongAreaTreeNode> getJingDongAreaTree() throws Exception;

    void updateJingDongArea() throws Exception;

}
