package org.trc.mapper.supplier;

import org.trc.domain.supplier.SupplierChannelRelation;
import org.trc.domain.supplier.SupplierChannelRelationExt;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierChannelRelationMapper extends BaseMapper<SupplierChannelRelation>{

    /**
     * 查询供应商渠道关系
     * @param map
     * @return
     * @throws Exception
     */
    List<SupplierChannelRelationExt> selectSupplierChannels(Map<String, Object> map) throws Exception;

}
