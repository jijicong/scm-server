package org.trc.biz.system;

import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.util.AppResult;

import java.util.List;

/**
 * Created by hzwdx on 2017/8/7.
 */
public interface IMetadataBiz {
    /**
     * 数据字典列表查询
     * @return
     */
    List<Dict> queryDict();

    /**
     * 查询地址
     * @return
     */
    List<AreaTreeNode> queryAddress();

    /**
     * 查询京东地址
     * @return
     */
    List<AreaTreeNode> queryJDAddress();

    /**
     * 京东地址更新
     * @return
     */
    AppResult jDAddressUpdate();

}
