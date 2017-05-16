package org.trc.service.util;

import org.trc.domain.util.AreaTreeNode;
import org.trc.domain.util.area;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/6.
 */
public interface ILocationUtilService extends IBaseService<area,Long>{
     List<AreaTreeNode> getTreeNodeFromLocation() throws Exception;
}