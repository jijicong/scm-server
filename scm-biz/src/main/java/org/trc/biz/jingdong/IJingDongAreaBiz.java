package org.trc.biz.jingdong;

import org.trc.domain.util.JingDongArea;
import org.trc.domain.util.JingDongAreaTreeNode;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/19.
 */
public interface IJingDongAreaBiz  {

    List<JingDongAreaTreeNode>  getJingDongAreaTree() throws Exception;

    void updateJingDongArea() throws Exception;

}
