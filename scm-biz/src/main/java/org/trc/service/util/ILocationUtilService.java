package org.trc.service.util;

import org.trc.domain.util.Area;
import org.trc.domain.util.AreaTreeNode;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/6.
 */
public interface ILocationUtilService extends IBaseService<Area,Long>{
     List<AreaTreeNode> getTreeNodeFromLocation() throws Exception;
}
