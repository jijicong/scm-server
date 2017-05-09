package org.trc.service.util;

import org.trc.domain.util.area;
import org.trc.domain.util.TreeNode;
import org.trc.service.IBaseService;

/**
 * Created by sone on 2017/5/6.
 */
public interface ILocationUtilService extends IBaseService<area,Long>{
    public TreeNode getTreeNodeFromLocation();
}
