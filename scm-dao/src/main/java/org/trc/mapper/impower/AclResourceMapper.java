package org.trc.mapper.impower;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.impower.AclResource;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface AclResourceMapper extends BaseMapper<AclResource>{
        List<AclResource> selectJurisdictionListByCodes(@Param("codes")Long ...code) throws Exception;
}