package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierChannelRelation;
import org.trc.domain.supplier.SupplierChannelRelationExt;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierChannelRelationService extends IBaseService<SupplierChannelRelation, Long>{

    /**
     * 查询供应商渠道关系
     * @param map
     * @return
     * @throws Exception
     */
    List<SupplierChannelRelationExt> selectSupplierChannels(Map<String, Object> map) throws Exception;


}
